#!/usr/bin/env python3
"""Run a synchronized, mixed ten-type UDP comparison on isolated Compose projects."""
import argparse
import csv
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
import re
import signal
import subprocess
import sys
import tempfile
import threading
import time
from urllib.error import HTTPError
from urllib.request import urlopen

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "scripts/tests/output"
DEFAULT_CORPUS = "linux-multitype-adm4-20260930"
TYPES = {
    "bsm": (46800, "topic.OdeRawEncodedBSMJson", "topic.OdeBsmJson", "RawEncodedBSMJsonRouter"),
    "map": (44920, "topic.OdeRawEncodedMAPJson", "topic.OdeMapJson", "RawEncodedMAPJsonRouter"),
    "spat": (44910, "topic.OdeRawEncodedSPATJson", "topic.OdeSpatJson", "RawEncodedSPATJsonRouter"),
    "tim": (47900, "topic.OdeRawEncodedTIMJson", "topic.OdeTimJson", "RawEncodedTIMJsonRouter"),
    "psm": (44940, "topic.OdeRawEncodedPSMJson", "topic.OdePsmJson", "RawEncodedPSMJsonRouter"),
    "srm": (44930, "topic.OdeRawEncodedSRMJson", "topic.OdeSrmJson", "RawEncodedSRMJsonRouter"),
    "ssm": (44900, "topic.OdeRawEncodedSSMJson", "topic.OdeSsmJson", "RawEncodedSSMJsonRouter"),
    "sdsm": (44950, "topic.OdeRawEncodedSDSMJson", "topic.OdeSdsmJson", "RawEncodedSDSMJsonRouter"),
    "rtcm": (44960, "topic.OdeRawEncodedRTCMJson", "topic.OdeRtcmJson", "RawEncodedRTCMJsonRouter"),
    "rsm": (44970, "topic.OdeRawEncodedRSMJson", "topic.OdeRsmJson", "RawEncodedRSMJsonRouter"),
}


# FFM labels map to (ODE_FFM_UDP_ROUTING, ODE_FFM_RAW_PUBLISH). External ADM has no entry.
FFM_ROUTES = {
    "ffm-local": ("raw-topic", True),
    "ffm-direct-raw": ("direct", True),
    "ffm-direct-noraw": ("direct", False),
}


def is_ffm(label):
    return label in FFM_ROUTES


def topology_topics(label):
    topics = ["topic.Asn1DecoderInput", "topic.Asn1DecoderOutput"] if label == "external-adm4" else []
    for _, raw_topic, json_topic, _ in TYPES.values():
        topics.extend([raw_topic, json_topic])
        if is_ffm(label) and raw_topic.startswith("topic."):
            topics.append("dlq." + raw_topic[len("topic."):])
    return list(dict.fromkeys(topics))


def scrub(text):
    text = re.sub(r"(?i)(password|passwd|token|secret|authorization)([=: ]+)\S+",
                  r"\1\2[REDACTED]", text)
    return re.sub(r"(https?://)[^/@\s]+:[^/@\s]+@", r"\1[REDACTED]@", text)


def validate_corpus(corpus_dir, expected_lines):
    failures = []
    hashes = {}
    for name in TYPES:
        path = corpus_dir / f"{name}.packets"
        if not path.is_file():
            failures.append(f"{path}: missing")
            continue
        seen = set()
        line_count = 0
        invalid_hex = False
        duplicate = False
        packet_hash = hashlib.sha256()
        with path.open("rb") as stream:
            for line_count, raw_line in enumerate(stream, 1):
                line = raw_line[:-1] if raw_line.endswith(b"\n") else raw_line
                if line.endswith(b"\r"):
                    line = line[:-1]
                if not re.fullmatch(rb"(?:[0-9a-fA-F]{2})+", line):
                    if not invalid_hex:
                        failures.append(f"{path}:{line_count}: packet is not valid hex")
                        invalid_hex = True
                    continue
                packet = bytes.fromhex(line.decode("ascii"))
                packet_hash.update(packet)
                packet_hash.update(b"\n")
                if packet in seen:
                    if not duplicate:
                        failures.append(f"{path}:{line_count}: duplicate packet")
                        duplicate = True
                    continue
                seen.add(packet)
        if line_count != expected_lines:
            failures.append(f"{path}: expected {expected_lines} lines, found {line_count}")
        hashes[name] = packet_hash.hexdigest()
    if failures:
        raise ValueError("invalid packet corpus: " + "; ".join(failures))
    return hashes


def git_source_identity(source):
    def git(*args, text=True):
        return subprocess.run(["git", *args], cwd=source, text=text,
                              capture_output=True, check=True).stdout

    branch = git("rev-parse", "--abbrev-ref", "HEAD").strip()
    head = git("rev-parse", "HEAD").strip()
    status = git("status", "--porcelain=v1", "--untracked-files=all", text=False)
    diff = git("diff", "HEAD", "--binary", text=False)
    untracked = git("ls-files", "--others", "--exclude-standard", "-z", text=False)
    working_hash = hashlib.sha256(diff)
    for relative_path in untracked.split(b"\0"):
        if not relative_path:
            continue
        path = source / os.fsdecode(relative_path)
        working_hash.update(b"\0untracked\0" + relative_path + b"\0")
        if path.is_symlink():
            working_hash.update(os.fsencode(os.readlink(path)))
        elif path.is_file():
            with path.open("rb") as stream:
                for chunk in iter(lambda: stream.read(1024 * 1024), b""):
                    working_hash.update(chunk)
    return {"branch": branch, "head": head, "dirty": bool(status),
            "tracked_diff_sha256": hashlib.sha256(diff).hexdigest(),
            "working_tree_sha256": working_hash.hexdigest()}


def container_limit_checks(host, cgroup):
    cpu_quota = host.get("CpuQuota", 0)
    cpu_unlimited = cpu_quota in (None, 0, -1)
    checks = {
        "nano_cpus_unlimited": host.get("NanoCpus", 0) in (None, 0),
        "cpu_quota_unlimited": cpu_unlimited,
        "cpu_period_not_limiting": cpu_unlimited or host.get("CpuPeriod", 0) in (None, 0),
        "cpu_shares_default": host.get("CpuShares", 0) in (None, 0),
        "cpu_affinity_unset": not host.get("CpusetCpus") and not host.get("CpusetMems"),
        "memory_limit_unlimited": host.get("Memory", 0) in (None, 0),
        "memory_reservation_unset": host.get("MemoryReservation", 0) in (None, 0),
        "memory_swap_unlimited": host.get("MemorySwap", 0) in (None, 0, -1),
        "cgroup_cpu_unlimited": cgroup.get("cpu.max", "").split()[:1] == ["max"],
        "cgroup_memory_unlimited": cgroup.get("memory.max") == "max",
        "cgroup_memory_high_unlimited": cgroup.get("memory.high") == "max",
        "cgroup_memory_swap_unlimited": cgroup.get("memory.swap.max") == "max",
    }
    return checks


def running_adm_processes(output):
    return [match.group(1) for line in output.splitlines()
            if (match := re.match(r"^(?:asn1codec:)?asn1codec_(\d+)\s+RUNNING(?:\s|$)", line))]


def host_ode_8080_state():
    listener = subprocess.run(["ss", "-ltnH", "( sport = :8080 )"],
                              text=True, capture_output=True)
    tcp_listener = listener.returncode == 0 and bool(listener.stdout.strip())
    health_status = None
    health_http_status = None
    error = None
    try:
        with urlopen("http://127.0.0.1:8080/actuator/health", timeout=2) as response:
            health_http_status = response.status
            health_status = json.loads(response.read().decode("utf-8")).get("status")
    except HTTPError as exception:
        health_http_status = exception.code
        error = f"HTTPError: {exception}"
    except Exception as exception:
        error = f"{type(exception).__name__}: {exception}"
    return {"tcp_listener": tcp_listener, "health_http_status": health_http_status,
            "health_status": health_status, "error": error,
            "listener_check_error": listener.stderr.strip() if listener.returncode else None}


def load_comparison_status(root, selected_modes):
    for mode in selected_modes:
        evidence_dir = root / mode
        if evidence_dir.exists() or evidence_dir.is_symlink():
            raise ValueError(f"refusing to overwrite existing evidence directory: {evidence_dir}")

    status_path = root / "comparison-status.json"
    if status_path.is_symlink():
        raise ValueError(f"refusing to read or overwrite symlinked comparison status: {status_path}")
    if not status_path.exists():
        return []
    if not status_path.is_file():
        raise ValueError(f"comparison status is not a file: {status_path}")
    try:
        manifests = json.loads(status_path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as error:
        raise ValueError(f"cannot read existing comparison status {status_path}: {error}") from error
    if not isinstance(manifests, list) or any(
            not isinstance(entry, dict) or not isinstance(entry.get("mode"), str)
            for entry in manifests):
        raise ValueError(f"existing comparison status has an invalid format: {status_path}")
    recorded_modes = [entry["mode"] for entry in manifests]
    if len(recorded_modes) != len(set(recorded_modes)):
        raise ValueError(f"existing comparison status repeats a mode: {status_path}")
    already_recorded = sorted(set(selected_modes) & set(recorded_modes))
    if already_recorded:
        raise ValueError("refusing to run a mode already recorded in comparison status: "
                         + ", ".join(already_recorded))
    return manifests


class Harness:
    def __init__(self, root, source, label, project, image, args):
        self.root, self.source, self.label, self.project = root, source, label, project
        self.image, self.args = image, args
        self.dir = root / label
        self.dir.mkdir(parents=True, exist_ok=True)
        self.log = (self.dir / "runner.log").open("w", encoding="utf-8")
        self.compose_file = source / "docker-compose.yml"
        self.override = None
        self.services = ["ode", "kafka"] + (["adm"] if label == "external-adm4" else [])
        self.base = ["docker", "compose", "--project-name", project,
                     "--env-file", str(ROOT / ".env"), "--profile", "ode", "--profile", "kafka",
                     "--profile", "kafka_setup", "--profile", "benchmark", "--profile", "adm",
                     "-f", str(self.compose_file)]
        self.base_env = os.environ.copy()
        self.started = time.monotonic()
        self.samples = []
        self.project_start_attempted = False
        ffmlib_match = re.search(
            r"<ffmlib\.version>([^<]+)</ffmlib\.version>",
            (source / "jpo-ode-svcs/pom.xml").read_text(encoding="utf-8"))
        self.runtime = {
            "label": label, "source_tree": str(source), "source_identity": image,
            "created_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
            "settings": {"aggregate_target_messages_per_second": 500,
                "type_count": 10, "per_type_rate": 50, "per_type_count": args.count,
                "warmup_per_type": 1000, "round_robin_phase_step_ms": 2,
                "external_adm_processes": 4 if label == "external-adm4" else None,
                "input_output_partitions": 4 if label == "external-adm4" else 4,
                "ffm_listeners_per_type": 4, "listeners_per_type": 4,
                "ffmlib_version": ffmlib_match.group(1) if ffmlib_match else None,
                "sync_commits": False, "raw_partition_strategy": "round_robin",
                "producer_linger_ms": 0, "linger_ms": 0,
                "compression": "none", "acks": "all",
                "idempotence": True, "host_ode_port": args.port,
                "ffm_udp_routing_and_raw_publish": FFM_ROUTES.get(label),
                "kafka_linger_ms_override": 0, "kafka_compression_override": "none"},
            "vm_capacity": {}, "containers_uncapped_before_traffic": {},
            "corpus_evidence": args.corpus_evidence,
            "sample_errors": [], "runs": {}, "restore": {"compose_down": False}}

    def call(self, name, cmd, *, env=None, timeout=None, check=True):
        result = subprocess.run(cmd, cwd=ROOT, env=env or self.base_env,
                                text=True, capture_output=True, timeout=timeout)
        self.log.write(f"\n[{name}] exit={result.returncode}\n")
        if result.stdout:
            self.log.write(scrub(result.stdout))
        if result.stderr:
            self.log.write(scrub(result.stderr))
        self.log.flush()
        if check and result.returncode:
            raise RuntimeError(f"{name} failed ({result.returncode}): {scrub(result.stderr)[-1500:]}")
        return result

    def compose(self, *args):
        cmd = self.base.copy()
        if self.override:
            cmd += ["-f", str(self.override)]
        return cmd + list(args)

    def make_override(self, directory):
        adm_text = ""
        if self.label == "external-adm4":
            conf = directory / "adm.properties"
            conf_source = self.source / "asn1_codec/config/adm.properties"
            content = conf_source.read_text()
            content = content.replace("group.id=AsnDecode", "group.id=AsnDecodeLatencyBenchmark")
            content = content.replace("auto.offset.reset=smallest", "auto.offset.reset=latest")
            content = content.replace("compression.type=zstd", "compression.type=none")
            conf.write_text(content + "\nlinger.ms=0\nacks=all\nenable.idempotence=true\n")
            supervisor = directory / "supervisord.conf"
            stext = (self.source / "asn1_codec/supervisord.conf").read_text()
            if "-b %(ENV_DOCKER_HOST_IP)s:9092" not in stext:
                raise RuntimeError("expected ADM supervisor bootstrap setting absent")
            supervisor.write_text(stext.replace("-b %(ENV_DOCKER_HOST_IP)s:9092", "-b kafka:9094"))
            adm_text = f'''\n  adm:
    image: jpoode_acm_{self.project}:local
    cpus: !reset null
    mem_limit: !reset null
    mem_reservation: !reset null
    memswap_limit: !reset null
    cpu_quota: !reset null
    cpu_period: !reset null
    cpu_shares: !reset null
    cpuset: !reset null
    deploy:
      resources:
        limits: !reset null
    environment:
      ACM_NUMBER_OF_PROCESSES: "4"
      ADM_NUMBER_OF_PROCESSES: "4"
      ACM_CONFIG_FILE: benchmark-adm.properties
    entrypoint: ["/asn1_codec/run_acm.sh"]
    command: []
    volumes: !override
      - {conf}:/asn1_codec/config/benchmark-adm.properties:ro
      - {supervisor}:/etc/supervisord.conf:ro
'''
        ffm = is_ffm(self.label)
        route, raw_publish = FFM_ROUTES.get(self.label, ("raw-topic", True))
        text = f'''services:
  ode:
    image: {self.image}
    ports: !override ["{self.args.port}:8080"]
    volumes: !override
      - {self.dir}/shared:/jpo-ode
      - {self.dir}/uploads:/home/uploads
    cpus: !reset null
    mem_limit: !reset null
    mem_reservation: !reset null
    memswap_limit: !reset null
    cpu_quota: !reset null
    cpu_period: !reset null
    cpuset: !reset null
    cpu_shares: !reset null
    deploy:
      resources:
        limits: !reset null
    environment:
      ODE_KAFKA_BROKERS: kafka:9094
      ODE_ASN1_CODEC_MODE: {"ffm" if ffm else "external"}
      ODE_FFM_UDP_ROUTING: {route}
      ODE_FFM_RAW_PUBLISH: "{str(raw_publish).lower()}"
      ODE_FFM_LISTENER_CONCURRENCY: "4"
      ODE_FFM_STARTUP_TIMEOUT: 120s
      ODE_FFM_SYNC_COMMITS: "false"
      ODE_FFM_RAW_PARTITION_STRATEGY: round_robin
      KAFKA_LINGER_MS: "0"
      KAFKA_COMPRESSION_TYPE: none
      MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: health,prometheus
  kafka:
    ports: !reset []
    cpus: !reset null
    mem_limit: !reset null
    mem_reservation: !reset null
    memswap_limit: !reset null
    cpu_quota: !reset null
    cpu_period: !reset null
    cpuset: !reset null
    cpu_shares: !reset null
    deploy:
      resources:
        limits: !reset null
  kafka-setup:
    environment:
      KAFKA_BOOTSTRAP_SERVERS: kafka:9094
      KAFKA_TOPIC_PARTITIONS: "4"
  decode-benchmark:
    image: jpoode_decode_benchmark_{self.project}:local
    build:
      context: {ROOT}
      dockerfile: scripts/benchmarks/Dockerfile
    entrypoint: ["python", "/benchmarks/decode_benchmark.py"]
    environment:
      DECODE_BENCHMARK_UDP_HOST: ode
      DECODE_BENCHMARK_METRICS_URL: http://ode:8080/actuator/prometheus
      DECODE_BENCHMARK_HEALTH_URL: http://ode:8080/actuator/health
    volumes: !override
      - {ROOT}/scripts/benchmarks:/benchmarks:ro
      - {ROOT}/scripts/tests:/tests:ro
      - {OUTPUT}:/output
      - {json.dumps(str(self.args.corpus_dir) + ":/corpus:ro")}
{adm_text}'''
        path = directory / "override.yml"
        path.write_text(text)
        self.override = path
        self.call("compose-config-check", self.compose("config", "--format", "json"),
                  check=True)

    def vm_capacity(self):
        cpus = len(os.sched_getaffinity(0)) if hasattr(os, "sched_getaffinity") else os.cpu_count()
        mem = next(int(row.split()[1]) * 1024 for row in Path("/proc/meminfo").read_text().splitlines()
                   if row.startswith("MemTotal:"))
        cgroup_cpu = Path("/sys/fs/cgroup/cpu.max")
        cgroup_mem = Path("/sys/fs/cgroup/memory.max")
        effective_cpus, effective_mem = cpus, mem
        if cgroup_cpu.exists():
            quota, period = cgroup_cpu.read_text().split()
            if quota != "max":
                effective_cpus = min(cpus, int(quota) / int(period))
        if cgroup_mem.exists() and cgroup_mem.read_text().strip() != "max":
            effective_mem = min(mem, int(cgroup_mem.read_text()))
        self.runtime["vm_capacity"] = {"visible_logical_cpus": cpus, "effective_cpu_cores": effective_cpus,
                                        "visible_memory_bytes": mem, "effective_memory_bytes": effective_mem}
        self.runtime["unrelated_ode_8080_preflight"] = host_ode_8080_state()

    def wait_service(self, service, timeout=300):
        end = time.monotonic() + timeout
        while time.monotonic() < end:
            inspect = self.call("inspect-health", self.compose("ps", "-q", service), check=False)
            cid = inspect.stdout.strip()
            if cid:
                state = self.call("inspect-health", ["docker", "inspect", "--format",
                    "{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}", cid], check=False).stdout.strip()
                if state == "healthy" or (service == "adm" and state == "running"):
                    return cid
            time.sleep(2)
        raise TimeoutError(f"{service} did not become healthy")

    def cgroup(self, cid):
        script = "for f in cpu.max memory.max memory.high memory.swap.max memory.current cpu.stat memory.events cpu.pressure memory.pressure cpuset.cpus cpuset.mems cpuset.cpus.effective; do echo __$f; cat /sys/fs/cgroup/$f 2>&1; done"
        output = self.call("cgroup-sample", ["docker", "exec", cid, "sh", "-c", script], check=False).stdout
        result, key = {}, None
        for line in output.splitlines():
            if line.startswith("__"):
                key = line[2:]; result[key] = []
            elif key:
                result[key].append(line.strip())
        for key, value in list(result.items()):
            value = "\n".join(value)
            if key in ("cpu.stat", "memory.events"):
                result[key] = dict((p[0], int(p[1])) for line in value.splitlines() if len(p := line.split()) == 2 and p[1].isdigit())
            elif key in ("memory.current",):
                result[key] = int(value) if value.isdigit() else None
            elif key in ("cpu.pressure", "memory.pressure"):
                parsed = {}
                for line in value.splitlines():
                    words = line.split()
                    if words:
                        vals = dict(x.split("=", 1) for x in words[1:] if "=" in x)
                        parsed[words[0]] = {k: float(v) if k.startswith("avg") else int(v)
                                            for k, v in vals.items()}
                result[key] = parsed
            else:
                result[key] = value
        return result

    def verify_uncapped(self):
        for service in self.services:
            cid = self.wait_service(service, 20)
            inspected = json.loads(self.call("inspect", ["docker", "inspect", cid]).stdout)[0]
            host = inspected["HostConfig"]
            cgroup = self.cgroup(cid)
            limit_checks = container_limit_checks(host, cgroup)
            uncapped = all(limit_checks.values())
            self.runtime["containers_uncapped_before_traffic"][service] = {
                "container_id": cid, "image": inspected["Config"].get("Image"),
                "image_id": inspected.get("Image"), "host_limits": {k: host.get(k) for k in
                    ("NanoCpus", "CpuQuota", "CpuPeriod", "CpuShares", "CpuCount", "CpuPercent",
                     "Memory", "MemoryReservation", "MemorySwap", "CpusetCpus", "CpusetMems")},
                "cgroup_initial": cgroup, "limit_checks": limit_checks, "uncapped": uncapped}
            if not uncapped:
                failed = ", ".join(name for name, passed in limit_checks.items() if not passed)
                raise RuntimeError(f"{service} still has container-level CPU/memory limits: {failed}")
        self.write_runtime()

    def verify_adm_processes(self, timeout=60):
        cid = self.runtime["containers_uncapped_before_traffic"]["adm"]["container_id"]
        command = ["docker", "exec", cid, "supervisorctl", "-c", "/etc/supervisord.conf", "status"]
        deadline = time.monotonic() + timeout
        last_evidence = None
        while time.monotonic() < deadline:
            result = self.call("adm-processes-preflight", command, check=False)
            running = running_adm_processes(result.stdout)
            last_evidence = {
                "container_id": cid, "expected_processes": 4, "command": command,
                "exit_code": result.returncode, "output": scrub(result.stdout),
                "stderr": scrub(result.stderr), "running_processes": running,
                "running_count": len(running),
                "verified_utc": dt.datetime.now(dt.timezone.utc).isoformat()}
            self.runtime["external_adm_process_preflight"] = last_evidence
            if result.returncode == 0 and sorted(running) == ["0", "1", "2", "3"]:
                self.write_runtime()
                return
            time.sleep(2)
        if last_evidence is not None:
            self.runtime["external_adm_process_preflight"] = last_evidence
            self.write_runtime()
        raise TimeoutError("external ADM did not report four running asn1codec processes before traffic")

    def wait_actuator_health(self, timeout=120):
        deadline = time.monotonic() + timeout
        health_url = f"http://127.0.0.1:{self.args.port}/actuator/health"
        prometheus_url = f"http://127.0.0.1:{self.args.port}/actuator/prometheus"
        last_error = "health or Prometheus endpoint was not ready"
        last_evidence = None

        def get_evidence(url):
            status = None
            raw_body = b""
            error_text = None
            try:
                with urlopen(url, timeout=3) as response:
                    status = response.status
                    raw_body = response.read()
            except HTTPError as error:
                status = error.code
                raw_body = error.read()
                error_text = f"HTTPError: {error}"
            except Exception as error:
                error_text = f"{type(error).__name__}: {error}"
            body = raw_body.decode("utf-8", errors="replace")
            return {"url": url, "status": status, "body": body,
                    "body_bytes": len(raw_body), "body_nonempty": bool(raw_body.strip()),
                    "error": error_text}

        while time.monotonic() < deadline:
            health = get_evidence(health_url)
            prometheus = get_evidence(prometheus_url)
            last_evidence = {"checked_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
                             "health": health, "prometheus": prometheus}
            try:
                health_status = json.loads(health["body"]).get("status")
            except (json.JSONDecodeError, AttributeError):
                health_status = None
            health_ok = 200 <= (health["status"] or 0) < 300 and health_status == "UP" and health["body_nonempty"]
            prometheus_ok = (200 <= (prometheus["status"] or 0) < 300
                             and prometheus["body_nonempty"])
            if health_ok and prometheus_ok:
                self.runtime["actuator_endpoint_preflight"] = last_evidence
                self.runtime["actuator_health_preflight"] = {
                    "url": health_url, "status": health_status,
                    "verified_utc": last_evidence["checked_utc"]}
                self.write_runtime()
                return
            last_error = (f"health status={health['status']} body_status={health_status} "
                          f"body_nonempty={health['body_nonempty']}; prometheus status={prometheus['status']} "
                          f"body_nonempty={prometheus['body_nonempty']}")
            time.sleep(1)
        if last_evidence is not None:
            self.runtime["actuator_endpoint_preflight"] = last_evidence
            self.write_runtime()
        raise TimeoutError(f"actuator health did not become UP before traffic: {last_error}")

    def sample_resources(self, done, start):
        prev = {}
        while not done.is_set():
            timestamp = dt.datetime.now(dt.timezone.utc).isoformat()
            for service in self.services:
                try:
                    cid = self.runtime["containers_uncapped_before_traffic"][service]["container_id"]
                    current = self.cgroup(cid)
                    old = prev.get(service)
                    cpu = current.get("cpu.stat", {}).get("usage_usec")
                    interval_cpu = None
                    if old and cpu is not None:
                        dt_s = time.monotonic() - old["monotonic"]
                        prior = old["snapshot"].get("cpu.stat", {}).get("usage_usec")
                        if prior is not None and dt_s:
                            interval_cpu = (cpu-prior)/(dt_s*1e6)*100
                    self.samples.append({"timestamp_utc": timestamp, "elapsed_seconds": time.monotonic()-start,
                        "actual_cadence_seconds": None if not old else time.monotonic()-old["monotonic"],
                        "service": service, "container_id": cid, "cpu_usage_usec": cpu,
                        "interval_cpu_percent": interval_cpu, "memory_current_bytes": current.get("memory.current"),
                        "cpu_max": current.get("cpu.max"), "memory_max": current.get("memory.max"),
                        "memory_high": current.get("memory.high"),
                        "cpu_throttled_usec": current.get("cpu.stat", {}).get("throttled_usec"),
                        "cpu_nr_throttled": current.get("cpu.stat", {}).get("nr_throttled"),
                        "memory_events": current.get("memory.events", {}),
                        "cpu_pressure": current.get("cpu.pressure", {}),
                        "memory_pressure": current.get("memory.pressure", {})})
                    prev[service] = {"snapshot": current, "monotonic": time.monotonic()}
                except Exception as error:
                    self.runtime["sample_errors"].append(f"{service}: {type(error).__name__}: {error}")
            time.sleep(max(0, 1 - (time.monotonic()-start) % 1))

    def run_workload(self):
        starts = time.monotonic() + 90
        measured = starts + 23
        processes = {}
        type_dirs = {name: self.dir / name for name in TYPES}
        for directory in type_dirs.values():
            directory.mkdir(exist_ok=True)
        for index, (name, (port, raw, output, group)) in enumerate(TYPES.items()):
            args = ["run", "--rm", "--no-deps", "decode-benchmark", "--mode",
                    "ffm" if is_ffm(self.label) else "external", "--broker", "kafka:9094",
                    "--packet-file", f"/corpus/{name}.packets", "--message-type", name.upper(),
                    "--udp-host", "ode", "--udp-port", str(port), "--raw-topic", raw,
                    "--json-topic", output, "--consumer-group", group, "--metrics-url",
                    "http://ode:8080/actuator/prometheus", "--health-url", "http://ode:8080/actuator/health",
                    "--count", str(self.args.count), "--warmup", "1000", "--rate", "50",
                    "--warmup-start-monotonic", f"{starts:.9f}", "--measured-start-monotonic",
                    f"{measured:.9f}", "--phase-offset-ms", str(index*2), "--max-p95-ms", "5",
                    "--quiet-period-seconds", "15", "--drain-timeout-seconds", "180", "--output",
                    f"/output/{self.dir.relative_to(OUTPUT.resolve())}/{name}/latency.csv"]
            if is_ffm(self.label):
                route, raw_publish = FFM_ROUTES[self.label]
                args += ["--ingest-route", route] + ([] if raw_publish else ["--no-raw-publish"])
            else:
                args += ["--drain-group", "AsnDecodeLatencyBenchmark:topic.Asn1DecoderInput",
                         "--drain-group", "Asn1DecodedDataRouter:topic.Asn1DecoderOutput"]
            command = self.compose(*args)
            log_path = type_dirs[name] / "runner.log"
            stream = log_path.open("w", encoding="utf-8")
            proc = subprocess.Popen(command, cwd=ROOT, stdout=stream, stderr=subprocess.STDOUT,
                                    env=self.base_env.copy(), text=True)
            processes[name] = (proc, stream)
            self.runtime["runs"][name] = {"container_command": "decode-benchmark (secrets omitted)",
                "per_type_target_rate": 50, "phase_offset_ms": index*2,
                "warmup_start_monotonic": starts, "measured_start_monotonic": measured,
                "latency_csv": str(type_dirs[name]/"latency.csv")}
        self.write_runtime()
        sampler_done = threading.Event()
        sampler = threading.Thread(target=self.sample_resources, args=(sampler_done, starts), daemon=True)
        sampler.start()
        try:
            while any(process.poll() is None for process, _ in processes.values()):
                time.sleep(2)
            codes = {}
            for name, (process, stream) in processes.items():
                codes[name] = process.wait()
                stream.close()
                self.runtime["runs"][name]["exit_code"] = codes[name]
            return codes
        except BaseException:
            for process, _ in processes.values():
                if process.poll() is None:
                    process.terminate()
            raise
        finally:
            sampler_done.set(); sampler.join(timeout=10)
            self.write_resources(); self.write_runtime()

    def write_resources(self):
        path = self.dir / "resource-samples.csv"
        fields = sorted({key for row in self.samples for key in row})
        with path.open("w", newline="", encoding="utf-8") as stream:
            writer = csv.DictWriter(stream, fieldnames=fields); writer.writeheader()
            for row in self.samples:
                writer.writerow({k: json.dumps(v, sort_keys=True) if isinstance(v, (dict,list)) else v
                                 for k,v in row.items()})
        by_service = {}
        for service in self.services:
            rows = [r for r in self.samples if r["service"] == service and r["interval_cpu_percent"] is not None]
            total_dur = sum(r["actual_cadence_seconds"] for r in rows)
            by_service[service] = {"average_cpu_cores": sum(r["interval_cpu_percent"]*r["actual_cadence_seconds"] for r in rows)/100/total_dur if total_dur else None,
                "peak_cpu_cores": max((r["interval_cpu_percent"]/100 for r in rows), default=None),
                "peak_memory_bytes": max((r["memory_current_bytes"] or 0 for r in self.samples if r["service"] == service), default=None),
                "sample_count": len([r for r in self.samples if r["service"] == service]),
                "average_cadence_seconds": sum(r["actual_cadence_seconds"] for r in rows)/len(rows) if rows else None,
                "peak_cpu_throttled_usec_counter": max((r["cpu_throttled_usec"] or 0 for r in self.samples if r["service"] == service), default=None),
                "cgroup_cpu_max": self.runtime["containers_uncapped_before_traffic"].get(service,{}).get("cgroup_initial",{}).get("cpu.max"),
                "cgroup_memory_max": self.runtime["containers_uncapped_before_traffic"].get(service,{}).get("cgroup_initial",{}).get("memory.max")}
        self.runtime["resource_summary"] = by_service

    def write_runtime(self):
        (self.dir / "runtime-evidence.json").write_text(json.dumps(self.runtime, indent=2, default=str))

    def close_project(self):
        if not self.project_start_attempted:
            return
        result = self.call("isolated-project-down", self.compose("down", "--volumes", "--remove-orphans"), check=False, timeout=300)
        before = self.runtime.get("unrelated_ode_8080_preflight")
        after = host_ode_8080_state()
        unchanged = (isinstance(before, dict)
                     and before.get("tcp_listener") == after.get("tcp_listener")
                     and before.get("health_http_status") == after.get("health_http_status")
                     and before.get("health_status") == after.get("health_status"))
        self.runtime["restore"] = {"compose_down": result.returncode == 0,
            "exit_code": result.returncode, "ode_8080_before": before,
            "ode_8080_after": after, "ode_8080_state_unchanged": unchanged}
        self.write_runtime()

    def close(self):
        self.log.close()


def main():
    parser = argparse.ArgumentParser(
        description="Compare FFM with external ADM on the same ODE checkout by default.")
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--corpus-dir", type=Path,
                        help="directory containing the ten per-message-type packet files")
    parser.add_argument("--count", type=int, default=50000)
    parser.add_argument("--port", type=int, default=18080)
    parser.add_argument("--only", choices=("ffm-local", "ffm-direct-raw", "ffm-direct-noraw",
                                           "external-adm4"),
                        help="run one comparison side without repeating the other")
    parser.add_argument("--external-tree", type=Path, default=ROOT,
                        help="ODE source for external ADM; defaults to the current checkout")
    args = parser.parse_args()
    output_base = OUTPUT.resolve()
    root = args.output_dir.resolve()
    try:
        root.relative_to(output_base)
    except ValueError:
        parser.error(f"output directory must resolve inside {output_base}: {root}")

    args.corpus_dir = (args.corpus_dir or OUTPUT / DEFAULT_CORPUS).resolve()
    try:
        packet_hashes = validate_corpus(args.corpus_dir, args.count + 1000)
    except (OSError, ValueError) as error:
        parser.error(str(error))
    corpus_manifest_path = args.corpus_dir / "corpus-manifest.json"
    corpus_manifest = None
    corpus_manifest_sha256 = None
    if corpus_manifest_path.exists():
        try:
            manifest_bytes = corpus_manifest_path.read_bytes()
            corpus_manifest = json.loads(manifest_bytes.decode("utf-8"))
            if not isinstance(corpus_manifest, dict) or not isinstance(corpus_manifest.get("types"), dict):
                parser.error(f"invalid corpus manifest structure: {corpus_manifest_path}")
            if (corpus_manifest.get("codec") != "FFMLib 3.0.0-beta6"
                    or corpus_manifest.get("warmup_per_type") != 1000
                    or corpus_manifest.get("measured_per_type") != args.count
                    or corpus_manifest.get("unique_packets_per_type") != args.count + 1000):
                parser.error(f"corpus manifest does not match the beta6 workload: {corpus_manifest_path}")
            corpus_manifest_sha256 = hashlib.sha256(manifest_bytes).hexdigest()
            manifest_types = corpus_manifest.get("types", {})
            for name, packet_hash in packet_hashes.items():
                manifest_entry = manifest_types.get(name, {})
                if (not isinstance(manifest_entry, dict)
                        or manifest_entry.get("packets") != args.count + 1000
                        or manifest_entry.get("sha256_lines") != packet_hash):
                    parser.error(f"corpus manifest does not match {name}.packets")
        except (OSError, UnicodeError, json.JSONDecodeError) as error:
            parser.error(f"cannot read corpus manifest {corpus_manifest_path}: {error}")
    args.corpus_evidence = {
        "directory": str(args.corpus_dir),
        "manifest_path": str(corpus_manifest_path) if corpus_manifest is not None else None,
        "manifest_sha256": corpus_manifest_sha256,
        "manifest": corpus_manifest,
        "per_type_sha256": packet_hashes,
    }

    if args.count != 50000:
        print("NOTE: non-default per-type count requested", flush=True)

    source_ffm = ROOT
    modes = [
        ("ffm-local", source_ffm, "odecmpffm", "jpoode_ode_odecmpffm:local"),
        ("ffm-direct-raw", source_ffm, "odecmpdirraw", "jpoode_ode_odecmpdirraw:local"),
        ("ffm-direct-noraw", source_ffm, "odecmpdirnoraw", "jpoode_ode_odecmpdirnoraw:local"),
        ("external-adm4", args.external_tree.resolve(), "odecmpadm", "jpoode_ode_odecmpadm:local"),
    ]
    if args.only:
        modes = [mode for mode in modes if mode[0] == args.only]
    try:
        manifests = load_comparison_status(root, {mode[0] for mode in modes})
    except ValueError as error:
        parser.error(str(error))

    source_identities = {}
    identity_cache = {}
    for label, source, _, _ in modes:
        if label == "external-adm4" and not (source / "docker-compose.yml").is_file():
            parser.error(f"external ADM source tree missing: {source}")
        try:
            source_key = source.resolve()
            if source_key not in identity_cache:
                identity_cache[source_key] = git_source_identity(source_key)
            source_identities[label] = identity_cache[source_key]
        except subprocess.CalledProcessError as error:
            parser.error(f"cannot identify git source for {label} at {source}: {scrub(error.stderr or '')}")

    check = subprocess.run(["ss", "-ltn", f"( sport = :{args.port} )"],
                           text=True, capture_output=True)
    if check.returncode:
        parser.error(f"could not check whether requested exposed port {args.port} is occupied: {scrub(check.stderr)}")
    if check.stdout and any(line.strip() for line in check.stdout.splitlines()[1:]):
        parser.error(f"requested exposed port {args.port} is occupied")

    projects = {mode[2] for mode in modes}
    project_list = subprocess.run(["docker", "compose", "ls", "--all", "--quiet"],
                                  cwd=ROOT, text=True, capture_output=True)
    if project_list.returncode:
        parser.error(f"could not check existing Compose projects: {scrub(project_list.stderr)}")
    existing_projects = set(project_list.stdout.splitlines())
    collisions = sorted(projects & existing_projects)
    if collisions:
        parser.error("isolated Compose project name already exists: " + ", ".join(collisions))

    root.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="ode-mixed-compare-") as temp:
        temp = Path(temp)
        for label, source, project, image in modes:
            harness = Harness(root, source, label, project, image, args)
            try:
                harness.vm_capacity()
                harness.runtime["source_identity"] = source_identities[label]
                harness.make_override(temp)
                harness.write_runtime()
                services = ["ode", "kafka-setup", "decode-benchmark"] + (["adm"] if label == "external-adm4" else [])
                build = harness.compose("build", *services)
                if harness.call("build-images", build, timeout=3600).returncode:
                    raise RuntimeError("image build failed")
                # Fresh isolated Kafka and project-scoped volumes; setup creates four partitions.
                up = ["up", "-d", "kafka", "kafka-setup", "ode"]
                harness.project_start_attempted = True
                if harness.call("start-infrastructure", harness.compose(*up), timeout=900).returncode:
                    raise RuntimeError("infrastructure failed to start")
                if label == "external-adm4":
                    harness.call("start-adm", harness.compose("up", "-d", "adm"), timeout=300)
                harness.verify_uncapped()
                if label == "external-adm4":
                    harness.verify_adm_processes()
                harness.wait_actuator_health()
                # Save topology and exact topic partition evidence before traffic.
                kafka = harness.runtime["containers_uncapped_before_traffic"]["kafka"]["container_id"]
                topology = {}
                for topic in topology_topics(label):
                    result = harness.call("topic-"+topic, ["docker", "exec", kafka,
                        "/opt/bitnami/kafka/bin/kafka-topics.sh", "--bootstrap-server", "localhost:9092",
                        "--describe", "--topic", topic], check=False)
                    match = re.search(r"PartitionCount:\s*(\d+)", result.stdout)
                    topology[topic] = int(match.group(1)) if match else None
                    if topology[topic] != 4:
                        raise RuntimeError(f"{topic} has {topology[topic]} partitions; expected 4")
                harness.runtime["topic_partitions"] = topology
                harness.write_runtime()
                codes = harness.run_workload()
                harness.runtime["status"] = "passed" if all(code == 0 for code in codes.values()) else "failed"
                manifests.append({"mode": label, "exit_codes": codes, "runtime": str(harness.dir/"runtime-evidence.json")})
                (root/"comparison-status.json").write_text(json.dumps(manifests, indent=2))
                harness.close_project()
            except BaseException as error:
                harness.runtime["setup_or_execution_failure"] = scrub(f"{type(error).__name__}: {error}")
                harness.write_runtime()
                try:
                    harness.close_project()
                except Exception as cleanup_error:
                    harness.log.write("cleanup failed: "+scrub(str(cleanup_error))+"\n")
                manifests.append({"mode": label, "failure": harness.runtime["setup_or_execution_failure"],
                                  "runtime": str(harness.dir/"runtime-evidence.json")})
                (root/"comparison-status.json").write_text(json.dumps(manifests, indent=2))
                raise
            finally:
                harness.close()
    print(root / "comparison-status.json")


if __name__ == "__main__":
    main()

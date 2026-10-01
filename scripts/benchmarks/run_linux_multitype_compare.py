#!/usr/bin/env python3
"""Run a synchronized, mixed ten-type UDP comparison on isolated Compose projects."""
import argparse
import csv
import datetime as dt
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
from urllib.request import urlopen

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "scripts/tests/output"
CORPUS = "linux-multitype-adm4-20260930"
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


def scrub(text):
    text = re.sub(r"(?i)(password|passwd|token|secret|authorization)([=: ]+)\S+",
                  r"\1\2[REDACTED]", text)
    return re.sub(r"(https?://)[^/@\s]+:[^/@\s]+@", r"\1[REDACTED]@", text)


class Harness:
    def __init__(self, root, source, label, project, image, args):
        self.root, self.source, self.label, self.project = root, source, label, project
        self.image, self.args = image, args
        self.dir = root / label
        self.dir.mkdir(parents=True, exist_ok=True)
        self.log = (self.dir / "runner.log").open("w", encoding="utf-8")
        self.compose_file = source / "docker-compose.yml"
        self.override = None
        self.services = ["ode", "kafka"] + (["adm"] if label == "develop-adm4" else [])
        self.base = ["docker", "compose", "--project-name", project,
                     "--env-file", str(ROOT / ".env"), "--profile", "ode", "--profile", "kafka",
                     "--profile", "kafka_setup", "--profile", "benchmark", "--profile", "adm",
                     "-f", str(self.compose_file)]
        self.base_env = os.environ.copy()
        self.started = time.monotonic()
        self.samples = []
        self.runtime = {
            "label": label, "source_tree": str(source), "source_identity": image,
            "created_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
            "settings": {"aggregate_target_messages_per_second": 500,
                "type_count": 10, "per_type_rate": 50, "per_type_count": args.count,
                "warmup_per_type": 1000, "round_robin_phase_step_ms": 2,
                "external_adm_processes": 4 if label == "develop-adm4" else None,
                "input_output_partitions": 4 if label == "develop-adm4" else 4,
                "producer_linger_ms": 0, "compression": "none", "acks": "all",
                "idempotence": True, "host_ode_port": 18080},
            "vm_capacity": {}, "containers_uncapped_before_traffic": {},
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
        if self.label == "develop-adm4":
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
        text = f'''services:
  ode:
    image: {self.image}
    ports: !override ["18080:8080"]
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
    deploy:
      resources:
        limits: !reset null
    environment:
      ODE_KAFKA_BROKERS: kafka:9094
      ODE_ASN1_CODEC_MODE: {"ffm" if self.label == "ffm-local" else "external"}
      ODE_FFM_LISTENER_CONCURRENCY: "4"
      ODE_FFM_STARTUP_TIMEOUT: 120s
      ODE_FFM_SYNC_COMMITS: "false"
      ODE_FFM_RAW_PARTITION_STRATEGY: round_robin
      ODE_FFM_PRODUCER_LINGER_MS: "0"
      ODE_FFM_PRODUCER_COMPRESSION_TYPE: none
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
        script = "for f in cpu.max memory.max memory.high memory.current cpu.stat memory.events cpu.pressure memory.pressure; do echo __$f; cat /sys/fs/cgroup/$f 2>&1; done"
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
            uncapped = (host.get("NanoCpus", 0) in (0, None) and host.get("CpuQuota", 0) in (0, None)
                        and host.get("Memory", 0) in (0, None) and host.get("MemorySwap", 0) in (0, None)
                        and cgroup.get("cpu.max", "").split()[:1] == ["max"]
                        and cgroup.get("memory.max") == "max" and cgroup.get("memory.high") == "max")
            self.runtime["containers_uncapped_before_traffic"][service] = {
                "container_id": cid, "image": inspected["Config"].get("Image"),
                "image_id": inspected.get("Image"), "host_limits": {k: host.get(k) for k in
                    ("NanoCpus", "CpuQuota", "CpuPeriod", "Memory", "MemorySwap", "CpusetCpus")},
                "cgroup_initial": cgroup, "uncapped": uncapped}
            if not uncapped:
                raise RuntimeError(f"{service} still has container-level CPU/memory limits")
        self.write_runtime()

    def wait_actuator_health(self, timeout=120):
        deadline = time.monotonic() + timeout
        last_error = "health response was not UP"
        while time.monotonic() < deadline:
            try:
                with urlopen("http://127.0.0.1:18080/actuator/health", timeout=3) as response:
                    status = json.loads(response.read().decode("utf-8")).get("status")
                if status == "UP":
                    self.runtime["actuator_health_preflight"] = {
                        "url": "http://127.0.0.1:18080/actuator/health",
                        "status": status, "verified_utc": dt.datetime.now(dt.timezone.utc).isoformat()}
                    self.write_runtime()
                    return
                last_error = f"status={status}"
            except Exception as error:
                last_error = f"{type(error).__name__}: {error}"
            time.sleep(1)
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
                    "ffm" if self.label == "ffm-local" else "external", "--broker", "kafka:9094",
                    "--packet-file", f"/output/{CORPUS}/{name}.packets", "--message-type", name.upper(),
                    "--udp-host", "ode", "--udp-port", str(port), "--raw-topic", raw,
                    "--json-topic", output, "--consumer-group", group, "--metrics-url",
                    "http://ode:8080/actuator/prometheus", "--health-url", "http://ode:8080/actuator/health",
                    "--count", str(self.args.count), "--warmup", "1000", "--rate", "50",
                    "--warmup-start-monotonic", f"{starts:.9f}", "--measured-start-monotonic",
                    f"{measured:.9f}", "--phase-offset-ms", str(index*2), "--max-p95-ms", "5",
                    "--quiet-period-seconds", "15", "--drain-timeout-seconds", "180", "--output",
                    f"/output/{self.dir.relative_to(OUTPUT)}/{name}/latency.csv"]
            if self.label != "ffm-local":
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
        result = self.call("isolated-project-down", self.compose("down", "--volumes", "--remove-orphans"), check=False, timeout=300)
        self.runtime["restore"] = {"compose_down": result.returncode == 0, "exit_code": result.returncode}
        self.write_runtime()

    def close(self):
        self.log.close()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--count", type=int, default=50000)
    parser.add_argument("--port", type=int, default=18080)
    parser.add_argument("--only", choices=("ffm-local", "develop-adm4"),
                        help="run one comparison side without repeating the other")
    parser.add_argument("--develop-tree", type=Path, default=Path("/tmp/ode-develop-20260930"))
    args = parser.parse_args()
    root = args.output_dir.resolve(); root.mkdir(parents=True, exist_ok=True)
    if args.count != 50000:
        print("NOTE: non-default per-type count requested", flush=True)
    missing = [str(OUTPUT/CORPUS/f"{name}.packets") for name in TYPES
               if not (OUTPUT/CORPUS/f"{name}.packets").is_file()]
    if missing:
        parser.error("missing corpus files: " + ", ".join(missing))
    for port in (args.port,):
        check = subprocess.run(["bash", "-lc", f"ss -ltn '( sport = :{port} )' | tail -n +2"],
                               text=True, capture_output=True)
        if check.stdout.strip():
            parser.error(f"requested exposed port {port} is occupied")
    source_ffm = ROOT
    source_dev = args.develop_tree.resolve()
    if not (source_dev/"docker-compose.yml").is_file():
        parser.error(f"develop source snapshot missing: {source_dev}")
    manifests = []
    with tempfile.TemporaryDirectory(prefix="ode-mixed-compare-") as temp:
        temp = Path(temp)
        modes = (
            ("ffm-local", source_ffm, "odecmpffm", "jpoode_ode_odecmpffm:local"),
            ("develop-adm4", source_dev, "odecmpdev", "jpoode_ode_odecmpdev:local"))
        if args.only:
            modes = tuple(mode for mode in modes if mode[0] == args.only)
        for label, source, project, image in modes:
            harness = Harness(root, source, label, project, image, args)
            try:
                harness.vm_capacity()
                if label == "ffm-local":
                    diff = subprocess.run(["git", "diff", "HEAD", "--binary"], cwd=ROOT,
                                          capture_output=True, check=True).stdout
                    harness.runtime["source_identity"] = {"branch": subprocess.run(["git","branch","--show-current"],cwd=ROOT,capture_output=True,text=True).stdout.strip(),
                        "head": subprocess.run(["git","rev-parse","HEAD"],cwd=ROOT,capture_output=True,text=True).stdout.strip(),
                        "tracked_diff_sha256": __import__("hashlib").sha256(diff).hexdigest()}
                else:
                    harness.runtime["source_identity"] = {"branch": "develop", "commit": "eb8b142cdf0a5f66af4ef9d0d33567672d4139f4"}
                harness.make_override(temp)
                harness.write_runtime()
                services = ["ode", "kafka-setup", "decode-benchmark"] + (["adm"] if label == "develop-adm4" else [])
                build = harness.compose("build", *services)
                if harness.call("build-images", build, timeout=3600).returncode:
                    raise RuntimeError("image build failed")
                # Fresh isolated Kafka and project-scoped volumes; setup creates four partitions.
                up = ["up", "-d", "kafka", "kafka-setup", "ode"]
                if harness.call("start-infrastructure", harness.compose(*up), timeout=900).returncode:
                    raise RuntimeError("infrastructure failed to start")
                if label == "develop-adm4":
                    harness.call("start-adm", harness.compose("up", "-d", "adm"), timeout=300)
                harness.verify_uncapped()
                harness.wait_actuator_health()
                # Save topology and exact topic partition evidence before traffic.
                kafka = harness.runtime["containers_uncapped_before_traffic"]["kafka"]["container_id"]
                topics = ["topic.Asn1DecoderInput", "topic.Asn1DecoderOutput"] if label == "develop-adm4" else []
                for name in TYPES:
                    topics.extend([TYPES[name][1], TYPES[name][2]])
                topology = {}
                for topic in dict.fromkeys(topics):
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

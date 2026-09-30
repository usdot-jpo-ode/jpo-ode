#!/usr/bin/env python3
"""Run one uncapped Linux FFMLib workload and restore Compose limits."""

import argparse
import concurrent.futures
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


ROOT = Path(__file__).resolve().parents[2]
OUTPUT_ROOT = ROOT / "scripts/tests/output"
SERVICES = ("ode", "kafka")
SAMPLE_INTERVAL_SECONDS = 1.0


def scrub(value):
    """Remove credential-like values before writing process output to local evidence."""
    value = re.sub(r"(?i)(password|passwd|token|secret|authorization)([=: ]+)\S+",
                   r"\1\2[REDACTED]", value)
    return re.sub(r"(https?://)[^/@\s]+:[^/@\s]+@", r"\1[REDACTED]@", value)


class Runner:
    def __init__(self, output_dir, count=300000, warmup=1000, rate=1000):
        self.services = SERVICES
        self.output_dir = output_dir
        self.count = count
        self.warmup = warmup
        self.rate = rate
        self.log_path = output_dir / "runner.log"
        self.log = self.log_path.open("w", encoding="utf-8")
        self.compose_base = ["docker", "compose", "--profile", "all",
                             "--profile", "benchmark", "-f", str(ROOT / "docker-compose.yml")]
        self.override_path = None
        self.restore_required = False
        self.compose_limits = {}
        self.original_containers = {}
        self.runtime = {"started_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
                        "source": {}, "vm_capacity": {}, "compose_limits": {},
                        "containers_before_traffic": {}, "container_results": {},
                        "sample_target_interval_seconds": SAMPLE_INTERVAL_SECONDS,
                        "sample_errors": [], "benchmark": {"invoked": False},
                        "restore": {"attempted": False, "success": False, "errors": []}}
        self._write_runtime()

    def log_line(self, value):
        self.log.write(scrub(str(value)) + "\n")
        self.log.flush()

    def command(self, label, args, *, check=True, timeout=None, log_output=True):
        completed = subprocess.run(args, cwd=ROOT, text=True, capture_output=True,
                                   timeout=timeout, check=False)
        if log_output and completed.stdout:
            self.log_line(f"[{label}] {completed.stdout.rstrip()}")
        if completed.stderr:
            self.log_line(f"[{label}:stderr] {completed.stderr.rstrip()}")
        if check and completed.returncode:
            raise RuntimeError(f"{label} failed (exit {completed.returncode}): "
                               f"{scrub(completed.stderr.strip())[-1200:]}")
        return completed

    def command_stream(self, label, args, *, timeout=None):
        self.log_line(f"[{label}] started")
        with self.log_path.open("a", encoding="utf-8") as stream:
            process = subprocess.Popen(args, cwd=ROOT, stdout=subprocess.PIPE,
                                       stderr=subprocess.STDOUT, text=True, bufsize=1)

            def copy_sanitized_output():
                for line in process.stdout:
                    stream.write(scrub(line))
                    stream.flush()

            output_thread = threading.Thread(target=copy_sanitized_output, daemon=True)
            output_thread.start()
            try:
                result = process.wait(timeout=timeout)
            except subprocess.TimeoutExpired as error:
                process.terminate()
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()
                output_thread.join(timeout=10)
                raise TimeoutError(f"{label} exceeded its {timeout}-second timeout") from error
            except BaseException:
                process.terminate()
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()
                raise
            output_thread.join(timeout=10)
            if output_thread.is_alive():
                process.stdout.close()
                raise RuntimeError(f"{label} output stream did not close")
        self.log_line(f"[{label}] finished with exit {result}")
        return result

    def compose(self, *args, override=False):
        command = list(self.compose_base)
        if override and self.override_path:
            command.extend(["-f", str(self.override_path)])
        command.extend(args)
        return command

    def inspect_container(self, service):
        ids = self.command(f"inspect-{service}", self.compose("ps", "-aq", service)) \
            .stdout.strip().splitlines()
        if len(ids) != 1:
            raise RuntimeError(f"expected one {service} container, found {len(ids)}")
        raw = self.command(f"inspect-{service}", ["docker", "inspect", ids[0]],
                           log_output=False).stdout
        inspected = json.loads(raw)[0]
        host = inspected.get("HostConfig", {})
        config = inspected.get("Config", {})
        state = inspected.get("State", {})
        return {
            "id": ids[0], "image_tag": config.get("Image"),
            "image_id": inspected.get("Image"),
            "health": state.get("Health", {}).get("Status", state.get("Status")),
            "status": state.get("Status"),
            "limits": {key: host.get(key, 0) for key in (
                "NanoCpus", "CpuQuota", "CpuPeriod", "Memory", "MemorySwap",
                "CpuShares", "CpusetCpus", "CpusetMems")},
        }

    def compose_config(self):
        result = self.command("compose-config", self.compose("config", "--format", "json"),
                              log_output=False)
        config = json.loads(result.stdout)
        for service in self.services:
            definition = config["services"][service]
            resources = definition.get("deploy", {}).get("resources", {})
            limits = resources.get("limits", {}) or {}
            self.compose_limits[service] = {key: limits.get(key) for key in ("cpus", "memory")}
        self.runtime["compose_limits"] = self.compose_limits
        environment = config["services"]["ode"].get("environment", {})
        expected_settings = {
            "ODE_ASN1_CODEC_MODE": "ffm",
            "ODE_FFM_LISTENER_CONCURRENCY": "4",
            "ODE_FFM_STARTUP_TIMEOUT": "120s",
            "ODE_FFM_SYNC_COMMITS": "false",
            "ODE_FFM_RAW_PARTITION_STRATEGY": "round_robin",
            "ODE_FFM_PRODUCER_LINGER_MS": "0",
            "ODE_FFM_PRODUCER_COMPRESSION_TYPE": "none",
        }
        actual_settings = {key: environment.get(key) for key in expected_settings}
        self.runtime["ffmlib_settings"] = actual_settings
        mismatches = [key for key, expected in expected_settings.items()
                      if str(actual_settings.get(key)).lower() != expected.lower()]
        self.runtime["ffmlib_producer_guarantees"] = {
            "acks": "all", "enable_idempotence": True,
            "confirmed_output_before_raw_ack": True,
        }
        if mismatches:
            raise RuntimeError("Compose FFMLib settings do not match the requested workload: "
                               + ", ".join(mismatches))

    def source_identity(self):
        branch = self.command("source-branch", ["git", "branch", "--show-current"]).stdout.strip()
        commit = self.command("source-commit", ["git", "rev-parse", "HEAD"]).stdout.strip()
        diff = self.command("source-diff", ["git", "diff", "HEAD", "--binary"],
                            log_output=False).stdout
        status = self.command("source-status", ["git", "status", "--porcelain"]).stdout
        untracked = self.command("source-untracked", ["git", "ls-files", "--others",
            "--exclude-standard", "-z"], log_output=False).stdout.split("\0")
        untracked_hashes = {}
        for name in untracked:
            if name:
                untracked_hashes[name] = hashlib.sha256((ROOT / name).read_bytes()).hexdigest()
        self.runtime["source"] = {"branch": branch, "commit": commit,
            "working_tree_modified": bool(status.strip()),
            "tracked_diff_sha256": hashlib.sha256(diff.encode("utf-8")).hexdigest(),
            "untracked_file_sha256": untracked_hashes}

    def vm_capacity(self):
        try:
            cpu_count = len(os.sched_getaffinity(0))
        except AttributeError:
            cpu_count = os.cpu_count()
        mem_total = None
        for line in Path("/proc/meminfo").read_text(encoding="utf-8").splitlines():
            if line.startswith("MemTotal:"):
                mem_total = int(line.split()[1]) * 1024
                break
        effective_cpus = cpu_count
        host_cpu_max = Path("/sys/fs/cgroup/cpu.max")
        if host_cpu_max.exists():
            quota, period = host_cpu_max.read_text().split()
            if quota != "max":
                effective_cpus = min(effective_cpus, int(quota) / int(period))
        host_memory_max = Path("/sys/fs/cgroup/memory.max")
        effective_memory = mem_total
        if host_memory_max.exists():
            limit = host_memory_max.read_text().strip()
            if limit != "max":
                effective_memory = min(effective_memory, int(limit))
        self.runtime["vm_capacity"] = {
            "logical_cpus_visible": cpu_count, "effective_cpu_cores": effective_cpus,
            "memory_bytes_visible": mem_total, "effective_memory_bytes": effective_memory,
        }

    def check_original_stack(self):
        for service in self.services:
            state = self.inspect_container(service)
            if state["health"] != "healthy":
                raise RuntimeError(f"{service} is not healthy before the uncapped run")
            self.original_containers[service] = state
        for service in self.services:
            self.runtime["containers_before_traffic"][service] = self.original_containers[service]
        self._write_runtime()

    def build_images(self):
        exit_code = self.command_stream("build-images",
            self.compose("build", "ode", "decode-benchmark"), timeout=1800)
        if exit_code:
            raise RuntimeError(f"Compose image build failed (exit {exit_code})")

    def make_override(self, directory):
        path = Path(directory) / "uncapped.override.yml"
        reset = """services:
  ode:
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
  kafka:
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
"""
        path.write_text(reset, encoding="utf-8")
        self.override_path = path
        result = self.command("validate-uncapped-compose",
            self.compose("config", "--format", "json", override=True), log_output=False)
        config = json.loads(result.stdout)
        checks = {}
        for service in self.services:
            definition = config["services"][service]
            hard_limits = definition.get("deploy", {}).get("resources", {}).get("limits") or {}
            direct_limits = {key: definition.get(key) for key in (
                "cpus", "mem_limit", "mem_reservation", "memswap_limit", "cpu_quota",
                "cpuset") if definition.get(key) not in (None, 0, "0")}
            checks[service] = {"deploy_limits": hard_limits, "direct_limits": direct_limits}
        self.runtime["uncapped_override_config"] = checks
        if any(item["deploy_limits"] or item["direct_limits"] for item in checks.values()):
            raise RuntimeError("temporary Compose override did not remove every configured limit")

    def wait_healthy(self, service, timeout=240):
        deadline = time.monotonic() + timeout
        last_status = None
        while time.monotonic() < deadline:
            state = self.inspect_container(service)
            last_status = state["health"]
            if last_status == "healthy":
                return state
            if state["status"] in ("exited", "dead"):
                break
            time.sleep(2)
        raise RuntimeError(f"{service} did not become healthy (last status {last_status})")

    def cgroup_snapshot(self, container_id):
        script = "for f in cpu.max cpuset.cpus memory.max memory.high memory.current " \
            "memory.swap.max " \
            "cpu.stat memory.events cpu.pressure memory.pressure; do " \
            "echo __FILE__$f; cat /sys/fs/cgroup/$f 2>&1; done"
        output = self.command("cgroup-read", ["docker", "exec", container_id,
            "sh", "-c", script], log_output=False).stdout
        files = {}
        current = None
        for line in output.splitlines():
            if line.startswith("__FILE__"):
                current = line[len("__FILE__"):]
                files[current] = []
            elif current is not None:
                files[current].append(line.strip())
        parsed = {name: "\n".join(values) for name, values in files.items()}
        for name in ("cpu.stat", "memory.events"):
            parsed[name] = parse_kv(parsed.get(name, ""))
        for name in ("cpu.pressure", "memory.pressure"):
            parsed[name] = parse_pressure(parsed.get(name, ""))
        for name in ("memory.current",):
            try:
                parsed[name] = int(parsed[name])
            except (KeyError, ValueError):
                parsed[name] = None
        return parsed

    def health_status(self, container_id):
        template = "{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}"
        result = self.command("container-health", ["docker", "inspect", "--format", template,
            container_id], log_output=False)
        return result.stdout.strip()

    def uncapped_state(self, service, container):
        limits = container["limits"]
        cgroup = self.cgroup_snapshot(container["id"])
        errors = []
        for name in ("NanoCpus", "CpuQuota", "Memory", "MemorySwap"):
            if limits[name] not in (None, 0, -1):
                errors.append(f"HostConfig.{name}={limits[name]}")
        if limits["CpusetCpus"]:
            errors.append("HostConfig.CpusetCpus is set")
        if limits["CpusetMems"]:
            errors.append("HostConfig.CpusetMems is set")
        if cgroup.get("cpu.max", "").split()[:1] != ["max"]:
            errors.append(f"cgroup cpu.max={cgroup.get('cpu.max')}")
        if cgroup.get("memory.max") != "max":
            errors.append(f"cgroup memory.max={cgroup.get('memory.max')}")
        if cgroup.get("memory.high") != "max":
            errors.append(f"cgroup memory.high={cgroup.get('memory.high')}")
        if cgroup.get("memory.swap.max") != "max":
            errors.append(f"cgroup memory.swap.max={cgroup.get('memory.swap.max')}")
        result = {"container": container, "cgroup": cgroup, "uncapped": not errors,
                  "verification_errors": errors}
        self.runtime["containers_before_traffic"][service] = result
        return result

    def set_uncapped(self):
        # Set before the first recreate so every caught failure enters restoration.
        self.restore_required = True
        self.command_stream("recreate-kafka-uncapped", self.compose(
            "up", "-d", "--no-deps", "--force-recreate", "kafka", override=True), timeout=300)
        self.wait_healthy("kafka")
        self.command_stream("recreate-ode-uncapped", self.compose(
            "up", "-d", "--no-deps", "--force-recreate", "ode", override=True), timeout=600)
        self.wait_healthy("ode")
        verified = {}
        for service in self.services:
            container = self.inspect_container(service)
            result = self.uncapped_state(service, container)
            verified[service] = result
        self.runtime["containers_before_traffic"] = verified
        self._write_runtime()
        errors = [f"{service}: {', '.join(item['verification_errors'])}"
                  for service, item in verified.items() if not item["uncapped"]]
        if errors:
            raise RuntimeError("container uncapping verification failed: " + "; ".join(errors))

    def sample_one(self, service, state):
        snapshot = self.cgroup_snapshot(state["id"])
        snapshot["health_status"] = self.health_status(state["id"])
        return snapshot

    def workload_command(self, benchmark_csv_path):
        return self.compose("run", "--rm", "--no-deps", "decode-benchmark",
            "--mode", "ffm", "--broker", "kafka:9094",
            "--fixture", "/tests/udpsender_bsm.py", "--udp-host", "ode",
            "--udp-port", "46800", "--metrics-url", "http://ode:8080/actuator/prometheus",
            "--health-url", "http://ode:8080/actuator/health", "--count", str(self.count),
            "--warmup", str(self.warmup), "--rate", str(self.rate), "--max-p95-ms", "5",
            "--quiet-period-seconds", "15", "--drain-timeout-seconds", "120",
            "--output", "/output/" + benchmark_csv_path.parent.name + "/"
                + benchmark_csv_path.name)

    def collect_workload(self, benchmark_csv_path, resource_samples_path):
        states = {service: self.inspect_container(service) for service in self.services}
        previous = {service: self.sample_one(service, states[service]) for service in self.services}
        baselines = previous.copy()
        previous_time = time.monotonic()
        last_success_time = {service: previous_time for service in self.services}
        scheduled = previous_time + SAMPLE_INTERVAL_SECONDS
        rows = []
        totals = {service: {"cpu_usage_usec": 0, "cpu_interval_percent": [],
                            "peak_memory_bytes": 0, "cpu_throttled_usec": 0,
                            "nr_throttled": 0, "nr_periods": 0,
                            "memory_pressure_some_total": 0,
                            "memory_pressure_full_total": 0,
                            "memory_events_high": 0, "memory_events_max": 0,
                            "memory_events_oom": 0, "memory_events_oom_kill": 0,
                            "health_status_samples": 0, "unhealthy_samples": 0,
                            "errors": []} for service in self.services}
        command = self.workload_command(benchmark_csv_path)
        self.runtime["benchmark"] = {"invoked": True, "mode": getattr(self, "mode", "ffm"), "count": self.count,
            "warmup": self.warmup, "rate_per_second": self.rate,
            "output_csv": str(benchmark_csv_path),
            "resource_samples_csv": str(resource_samples_path)}
        self._write_runtime()
        with self.log_path.open("a", encoding="utf-8") as stream:
            process = subprocess.Popen(command, cwd=ROOT, stdout=subprocess.PIPE,
                                       stderr=subprocess.STDOUT, text=True, bufsize=1)

            def copy_sanitized_output():
                for line in process.stdout:
                    stream.write(scrub(line))
                    stream.flush()

            output_thread = threading.Thread(target=copy_sanitized_output, daemon=True)
            output_thread.start()
            self.runtime["benchmark"]["process_started_utc"] = \
                dt.datetime.now(dt.timezone.utc).isoformat()
            start = time.monotonic()
            try:
                while process.poll() is None:
                    now = time.monotonic()
                    if now < scheduled:
                        time.sleep(scheduled - now)
                    sample_time = time.monotonic()
                    cadence = sample_time - previous_time
                    snapshots = {}
                    sample_errors = {}
                    with concurrent.futures.ThreadPoolExecutor(
                            max_workers=len(self.services)) as pool:
                        futures = {pool.submit(self.sample_one, service, states[service]): service
                                   for service in self.services}
                        for future, service in futures.items():
                            try:
                                snapshots[service] = future.result()
                            except Exception as error:
                                message = f"{service} sample: {type(error).__name__}: {error}"
                                totals[service]["errors"].append(message)
                                self.runtime["sample_errors"].append(message)
                                sample_errors[service] = message
                                snapshots[service] = {}
                    for service in self.services:
                        interval_elapsed = sample_time - last_success_time[service]
                        row, usage, interval_percent = sample_row(service, states[service],
                            snapshots[service], previous[service], interval_elapsed,
                            sample_time - start, cadence, sample_errors.get(service))
                        rows.append(row)
                        totals[service]["health_status_samples"] += 1
                        if row["health_status"] != ("running" if service == "adm" else "healthy"):
                            totals[service]["unhealthy_samples"] += 1
                        if service not in sample_errors:
                            aggregate_sample(totals[service], snapshots[service], usage,
                                             interval_percent, interval_elapsed)
                            previous[service] = snapshots[service]
                            last_success_time[service] = sample_time
                    previous_time = sample_time
                    scheduled = max(scheduled + SAMPLE_INTERVAL_SECONDS,
                                    sample_time + SAMPLE_INTERVAL_SECONDS)
            except BaseException:
                process.terminate()
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()
                output_thread.join(timeout=10)
                raise
            exit_code = process.wait()
            output_thread.join(timeout=10)
            if output_thread.is_alive():
                process.stdout.close()
                raise RuntimeError("benchmark output stream did not close")
            self.runtime["benchmark"]["exit_code"] = exit_code
            self.runtime["benchmark"]["process_finished_utc"] = \
                dt.datetime.now(dt.timezone.utc).isoformat()
        self.write_samples(resource_samples_path, rows)
        self.runtime["resource_sampling"] = {"target_interval_seconds": SAMPLE_INTERVAL_SECONDS,
            "containers": {service: {
                "sample_count": len([row for row in rows if row["service"] == service]),
                "average_cadence_seconds": average(
                    [row["actual_cadence_seconds"] for row in rows
                     if row["service"] == service]),
                "maximum_cadence_seconds": max(
                    [row["actual_cadence_seconds"] for row in rows
                     if row["service"] == service], default=None)} for service in self.services}}
        elapsed = max(0.001, time.monotonic() - start)
        for service in self.services:
            set_counter_deltas(totals[service], baselines[service], previous[service])
            totals[service]["average_cpu_percent"] = \
                totals[service]["cpu_usage_usec"] / (
                    max(totals[service]["cpu_sampled_duration_seconds"], 0.001)
                    * 1_000_000) * 100
            totals[service]["average_cpu_cores"] = totals[service]["average_cpu_percent"] / 100
            totals[service]["cpu_sampled_coverage_percent"] = \
                min(100.0, totals[service]["cpu_sampled_duration_seconds"] / elapsed * 100)
            totals[service]["peak_cpu_percent"] = \
                max(totals[service]["cpu_interval_percent"], default=0.0)
            totals[service]["peak_cpu_cores"] = totals[service]["peak_cpu_percent"] / 100
            totals[service]["cpu_interval_percent"].clear()
        health_failures = {service: result["unhealthy_samples"] for service, result in totals.items()
                           if result["unhealthy_samples"]}
        self.runtime["service_health_failures_during_workload"] = health_failures
        self.runtime["container_results"] = totals
        self._write_runtime()
        if exit_code == 0 and health_failures:
            return 3
        return exit_code

    def write_samples(self, path, rows):
        fields = ["timestamp_utc", "elapsed_seconds", "actual_cadence_seconds", "service",
            "container_id", "image_tag", "image_id", "interval_cpu_percent",
            "cumulative_cpu_usage_usec", "memory_current_bytes", "memory_max_bytes",
            "memory_high_bytes", "memory_swap_max_bytes", "cpu_max", "cpuset_cpus", "cpu_nr_periods",
            "cpu_nr_throttled", "cpu_throttled_usec",
            "cpu_pressure_some_avg10", "cpu_pressure_some_total",
            "memory_pressure_some_avg10", "memory_pressure_some_total",
            "memory_pressure_full_avg10", "memory_pressure_full_total", "memory_high_events",
            "memory_max_events", "memory_oom_events", "memory_oom_kill_events", "health_status",
            "sample_error"]
        with path.open("w", newline="", encoding="utf-8") as stream:
            writer = csv.DictWriter(stream, fieldnames=fields)
            writer.writeheader()
            writer.writerows(rows)

    def restore(self):
        if not self.restore_required:
            return
        self.runtime["restore"]["attempted"] = True
        try:
            kafka_exit = self.command_stream("restore-kafka-compose-limits", self.compose(
                "up", "-d", "--no-deps", "--force-recreate", "kafka"), timeout=300)
            if kafka_exit:
                raise RuntimeError(f"Compose returned {kafka_exit} restoring Kafka")
            ode_exit = self.command_stream("restore-ode-compose-limits", self.compose(
                "up", "-d", "--no-deps", "--force-recreate", "ode"), timeout=600)
            if ode_exit:
                raise RuntimeError(f"Compose returned {ode_exit} restoring ODE")
            after = {service: self.inspect_container(service) for service in self.services}
            self.runtime["restore"]["containers"] = after
            mismatches = []
            for service in self.services:
                if after[service]["limits"] != self.original_containers[service]["limits"]:
                    mismatches.append(f"{service} container limits differ from their original values")
            if mismatches:
                raise RuntimeError("; ".join(mismatches))
            self.runtime["restore"]["limits_restored"] = True
            unhealthy = []
            for service in self.services:
                try:
                    self.wait_healthy(service)
                except RuntimeError:
                    unhealthy.append(service)
            self.runtime["restore"]["services_healthy"] = not unhealthy
            if unhealthy:
                self.runtime["restore"]["health_errors"] = unhealthy
            self.runtime["restore"]["success"] = True
        except Exception as error:
            self.runtime["restore"]["errors"].append(
                f"{type(error).__name__}: {scrub(str(error))}")
            try:
                restored = {}
                for service in self.services:
                    original = self.original_containers[service]["limits"]
                    current = self.inspect_container(service)
                    update = ["docker", "update"]
                    if original["NanoCpus"]:
                        update += ["--cpus", str(original["NanoCpus"] / 1_000_000_000)]
                    elif original["CpuQuota"] and original["CpuPeriod"]:
                        update += ["--cpu-quota", str(original["CpuQuota"]),
                                   "--cpu-period", str(original["CpuPeriod"])]
                    if original["Memory"]:
                        update += ["--memory", str(original["Memory"])]
                    if original["MemorySwap"]:
                        update += ["--memory-swap", str(original["MemorySwap"])]
                    if original["CpusetCpus"]:
                        update += ["--cpuset-cpus", original["CpusetCpus"]]
                    self.command(f"fallback-restore-{service}", update + [current["id"]])
                    restored[service] = self.inspect_container(service)
                    if restored[service]["limits"] != original:
                        raise RuntimeError(f"fallback did not restore {service} limits")
                self.runtime["restore"]["fallback_containers"] = restored
                self.runtime["restore"]["limits_restored"] = True
                unhealthy = []
                for service in self.services:
                    try:
                        self.wait_healthy(service)
                    except RuntimeError:
                        unhealthy.append(service)
                self.runtime["restore"]["services_healthy"] = not unhealthy
                if unhealthy:
                    self.runtime["restore"]["health_errors"] = unhealthy
                self.runtime["restore"]["success"] = True
            except Exception as fallback_error:
                self.runtime["restore"]["errors"].append(
                    f"fallback {type(fallback_error).__name__}: {scrub(str(fallback_error))}")
        finally:
            self.runtime["finished_utc"] = dt.datetime.now(dt.timezone.utc).isoformat()
            self._write_runtime()

    def _write_runtime(self):
        path = self.output_dir / "runtime-evidence.json"
        path.write_text(json.dumps(self.runtime, indent=2), encoding="utf-8")

    def close(self):
        self.log.close()


def parse_kv(value):
    result = {}
    for line in value.splitlines():
        parts = line.split()
        if len(parts) == 2:
            try:
                result[parts[0]] = int(parts[1])
            except ValueError:
                continue
    return result


def average(values):
    return sum(values) / len(values) if values else None


def parse_pressure(value):
    result = {}
    for line in value.splitlines():
        parts = line.split()
        if not parts:
            continue
        entry = {}
        for field in parts[1:]:
            if "=" in field:
                key, raw = field.split("=", 1)
                try:
                    entry[key] = float(raw) if key.startswith("avg") else int(raw)
                except ValueError:
                    continue
        result[parts[0]] = entry
    return result


def sample_row(service, identity, current, previous, elapsed, run_elapsed, cadence,
               sample_error=None):
    cpu = current.get("cpu.stat", {})
    old_cpu = previous.get("cpu.stat", {})
    usage_delta = max(0, cpu.get("usage_usec", 0) - old_cpu.get("usage_usec", 0))
    interval_percent = usage_delta / max(elapsed, 0.001) / 10_000
    memory = current.get("memory.current") or 0
    events = current.get("memory.events", {})
    cpsi = current.get("cpu.pressure", {})
    mpsi = current.get("memory.pressure", {})
    row = {"timestamp_utc": dt.datetime.now(dt.timezone.utc).isoformat(),
        "elapsed_seconds": round(run_elapsed, 3), "actual_cadence_seconds": round(cadence, 3),
        "service": service, "container_id": identity["id"],
        "health_status": current.get("health_status"),
        "image_tag": identity["image_tag"], "image_id": identity["image_id"],
        "interval_cpu_percent": round(interval_percent, 3),
        "cumulative_cpu_usage_usec": cpu.get("usage_usec"),
        "memory_current_bytes": memory,
        "memory_max_bytes": current.get("memory.max"), "cpu_max": current.get("cpu.max"),
        "memory_high_bytes": current.get("memory.high"),
        "memory_swap_max_bytes": current.get("memory.swap.max"),
        "cpuset_cpus": current.get("cpuset.cpus"),
        "cpu_nr_periods": cpu.get("nr_periods"),
        "cpu_nr_throttled": cpu.get("nr_throttled"),
        "cpu_throttled_usec": cpu.get("throttled_usec"),
        "cpu_pressure_some_avg10": cpsi.get("some", {}).get("avg10"),
        "cpu_pressure_some_total": cpsi.get("some", {}).get("total"),
        "memory_pressure_some_avg10": mpsi.get("some", {}).get("avg10"),
        "memory_pressure_some_total": mpsi.get("some", {}).get("total"),
        "memory_pressure_full_avg10": mpsi.get("full", {}).get("avg10"),
        "memory_pressure_full_total": mpsi.get("full", {}).get("total"),
        "memory_high_events": events.get("high"), "memory_max_events": events.get("max"),
        "memory_oom_events": events.get("oom"), "memory_oom_kill_events": events.get("oom_kill"),
        "sample_error": sample_error}
    return row, usage_delta, interval_percent


def aggregate_sample(result, current, usage_delta, interval_percent, interval_seconds):
    result["cpu_usage_usec"] += usage_delta
    result["cpu_interval_percent"].append(interval_percent)
    result["cpu_sampled_duration_seconds"] = \
        result.get("cpu_sampled_duration_seconds", 0.0) + interval_seconds
    result["peak_memory_bytes"] = max(result["peak_memory_bytes"],
                                       current.get("memory.current") or 0)


def set_counter_deltas(result, baseline, final):
    base_cpu = baseline.get("cpu.stat", {})
    final_cpu = final.get("cpu.stat", {})
    result["cpu_throttled_usec"] = max(0, final_cpu.get("throttled_usec", 0)
                                       - base_cpu.get("throttled_usec", 0))
    result["nr_throttled"] = max(0, final_cpu.get("nr_throttled", 0)
                                 - base_cpu.get("nr_throttled", 0))
    result["nr_periods"] = max(0, final_cpu.get("nr_periods", 0)
                               - base_cpu.get("nr_periods", 0))
    result["cpu_pressure_some_total"] = max(0,
        final.get("cpu.pressure", {}).get("some", {}).get("total", 0)
        - baseline.get("cpu.pressure", {}).get("some", {}).get("total", 0))
    for event in ("high", "max", "oom", "oom_kill"):
        result["memory_events_" + event] = max(0,
            final.get("memory.events", {}).get(event, 0)
            - baseline.get("memory.events", {}).get(event, 0))
    for level in ("some", "full"):
        name = "memory_pressure_" + level + "_total"
        result[name] = max(0,
            final.get("memory.pressure", {}).get(level, {}).get("total", 0)
            - baseline.get("memory.pressure", {}).get(level, {}).get("total", 0))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--count", type=int, default=300000)
    parser.add_argument("--warmup", type=int, default=1000)
    parser.add_argument("--rate", type=int, default=1000)
    parser.add_argument("--label", choices=("compact-before", "compact-after"))
    args = parser.parse_args()
    if args.count <= 0 or args.warmup < 0 or args.rate <= 0:
        parser.error("count and rate must be positive and warmup must be nonnegative")
    stamp = dt.datetime.now().strftime("%Y%m%d-%H%M%S")
    output_dir = OUTPUT_ROOT / f"linux-{args.label}-{stamp}" if args.label \
        else OUTPUT_ROOT / f"linux-{stamp}"
    output_dir.mkdir(parents=True, exist_ok=False)
    csv_path = output_dir / f"linux-{stamp}.csv"
    resource_samples_path = output_dir / "resource-samples.csv"
    runner = Runner(output_dir, count=args.count, warmup=args.warmup, rate=args.rate)
    def interrupt(signum, _frame):
        raise KeyboardInterrupt(f"received signal {signum}")

    for signum in (signal.SIGINT, signal.SIGTERM):
        signal.signal(signum, interrupt)
    exit_code = 1
    try:
        runner.source_identity()
        runner.vm_capacity()
        runner.compose_config()
        runner.check_original_stack()
        runner.build_images()
        with tempfile.TemporaryDirectory(prefix="ffmlib-uncapped-") as temporary:
            runner.make_override(temporary)
            runner.set_uncapped()
            exit_code = runner.collect_workload(csv_path, resource_samples_path)
    except KeyboardInterrupt as error:
        runner.runtime["setup_or_run_failure"] = str(error)
        runner.log_line(error)
    except Exception as error:
        runner.runtime["setup_or_run_failure"] = f"{type(error).__name__}: {scrub(str(error))}"
        runner.log_line(runner.runtime["setup_or_run_failure"])
    finally:
        runner.restore()
        runner._write_runtime()
        runner.close()
    if not runner.runtime["restore"]["success"] and runner.restore_required:
        exit_code = 2
    print(json.dumps({"output_directory": str(output_dir),
                      "benchmark": runner.runtime["benchmark"],
                      "restore": runner.runtime["restore"],
                      "failure": runner.runtime.get("setup_or_run_failure")}, indent=2))
    return exit_code


if __name__ == "__main__":
    raise SystemExit(main())

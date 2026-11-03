#!/usr/bin/env python3
"""Summarize FFM route and External ADM mixed-message evidence with route-aware gates.

Reuses parsing, percentile, and resource helpers from summarize_linux_multitype_mixed.py. A gate
is marked not applicable (None) only when the route does not produce its evidence: raw records and
the raw-publication gauge when raw publishing is off, application consumer groups for direct
routing, and the offset-commit gauge for direct routing. Every other gate is required.
"""
import argparse
import csv
import json
import math
from pathlib import Path

from summarize_linux_multitype_mixed import (
    MAX_P95_MS, QUIET_PERIOD_SECONDS, RATE_TOLERANCE_PERCENT, TYPES, EvidenceError,
    close_metric, decode_json_cell, delta_counter, extra_groups_drained, integer, number,
    offset_map_covers_topic, pct, read_csv, read_json, require_keys, resource_metrics)

MODES = {
    "ffm-local": {"label": "FFM raw-topic", "ffm": True, "direct": False, "raw": True},
    "ffm-direct-raw": {"label": "FFM direct, raw on", "ffm": True, "direct": True, "raw": True},
    "ffm-direct-noraw": {"label": "FFM direct, raw off", "ffm": True, "direct": True, "raw": False},
    "external-adm4": {"label": "External ADM (4 workers)", "ffm": False, "direct": False, "raw": True},
}
RESOURCE_COLUMNS = (
    "timestamp_utc", "elapsed_seconds", "actual_cadence_seconds", "service",
    "interval_cpu_percent", "memory_current_bytes", "cpu_max", "memory_max",
    "cpu_throttled_usec", "cpu_nr_throttled", "memory_events", "cpu_pressure", "memory_pressure",
)
LATENCY_COLUMNS = (
    "raw_partition", "raw_offset", "raw_timestamp_type", "raw_timestamp_ms",
    "json_partition", "json_offset", "json_timestamp_type", "json_timestamp_ms",
    "udp_send_time_ms", "udp_send_monotonic_s", "udp_to_json_record_ms", "timestamp_status",
)
SUMMARY_KEYS = (
    "target_rate_per_second", "actual_udp_rate_per_second", "complete_10s_window_rates_per_second",
    "rate_tolerance_percent", "warmup", "measured_requested_count", "udp_sent", "raw_correlated",
    "json_correlated", "paired", "duplicate_raw", "duplicate_json", "unexpected_records",
    "unexpected_dlt_records", "invalid_timestamps", "negative_timestamp_quantization_samples",
    "benchmark_failure", "health_ok_through_quiet_period", "health_failure", "consumer_lag_drained",
    "final_watermarks", "pending_publications", "pending_raw_publications",
    "pending_offset_commits", "application_committed_offsets", "extra_pipeline_committed_offsets",
    "application_consumer_group", "raw_topic", "dlt_topic", "p50_ms", "p95_ms", "p99_ms",
    "max_p95_ms", "passed", "errors",
)


def parse_rows(rows, expect_raw, first_id, count):
    ids, json_positions, raw_positions, values, sends = [], [], [], [], []
    valid_rows = quantization_rows = 0
    integrity = True
    for row in rows:
        message_id = row.get("message_id") or row.get("bsm_id")
        if message_id:
            ids.append(message_id)
        else:
            integrity = False
        json_partition, json_offset = integer(row.get("json_partition")), integer(row.get("json_offset"))
        if json_partition is None or json_offset is None:
            integrity = False
        else:
            json_positions.append((json_partition, json_offset))
        json_type = integer(row.get("json_timestamp_type"))
        json_timestamp = number(row.get("json_timestamp_ms"))
        send_wall = number(row.get("udp_send_time_ms"))
        send_mono = number(row.get("udp_send_monotonic_s"))
        latency = number(row.get("udp_to_json_record_ms"))
        status = (row.get("timestamp_status") or "").strip()
        raw_ok = True
        if expect_raw:
            raw_partition, raw_offset = integer(row.get("raw_partition")), integer(row.get("raw_offset"))
            if raw_partition is None or raw_offset is None:
                integrity = False
            else:
                raw_positions.append((raw_partition, raw_offset))
            raw_ok = (integer(row.get("raw_timestamp_type")) == 1
                      and number(row.get("raw_timestamp_ms")) is not None)
        expected_latency = (json_timestamp - send_wall
                            if json_timestamp is not None and send_wall is not None else None)
        valid = (
            raw_ok and json_type == 1 and json_timestamp is not None and send_wall is not None
            and send_mono is not None and latency is not None and expected_latency is not None
            and latency >= -1.0
            and math.isclose(latency, expected_latency, rel_tol=1e-9, abs_tol=1e-6)
            and ((status == "valid" and latency >= 0)
                 or (status == "quantization_negative" and latency < 0))
        )
        if valid:
            valid_rows += 1
            values.append(latency)
            sends.append(send_mono)
            quantization_rows += status == "quantization_negative"
        else:
            integrity = False
    expected_ids = {f"{message_id:08X}" for message_id in range(first_id, first_id + count)}
    unique_ids = len(ids) == len(set(ids))
    return {
        "latencies_ms": values, "send_monotonic_s": sends,
        "valid_timestamp_rows": valid_rows, "quantization_negative_rows": quantization_rows,
        "row_integrity": integrity, "rows": len(ids),
        "unique_ids": unique_ids, "measured_id_range_matches": unique_ids and set(ids) == expected_ids,
        "unique_json_positions": len(json_positions) == len(set(json_positions)),
        "unique_raw_positions": (not expect_raw) or len(raw_positions) == len(set(raw_positions)),
    }


def evaluate_type(mode, name, item, runtime, count, first_id):
    config = MODES[mode]
    summary, rows = item["summary"], item["rows"]
    parsed = parse_rows(rows, config["raw"], first_id, count)
    latencies = parsed["latencies_ms"]
    calculated = {key: pct(latencies, fraction) for key, fraction in
                  (("p50_ms", .50), ("p95_ms", .95), ("p99_ms", .99))}
    settings = runtime.get("settings", {})
    run_config = runtime.get("runs", {}).get(name, {})
    lower = 50 * (1 - RATE_TOLERANCE_PERCENT / 100)
    upper = 50 * (1 + RATE_TOLERANCE_PERCENT / 100)
    actual_rate = number(summary.get("actual_udp_rate_per_second"))
    windows = [number(rate) for rate in summary.get("complete_10s_window_rates_per_second") or []]
    sent = integer(summary.get("udp_sent"))
    raw_count = integer(summary.get("raw_correlated"))
    json_count = integer(summary.get("json_correlated"))
    paired = integer(summary.get("paired"))
    # Direct routing runs no application consumer group, so its application-group gate is not applicable.
    app_drained = None
    if not config["direct"]:
        app_drained = offset_map_covers_topic(summary.get("application_committed_offsets"),
                                              summary.get("final_watermarks"), summary.get("raw_topic"))
    if mode == "external-adm4":
        app_drained = app_drained and extra_groups_drained(summary)
    checks = {
        "workload_parameters": (
            run_config.get("per_type_target_rate") == 50
            and settings.get("per_type_rate") == 50 and settings.get("per_type_count") == count
            and settings.get("warmup_per_type") == 1000 and settings.get("type_count") == 10
            and settings.get("aggregate_target_messages_per_second") == 500
            and number(summary.get("target_rate_per_second")) == 50
            and integer(summary.get("warmup")) == 1000
            and integer(summary.get("measured_requested_count")) == count
        ),
        "send_rate_within_5_percent": (
            actual_rate is not None and lower <= actual_rate <= upper
            and bool(windows) and all(rate is not None and lower <= rate <= upper for rate in windows)
        ),
        "measured_sends_sent": sent == count and len(rows) == count,
        "one_json_record_per_send": json_count == count and parsed["unique_ids"]
            and parsed["measured_id_range_matches"] and parsed["unique_json_positions"],
        "one_raw_record_per_send": (raw_count == count and paired == count
                                    and parsed["unique_raw_positions"]) if config["raw"] else None,
        "valid_timestamps": (
            parsed["row_integrity"] and parsed["valid_timestamp_rows"] == count
            and integer(summary.get("invalid_timestamps")) == 0
            and integer(summary.get("negative_timestamp_quantization_samples"))
            == parsed["quantization_negative_rows"]
        ),
        "zero_duplicates": integer(summary.get("duplicate_raw")) == 0
            and integer(summary.get("duplicate_json")) == 0,
        "zero_unexpected_records": integer(summary.get("unexpected_records")) == 0,
        "zero_unexpected_dlq": (integer(summary.get("unexpected_dlt_records")) == 0)
            if config["ffm"] else None,
        "consumer_groups_drained": (
            summary.get("consumer_lag_drained") is True and app_drained is True
        ) if not config["direct"] else (summary.get("consumer_lag_drained") is True),
        "health_through_quiet_period": summary.get("health_ok_through_quiet_period") is True
            and summary.get("health_failure") in (None, ""),
        "ffm_pending_zero": (
            integer(summary.get("pending_publications")) == 0
            and (not config["raw"] or integer(summary.get("pending_raw_publications")) == 0)
            and (config["direct"] or integer(summary.get("pending_offset_commits")) == 0)
        ) if config["ffm"] else None,
        "p95_strictly_below_5_ms": calculated["p95_ms"] is not None
            and calculated["p95_ms"] < MAX_P95_MS,
        "saved_percentiles_match_csv": all(
            close_metric(summary.get(key), calculated[key]) for key in calculated),
        "benchmark_completed": summary.get("benchmark_failure") in (None, ""),
    }
    failed = [key for key, value in checks.items() if value is False]
    return {
        "type": name.upper(), "p50_ms": calculated["p50_ms"], "p95_ms": calculated["p95_ms"],
        "p99_ms": calculated["p99_ms"], "valid_latency_rows": len(latencies),
        "actual_rate_per_second": actual_rate, "udp_sent": sent, "raw_correlated": raw_count,
        "json_correlated": json_count, "paired": paired,
        "duplicate_raw": integer(summary.get("duplicate_raw")),
        "duplicate_json": integer(summary.get("duplicate_json")),
        "unexpected_dlt_records": integer(summary.get("unexpected_dlt_records")),
        "invalid_timestamps": integer(summary.get("invalid_timestamps")),
        "pending_publications": integer(summary.get("pending_publications")),
        "pending_raw_publications": integer(summary.get("pending_raw_publications")),
        "pending_offset_commits": integer(summary.get("pending_offset_commits")),
        "checks": checks, "failed_checks": failed, "passed": not failed,
        "decode_benchmark_passed": summary.get("passed") is True,
        "decode_benchmark_errors": summary.get("errors") or [],
        "send_monotonic_s": parsed["send_monotonic_s"],
        "latencies_ms": latencies,
    }


def runtime_checks(mode, runtime):
    services = runtime.get("containers_uncapped_before_traffic", {})
    required = ["ode", "kafka"] + (["adm"] if mode == "external-adm4" else [])
    partitions = runtime.get("topic_partitions", {})
    restore = runtime.get("restore", {})
    adm = runtime.get("external_adm_process_preflight") or {}
    return {
        "containers_uncapped_before_traffic": all(
            services.get(service, {}).get("uncapped") is True for service in required),
        "topic_partitions_all_four": bool(partitions) and all(v == 4 for v in partitions.values()),
        "actuator_health_up_before_traffic":
            (runtime.get("actuator_health_preflight") or {}).get("status") == "UP",
        "adm_four_processes_running": mode != "external-adm4" or adm.get("running_count") == 4,
        # A failed acceptance gate exits non-zero, so check that every decode run completed instead.
        "all_decode_runs_completed": all(
            "exit_code" in runtime.get("runs", {}).get(name, {}) for name in TYPES),
        "no_setup_or_execution_failure": "setup_or_execution_failure" not in runtime,
        "isolated_project_removed": restore.get("compose_down") is True,
        "unrelated_8080_state_unchanged": restore.get("ode_8080_state_unchanged") is True,
    }


def aligned_round_peaks(runtime, samples, window):
    """Peak sums across containers, taking each sampling round (shared timestamp) as a unit."""
    services = set(runtime.get("containers_uncapped_before_traffic", {}))
    rounds = {}
    for row in samples:
        rounds.setdefault(row["timestamp_utc"], []).append(row)
    peak_cpu = peak_memory = None
    for group in rounds.values():
        elapsed = number(group[0].get("elapsed_seconds"))
        if window["start"] is None or elapsed is None or not window["start"] <= elapsed <= window["end"]:
            continue
        present = {row.get("service"): row for row in group if row.get("service") in services}
        if set(present) != services:
            continue
        cpu_values = [number(row.get("interval_cpu_percent")) for row in present.values()]
        memory_values = [integer(row.get("memory_current_bytes")) for row in present.values()]
        if any(value is None for value in cpu_values + memory_values):
            continue
        peak_cpu = max(peak_cpu or 0, sum(cpu_values) / 100)
        peak_memory = max(peak_memory or 0, sum(memory_values))
    return peak_cpu, peak_memory


def summarize_mode(mode, data, count, first_id):
    runtime, samples = data["runtime"], data["samples"]
    types = {name: evaluate_type(mode, name, data["types"][name], runtime, count, first_id)
             for name in TYPES}
    results = list(types.values())
    all_latencies = [value for result in results for value in result["latencies_ms"]]
    resources = resource_metrics(runtime, samples, results)
    peak_cpu, peak_memory = aligned_round_peaks(runtime, samples, resources["measured_send_window_elapsed_seconds"])
    average_values = [container["average_cpu_cores"] for container in resources["containers"].values()]
    prerequisites = runtime_checks(mode, runtime)
    return {
        "mode": mode, "label": MODES[mode]["label"],
        "aggregate": {
            "p50_ms": pct(all_latencies, .50), "p95_ms": pct(all_latencies, .95),
            "p99_ms": pct(all_latencies, .99), "paired": sum(r["paired"] or 0 for r in results),
            "json_correlated": sum(r["json_correlated"] or 0 for r in results),
            "raw_correlated": sum(r["raw_correlated"] or 0 for r in results),
            "udp_sent": sum(r["udp_sent"] or 0 for r in results),
            "actual_aggregate_rate_per_second": sum(r["actual_rate_per_second"] or 0 for r in results),
            "types_passed": sum(r["passed"] for r in results),
            "unexpected_dlt_records": sum(r["unexpected_dlt_records"] or 0 for r in results)
                if MODES[mode]["ffm"] else None,
            "duplicates": sum((r["duplicate_raw"] or 0) + (r["duplicate_json"] or 0) for r in results),
        },
        "prerequisites": prerequisites,
        "passed": all(r["passed"] for r in results) and all(prerequisites.values()),
        "types": {name: {k: v for k, v in result.items() if k not in ("send_monotonic_s", "latencies_ms")}
                  for name, result in types.items()},
        "resources": {
            "containers": resources["containers"],
            "total_average_cpu_cores": sum(average_values) if all(v is not None for v in average_values) else None,
            "total_peak_cpu_cores_aligned": peak_cpu,
            "total_peak_memory_bytes_aligned": peak_memory,
            "sample_rows": len(samples),
            "sample_errors": runtime.get("sample_errors", []),
            "measured_send_window": resources["measured_send_window_elapsed_seconds"],
        },
        "vm_capacity": runtime.get("vm_capacity"),
        "source_identity": runtime.get("source_identity"),
    }


def load_mode(root, mode):
    base = root / mode
    runtime = read_json(base / "runtime-evidence.json")
    require_keys(runtime, ("settings", "runs", "vm_capacity", "containers_uncapped_before_traffic",
                           "source_identity", "corpus_evidence"), base / "runtime-evidence.json")
    samples = read_csv(base / "resource-samples.csv", RESOURCE_COLUMNS)
    types = {}
    for name in TYPES:
        directory = base / name
        summary = read_json(directory / "latency.json")
        require_keys(summary, SUMMARY_KEYS, directory / "latency.json")
        rows = read_csv(directory / "latency.csv", LATENCY_COLUMNS,
                        one_of_columns=("message_id", "bsm_id"))
        types[name] = {"summary": summary, "rows": rows}
    return {"runtime": runtime, "samples": samples, "types": types}


def fmt(value, digits=3):
    value = number(value)
    return "n/a" if value is None else f"{value:.{digits}f}"


def fmt_mib(value):
    value = number(value)
    return "n/a" if value is None else f"{value / 1048576:.1f} MiB"


def gate_text(value):
    return "n/a" if value is None else ("PASS" if value else "FAIL")


def write_outputs(root, modes, count):
    summary = {"measured_per_type": count, "modes": modes}
    (root / "route-summary.json").write_text(json.dumps(summary, indent=2, default=str), encoding="utf-8")
    with (root / "route-per-type.csv").open("w", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream)
        writer.writerow(("mode", "type", "p50_ms", "p95_ms", "p99_ms", "udp_sent", "raw_correlated",
                         "json_correlated", "paired", "actual_rate_per_second", "duplicate_raw",
                         "duplicate_json", "unexpected_dlt_records", "invalid_timestamps",
                         "pending_publications", "pending_raw_publications",
                         "pending_offset_commits", "passed", "failed_checks"))
        for mode, data in modes.items():
            for name, result in data["types"].items():
                writer.writerow((mode, name, result["p50_ms"], result["p95_ms"], result["p99_ms"],
                                 result["udp_sent"], result["raw_correlated"], result["json_correlated"],
                                 result["paired"], result["actual_rate_per_second"],
                                 result["duplicate_raw"], result["duplicate_json"],
                                 result["unexpected_dlt_records"], result["invalid_timestamps"],
                                 result["pending_publications"], result["pending_raw_publications"],
                                 result["pending_offset_commits"], result["passed"],
                                 ";".join(result["failed_checks"])))
    with (root / "route-resources.csv").open("w", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream)
        writer.writerow(("mode", "container", "average_cpu_cores", "peak_cpu_cores_1s",
                         "peak_memory_bytes", "cpu_throttled_usec_delta", "nr_throttled_delta",
                         "memory_oom_events_delta", "memory_oom_kill_events_delta",
                         "average_cadence_seconds", "sample_count"))
        for mode, data in modes.items():
            for service, metrics in data["resources"]["containers"].items():
                writer.writerow((mode, service, metrics["average_cpu_cores"],
                                 metrics["peak_cpu_cores_1s"], metrics["peak_memory_bytes"],
                                 metrics["cpu_throttled_usec_delta"], metrics["nr_throttled_delta"],
                                 metrics["memory_oom_events_delta"],
                                 metrics["memory_oom_kill_events_delta"],
                                 metrics["average_cadence_seconds"], metrics["sample_count"]))


def markdown_table(headers, rows):
    lines = ["| " + " | ".join(headers) + " |", "|" + "|".join("---" for _ in headers) + "|"]
    lines += ["| " + " | ".join(str(cell) for cell in row) + " |" for row in rows]
    return "\n".join(lines)


def write_report(root, modes, count):
    names = list(modes)
    headers = ["Measure"] + [modes[m]["label"] for m in names]
    overall = [
        ["p50 latency", *[f"{fmt(modes[m]['aggregate']['p50_ms'])} ms" for m in names]],
        ["p95 latency", *[f"{fmt(modes[m]['aggregate']['p95_ms'])} ms" for m in names]],
        ["p99 latency", *[f"{fmt(modes[m]['aggregate']['p99_ms'])} ms" for m in names]],
        ["Actual aggregate send rate", *[f"{fmt(modes[m]['aggregate']['actual_aggregate_rate_per_second'], 2)} /s" for m in names]],
        ["UDP sends (measured)", *[f"{modes[m]['aggregate']['udp_sent']:,}" for m in names]],
        ["Paired JSON / raw records", *[(f"{modes[m]['aggregate']['json_correlated']:,} / "
                                         + (f"{modes[m]['aggregate']['raw_correlated']:,}" if MODES[m]["raw"] else "not published"))
                                        for m in names]],
        ["Duplicates (raw + JSON)", *[f"{modes[m]['aggregate']['duplicates']}" for m in names]],
        ["Unexpected DLQ records", *[("n/a" if modes[m]["aggregate"]["unexpected_dlt_records"] is None
                                      else str(modes[m]["aggregate"]["unexpected_dlt_records"])) for m in names]],
        ["Types passing every gate", *[f"{modes[m]['aggregate']['types_passed']} / 10" for m in names]],
        ["Run outcome", *["PASS" if modes[m]["passed"] else "FAIL" for m in names]],
    ]
    per_type_p = {}
    for metric, key in (("p50", "p50_ms"), ("p95", "p95_ms"), ("p99", "p99_ms")):
        per_type_p[metric] = markdown_table(
            ["Type", *[modes[m]["label"] for m in names]],
            [[name.upper(), *[f"{fmt(modes[m]['types'][name][key])} ms" for m in names]] for name in TYPES])
    gates = markdown_table(
        ["Type", *[modes[m]["label"] for m in names]],
        [[name.upper(), *[("PASS" if modes[m]["types"][name]["passed"] else
                           "FAIL (" + ", ".join(modes[m]["types"][name]["failed_checks"]) + ")")
                          for m in names]] for name in TYPES])
    resource_rows = []
    for m in names:
        containers = modes[m]["resources"]["containers"]
        for service, metrics in containers.items():
            resource_rows.append([modes[m]["label"], service, fmt(metrics["average_cpu_cores"]),
                                  fmt(metrics["peak_cpu_cores_1s"]), fmt_mib(metrics["peak_memory_bytes"]),
                                  metrics["cpu_throttled_usec_delta"] or 0,
                                  metrics["memory_oom_events_delta"] or 0,
                                  metrics["memory_oom_kill_events_delta"] or 0])
        total = modes[m]["resources"]
        resource_rows.append([modes[m]["label"], "Total", fmt(total["total_average_cpu_cores"]),
                              fmt(total["total_peak_cpu_cores_aligned"]),
                              fmt_mib(total["total_peak_memory_bytes_aligned"]), "", "", ""])
    prereq_rows = []
    for m in names:
        failed = [k for k, v in modes[m]["prerequisites"].items() if not v]
        prereq_rows.append([modes[m]["label"], "PASS" if not failed else "FAIL: " + ", ".join(failed)])
    lines = [
        "# ODE mixed-message performance comparison: FFM routes and External ADM", "",
        "## Test environment", "",
        "Linux VM, same host for every mode, each run in an isolated Compose project. ODE, Kafka, and (external run) ADM ran without container CPU or memory limits, verified in cgroups before traffic. Ten message types were sent at 50 messages/s each (500 messages/s aggregate), evenly interleaved at 2 ms phase offsets. Each mode sent "
        f"{1000:,} warm-up and {count:,} measured messages per type, from one shared packet corpus (byte-identical across modes). "
        "Latency is Kafka JSON CreateTime minus UDP send time; Kafka timestamps have millisecond resolution.", "",
        "## Modes", "",
        "- **FFM raw-topic**: `ODE_ASN1_CODEC_MODE=ffm`, `ODE_FFM_UDP_ROUTING=raw-topic`. Raw record is published, then decoded by a per-type listener with four consumers, confirmed JSON publication, then an asynchronous offset commit.",
        "- **FFM direct, raw on**: `ODE_FFM_UDP_ROUTING=direct`, `ODE_FFM_RAW_PUBLISH=true`. Decode and JSON publication on the UDP receiver thread; raw record is published but never consumed.",
        "- **FFM direct, raw off**: `ODE_FFM_UDP_ROUTING=direct`, `ODE_FFM_RAW_PUBLISH=false`. Decode and JSON publication only; no raw record and no consumers.",
        "- **External ADM (4 workers)**: `ODE_ASN1_CODEC_MODE=external` with four asn1_codec processes; input and output topics have four partitions.",
        "", "## How to read pass and fail", "",
        "A type passes only when every applicable gate passes: rate within 5% of 50/s, one JSON record (and one raw record when raw publishing is on) per measured send with a valid CreateTime, zero duplicates and unexpected records, zero unexpected DLQ records (FFM), drained consumers, health UP through the 15-second quiet period, zero FFM pending publications, raw publications, and offset commits where that route has them, and p95 strictly below 5 ms. A gate is shown as n/a when the route does not produce its evidence (raw records when raw publishing is off; consumer groups for direct routing; offset commits for direct routing). A mode passes only when every type passes and the pre-traffic and restore checks pass.",
        "", "## Overall comparison", "",
        markdown_table(headers, overall),
        "", "## Latency by message type", "",
        "### p50", "", per_type_p["p50"], "",
        "### p95", "", per_type_p["p95"], "",
        "### p99", "", per_type_p["p99"], "",
        "## Gate results by message type", "", gates, "",
        "## Pre-traffic and restore checks", "",
        markdown_table(["Mode", "Result"], prereq_rows), "",
        "## Container resource use", "",
        "CPU is in logical cores; peak CPU is the highest one-second interval average. Memory is peak cgroup usage during the measured send window. Average totals sum the container averages. Peak totals use aligned sampling rounds (all containers sampled in the same round), not the sum of each container's separate maximum.", "",
        markdown_table(["Test", "Container", "Average CPU", "Peak CPU", "Peak memory", "CPU throttled (usec)", "OOM events", "OOM kill events"],
                       resource_rows),
        "",
        "## Outcome", "",
        "See the per-mode gate table above for the reasons each type failed, if any. Results describe this one run of this workload and this host; they are not a repeatability study.",
        "",
    ]
    (root / "route-report.md").write_text("\n".join(lines), encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", type=Path, help="evidence directory containing one folder per mode")
    parser.add_argument("--modes", nargs="+", default=list(MODES), choices=list(MODES))
    parser.add_argument("--count", type=int, default=50000, help="measured messages per type")
    parser.add_argument("--warmup", type=int, default=1000)
    args = parser.parse_args()
    root = args.root.resolve()
    first_id = args.warmup
    modes = {}
    for mode in args.modes:
        try:
            data = load_mode(root, mode)
        except EvidenceError as error:
            raise SystemExit(f"{mode}: {error}") from error
        modes[mode] = summarize_mode(mode, data, args.count, first_id)
    write_outputs(root, modes, args.count)
    write_report(root, modes, args.count)
    print(root / "route-report.md")


if __name__ == "__main__":
    main()

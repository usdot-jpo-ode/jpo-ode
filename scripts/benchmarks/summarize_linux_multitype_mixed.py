#!/usr/bin/env python3
"""Build strict mixed-type comparison outputs from saved benchmark evidence."""
import argparse
import csv
import json
import math
from pathlib import Path

TYPES = ("bsm", "map", "spat", "tim", "psm", "srm", "ssm", "sdsm", "rtcm", "rsm")
MODES = {
    "ffm-local": "FFM",
    "external-adm4": "External ADM (4 workers)",
}
EXPECTED_TYPES = 10
EXPECTED_AGGREGATE_RATE = 500
EXPECTED_TYPE_RATE = 50
EXPECTED_MEASURED_PER_TYPE = 50000
EXPECTED_WARMUP_PER_TYPE = 1000
EXPECTED_FIRST_MEASURED_ID = EXPECTED_WARMUP_PER_TYPE
EXPECTED_LAST_MEASURED_ID = (
    EXPECTED_FIRST_MEASURED_ID + EXPECTED_MEASURED_PER_TYPE - 1
)
RATE_TOLERANCE_PERCENT = 5
MAX_P95_MS = 5
QUIET_PERIOD_SECONDS = 15
EXPECTED_FFMLIB_VERSION = "3.0.0-beta6"


class EvidenceError(Exception):
    """Raised when saved evidence is absent or cannot be interpreted."""


def pct(values, fraction):
    if not values:
        return None
    values = sorted(values)
    position = (len(values) - 1) * fraction
    low, high = math.floor(position), math.ceil(position)
    return values[low] if low == high else values[low] + (values[high] - values[low]) * (position - low)


def number(value):
    try:
        value = float(value)
    except (TypeError, ValueError):
        return None
    return value if math.isfinite(value) else None


def integer(value):
    value = number(value)
    return int(value) if value is not None and value.is_integer() else None


def fmt(value, digits=3):
    value = number(value)
    return "missing" if value is None else f"{value:.{digits}f}"


def fmt_count(value):
    value = integer(value)
    return "missing" if value is None else f"{value:,}"


def sum_if_complete(values):
    return sum(values) if values and all(value is not None for value in values) else None


def table(headers, rows):
    return "\n".join(
        ["| " + " | ".join(map(str, headers)) + " |",
         "| " + " | ".join("---" for _ in headers) + " |"]
        + ["| " + " | ".join(map(str, row)) + " |" for row in rows]
    )


def read_json(path):
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise EvidenceError(f"cannot read valid JSON from {path}: {error}") from error


def read_csv(path, required_columns, one_of_columns=()):
    try:
        with path.open(newline="", encoding="utf-8") as stream:
            reader = csv.DictReader(stream)
            fieldnames = set(reader.fieldnames or ())
            missing = sorted(set(required_columns) - fieldnames)
            if missing:
                raise EvidenceError(f"{path} is missing CSV columns: {', '.join(missing)}")
            if one_of_columns and not fieldnames.intersection(one_of_columns):
                raise EvidenceError(
                    f"{path} must include one of these CSV columns: "
                    + ", ".join(one_of_columns)
                )
            return list(reader)
    except OSError as error:
        raise EvidenceError(f"cannot read {path}: {error}") from error


def require_keys(data, keys, path):
    if not isinstance(data, dict):
        raise EvidenceError(f"{path} must contain a JSON object")
    missing = [key for key in keys if key not in data]
    if missing:
        raise EvidenceError(f"{path} is missing required fields: {', '.join(missing)}")


def load_inputs(root):
    required_files = []
    for mode in MODES:
        base = root / mode
        required_files.extend((base / "runtime-evidence.json", base / "resource-samples.csv"))
        for name in TYPES:
            required_files.extend((base / name / "latency.json", base / name / "latency.csv"))
    missing = [path for path in required_files if not path.is_file()]
    if missing:
        joined = "\n".join(f"  {path}" for path in missing)
        raise EvidenceError("missing required benchmark evidence files:\n" + joined)

    loaded = {}
    resource_columns = (
        "timestamp_utc", "elapsed_seconds", "actual_cadence_seconds", "service",
        "interval_cpu_percent", "memory_current_bytes", "cpu_max", "memory_max",
        "cpu_throttled_usec", "cpu_nr_throttled", "memory_events",
        "cpu_pressure", "memory_pressure",
    )
    latency_columns = (
        "raw_partition", "raw_offset", "raw_timestamp_type", "raw_timestamp_ms",
        "json_partition", "json_offset", "json_timestamp_type", "json_timestamp_ms",
        "udp_send_time_ms", "udp_send_monotonic_s", "udp_to_json_record_ms",
        "timestamp_status",
    )
    for mode in MODES:
        base = root / mode
        runtime_path = base / "runtime-evidence.json"
        runtime = read_json(runtime_path)
        require_keys(runtime, ("settings", "runs", "vm_capacity",
                               "containers_uncapped_before_traffic", "source_identity",
                               "corpus_evidence"),
                     runtime_path)
        require_keys(runtime["settings"], ("aggregate_target_messages_per_second",
                                            "type_count", "per_type_rate",
                                            "per_type_count", "warmup_per_type"),
                     runtime_path)
        samples = read_csv(base / "resource-samples.csv", resource_columns)
        type_inputs = {}
        for name in TYPES:
            directory = base / name
            summary_path = directory / "latency.json"
            summary = read_json(summary_path)
            require_keys(summary, (
                "target_rate_per_second", "actual_udp_rate_per_second",
                "complete_10s_window_rates_per_second", "rate_tolerance_percent",
                "warmup", "measured_requested_count", "udp_sent", "raw_correlated",
                "json_correlated", "paired", "duplicate_raw", "duplicate_json",
                "unexpected_records", "unexpected_dlt_records", "invalid_timestamps",
                "negative_timestamp_quantization_samples", "benchmark_failure",
                "health_ok_through_quiet_period", "health_failure",
                "consumer_lag_drained", "final_watermarks",
                "pending_publications", "pending_raw_publications",
                "pending_offset_commits", "application_committed_offsets",
                "extra_pipeline_committed_offsets", "application_consumer_group",
                "raw_topic", "dlt_topic", "p50_ms", "p95_ms",
                "p99_ms", "max_p95_ms", "passed", "errors",
            ), summary_path)
            rows = read_csv(
                directory / "latency.csv", latency_columns,
                one_of_columns=("message_id", "bsm_id"),
            )
            type_inputs[name] = {"summary": summary, "rows": rows}
        loaded[mode] = {"runtime": runtime, "samples": samples, "types": type_inputs}
    return loaded


def offset_map_covers_topic(committed, watermarks, topic):
    if not isinstance(committed, dict) or not isinstance(watermarks, dict):
        return False
    topic_ends = {}
    for key, value in watermarks.items():
        if not isinstance(key, str) or ":" not in key:
            continue
        name, partition_text = key.rsplit(":", 1)
        end, partition = integer(value), integer(partition_text)
        if name == topic and partition is not None and end is not None:
            topic_ends[partition] = end
    if not topic_ends:
        return False
    for key, end in topic_ends.items():
        committed_value = integer(committed.get(f"{topic}:{key}"))
        if committed_value is None or committed_value < end:
            return False
    return True


def parse_latency_rows(rows):
    ids = []
    raw_positions = []
    json_positions = []
    values = []
    sends = []
    events = []
    valid_timestamp_rows = 0
    quantization_rows = 0
    row_integrity = True

    for row in rows:
        message_id = row.get("message_id") or row.get("bsm_id")
        if not message_id:
            row_integrity = False
        else:
            ids.append(message_id)
        raw_partition, raw_offset = integer(row.get("raw_partition")), integer(row.get("raw_offset"))
        json_partition, json_offset = integer(row.get("json_partition")), integer(row.get("json_offset"))
        if raw_partition is None or raw_offset is None:
            row_integrity = False
        else:
            raw_positions.append((raw_partition, raw_offset))
        if json_partition is None or json_offset is None:
            row_integrity = False
        else:
            json_positions.append((json_partition, json_offset))

        raw_type = integer(row.get("raw_timestamp_type"))
        json_type = integer(row.get("json_timestamp_type"))
        raw_timestamp = number(row.get("raw_timestamp_ms"))
        json_timestamp = number(row.get("json_timestamp_ms"))
        send_wall = number(row.get("udp_send_time_ms"))
        send_mono = number(row.get("udp_send_monotonic_s"))
        latency = number(row.get("udp_to_json_record_ms"))
        status = row.get("timestamp_status", "").strip()
        expected_latency = (json_timestamp - send_wall
                            if json_timestamp is not None and send_wall is not None else None)
        valid = (
            raw_type == 1 and json_type == 1
            and raw_timestamp is not None and json_timestamp is not None
            and send_wall is not None and send_mono is not None
            and latency is not None and expected_latency is not None
            and latency >= -1.0
            and math.isclose(latency, expected_latency, rel_tol=1e-9, abs_tol=1e-6)
            and status in ("valid", "quantization_negative")
            and ((status == "valid" and latency >= 0)
                 or (status == "quantization_negative" and latency < 0))
        )
        if valid:
            valid_timestamp_rows += 1
            values.append(latency)
            sends.append(send_mono)
            if status == "quantization_negative":
                quantization_rows += 1
        else:
            row_integrity = False

    unique_ids = len(ids) == len(set(ids))
    expected_ids = {
        f"{message_id:08X}"
        for message_id in range(
            EXPECTED_FIRST_MEASURED_ID,
            EXPECTED_LAST_MEASURED_ID + 1,
        )
    }
    id_range_matches = unique_ids and set(ids) == expected_ids
    unique_raw_positions = len(raw_positions) == len(set(raw_positions))
    unique_json_positions = len(json_positions) == len(set(json_positions))
    return {
        "ids": ids,
        "unique_ids": unique_ids,
        "measured_id_range_matches": id_range_matches,
        "expected_measured_id_range": [
            f"{EXPECTED_FIRST_MEASURED_ID:08X}",
            f"{EXPECTED_LAST_MEASURED_ID:08X}",
        ],
        "unique_raw_positions": unique_raw_positions,
        "unique_json_positions": unique_json_positions,
        "row_integrity": row_integrity,
        "valid_timestamp_rows": valid_timestamp_rows,
        "quantization_negative_rows": quantization_rows,
        "latencies_ms": values,
        "send_monotonic_s": sends,
    }


def close_metric(recorded, measured):
    recorded, measured = number(recorded), number(measured)
    return (recorded is not None and measured is not None
            and math.isclose(recorded, measured, rel_tol=1e-9, abs_tol=1e-6))


def app_offsets_drained(summary):
    return offset_map_covers_topic(
        summary.get("application_committed_offsets"),
        summary.get("final_watermarks"),
        summary.get("raw_topic"),
    )


def extra_groups_drained(summary):
    groups = summary.get("extra_pipeline_committed_offsets")
    if not isinstance(groups, dict) or len(groups) != 2:
        return False
    watermarks = summary.get("final_watermarks")
    if not isinstance(watermarks, dict):
        return False
    for offsets in groups.values():
        if not isinstance(offsets, dict) or not offsets:
            return False
        topics = {key.rsplit(":", 1)[0] for key in offsets if isinstance(key, str) and ":" in key}
        if len(topics) != 1:
            return False
        topic = next(iter(topics))
        if not offset_map_covers_topic(offsets, watermarks, topic):
            return False
    return True


def evaluate_type(mode, name, runtime, item):
    summary, rows = item["summary"], item["rows"]
    parsed = parse_latency_rows(rows)
    latencies = parsed["latencies_ms"]
    calculated = {key: pct(latencies, frac) for key, frac in
                  (("p50_ms", .50), ("p95_ms", .95), ("p99_ms", .99))}
    run_config = runtime.get("runs", {}).get(name, {})
    settings = runtime.get("settings", {})
    runtime_config_ok = (
        run_config.get("per_type_target_rate") == EXPECTED_TYPE_RATE
        and settings.get("per_type_rate") == EXPECTED_TYPE_RATE
        and settings.get("per_type_count") == EXPECTED_MEASURED_PER_TYPE
        and settings.get("warmup_per_type") == EXPECTED_WARMUP_PER_TYPE
        and settings.get("type_count") == EXPECTED_TYPES
        and settings.get("aggregate_target_messages_per_second") == EXPECTED_AGGREGATE_RATE
    )
    summary_config_ok = (
        number(summary.get("target_rate_per_second")) == EXPECTED_TYPE_RATE
        and integer(summary.get("warmup")) == EXPECTED_WARMUP_PER_TYPE
        and integer(summary.get("measured_requested_count")) == EXPECTED_MEASURED_PER_TYPE
        and number(summary.get("rate_tolerance_percent")) == RATE_TOLERANCE_PERCENT
        and number(summary.get("max_p95_ms")) == MAX_P95_MS
    )
    actual_rate = number(summary.get("actual_udp_rate_per_second"))
    target_rate = number(summary.get("target_rate_per_second"))
    lower = EXPECTED_TYPE_RATE * (1 - RATE_TOLERANCE_PERCENT / 100)
    upper = EXPECTED_TYPE_RATE * (1 + RATE_TOLERANCE_PERCENT / 100)
    window_rates = summary.get("complete_10s_window_rates_per_second")
    if not isinstance(window_rates, list):
        window_rates = []
    parsed_windows = [number(rate) for rate in window_rates]
    rate_ok = (
        actual_rate is not None and target_rate == EXPECTED_TYPE_RATE
        and lower <= actual_rate <= upper
        and bool(parsed_windows)
        and all(rate is not None and lower <= rate <= upper for rate in parsed_windows)
    )
    sent = integer(summary.get("udp_sent"))
    raw = integer(summary.get("raw_correlated"))
    json_count = integer(summary.get("json_correlated"))
    paired = integer(summary.get("paired"))
    correlations_ok = (
        sent == EXPECTED_MEASURED_PER_TYPE
        and raw == EXPECTED_MEASURED_PER_TYPE
        and json_count == EXPECTED_MEASURED_PER_TYPE
        and paired == EXPECTED_MEASURED_PER_TYPE
        and len(rows) == EXPECTED_MEASURED_PER_TYPE
        and parsed["unique_ids"]
        and parsed["measured_id_range_matches"]
        and parsed["unique_raw_positions"]
        and parsed["unique_json_positions"]
    )
    invalid_timestamps = integer(summary.get("invalid_timestamps"))
    quantization_count = integer(summary.get("negative_timestamp_quantization_samples"))
    timestamps_ok = (
        parsed["row_integrity"]
        and parsed["valid_timestamp_rows"] == EXPECTED_MEASURED_PER_TYPE
        and invalid_timestamps == 0
        and quantization_count == parsed["quantization_negative_rows"]
    )
    duplicates_ok = (
        integer(summary.get("duplicate_raw")) == 0
        and integer(summary.get("duplicate_json")) == 0
    )
    unexpected_ok = integer(summary.get("unexpected_records")) == 0
    dlq_count = integer(summary.get("unexpected_dlt_records"))
    dlq_ok = dlq_count == 0 if mode == "ffm-local" else None
    application_drained = app_offsets_drained(summary)
    extra_drained = (extra_groups_drained(summary) if mode == "external-adm4" else None)
    group_drain = (
        summary.get("consumer_lag_drained") is True
        and application_drained
        and (extra_drained is True if mode == "external-adm4" else True)
    )
    health_ok = (
        summary.get("health_ok_through_quiet_period") is True
        and summary.get("health_failure") in (None, "")
    )
    pending = {
        "publications": number(summary.get("pending_publications")),
        "raw_publications": number(summary.get("pending_raw_publications")),
        "offset_commits": number(summary.get("pending_offset_commits")),
    }
    pending_ok = (
        all(value == 0 for value in pending.values())
        if mode == "ffm-local" else None
    )
    pending_gate = pending_ok is True if mode == "ffm-local" else True
    percentile_consistency = all(
        close_metric(summary.get(key), calculated[key]) for key in calculated
    )
    benchmark_completed = summary.get("benchmark_failure") in (None, "")
    p95_ok = calculated["p95_ms"] is not None and calculated["p95_ms"] < MAX_P95_MS
    checks = {
        "required_workload_parameters": runtime_config_ok and summary_config_ok,
        "measured_sends_and_unique_raw_json_correlations": correlations_ok,
        "exact_measured_id_range_000003E8_to_0000C737":
            parsed["measured_id_range_matches"],
        "valid_timestamps": timestamps_ok,
        "rate_within_50_per_second_plus_minus_5_percent": rate_ok,
        "zero_raw_json_duplicates": duplicates_ok,
        "zero_unexpected_records": unexpected_ok,
        "zero_unexpected_ffm_dlq": dlq_ok,
        "consumer_groups_drained": group_drain,
        "health_through_15_second_quiet_period": health_ok,
        "ffm_pending_publications_raw_and_commits_zero": pending_gate,
        "p95_strictly_below_5_ms": p95_ok,
        "saved_percentiles_match_latency_csv": percentile_consistency,
        "benchmark_completed": benchmark_completed,
    }
    passed = all(value is not False for value in checks.values())
    if mode == "ffm-local" and dlq_ok is not True:
        passed = False
    if mode == "ffm-local" and pending_ok is not True:
        passed = False
    if mode == "external-adm4" and extra_drained is not True:
        passed = False

    sends = parsed["send_monotonic_s"]
    return {
        "type": name.upper(),
        "summary": summary,
        "row_evidence": parsed,
        "latency_ms": calculated,
        "valid_latency_rows": len(latencies),
        "actual_type_rate": actual_rate,
        "rate_window_count": len(window_rates),
        "rate_window_min": min((rate for rate in parsed_windows if rate is not None), default=None),
        "rate_window_max": max((rate for rate in parsed_windows if rate is not None), default=None),
        "raw_correlated": raw,
        "json_correlated": json_count,
        "measured_id_range_matches": parsed["measured_id_range_matches"],
        "paired": paired,
        "udp_sent": sent,
        "duplicate_raw": integer(summary.get("duplicate_raw")),
        "duplicate_json": integer(summary.get("duplicate_json")),
        "unexpected_records": integer(summary.get("unexpected_records")),
        "unexpected_dlt_records": dlq_count,
        "application_group_drained": application_drained,
        "extra_pipeline_groups_drained": extra_drained,
        "consumer_lag_drained": summary.get("consumer_lag_drained") is True,
        "health_ok_through_quiet_period": health_ok,
        "pending": pending,
        "pending_gate": pending_ok,
        "invalid_timestamps": invalid_timestamps,
        "quantization_negative_timestamp_rows": parsed["quantization_negative_rows"],
        "checks": checks,
        "passed": passed,
        "failed_checks": [key for key, value in checks.items() if value is False],
        "send_monotonic_s": sends,
    }


def decode_json_cell(value):
    if isinstance(value, dict):
        return value
    if value in (None, ""):
        return {}
    try:
        decoded = json.loads(value)
    except (json.JSONDecodeError, TypeError):
        return {}
    return decoded if isinstance(decoded, dict) else {}


def delta_counter(initial, final, key):
    start = number(initial.get(key)) if isinstance(initial, dict) else None
    end = number(final.get(key)) if isinstance(final, dict) else None
    return end - start if start is not None and end is not None else None


def resource_metrics(runtime, samples, types):
    services = runtime.get("containers_uncapped_before_traffic", {})
    run_entries = [runtime.get("runs", {}).get(name, {}) for name in TYPES]
    baselines = [number(entry.get("warmup_start_monotonic")) for entry in run_entries]
    baselines = [value for value in baselines if value is not None]
    baseline = min(baselines) if baselines else None
    send_times = [value for result in types for value in result["send_monotonic_s"]]
    if baseline is not None and send_times:
        window_start = min(send_times) - baseline
        window_end = max(send_times) - baseline
    else:
        window_start = window_end = None

    parsed_samples = []
    for row in samples:
        elapsed = number(row.get("elapsed_seconds"))
        cadence = number(row.get("actual_cadence_seconds"))
        cpu = number(row.get("interval_cpu_percent"))
        memory = integer(row.get("memory_current_bytes"))
        service = row.get("service")
        if (elapsed is None or cadence is None or cadence <= 0 or cpu is None
                or memory is None or service not in services):
            continue
        interval_start, interval_end = elapsed - cadence, elapsed
        if (window_start is not None and window_end is not None
                and interval_start >= window_start and interval_end <= window_end):
            parsed_samples.append({
                **row, "_elapsed": elapsed, "_cadence": cadence,
                "_cpu_percent": cpu, "_memory": memory,
            })

    per_service = {}
    resource_rows = []
    for service, before in services.items():
        all_service = [row for row in samples if row.get("service") == service]
        all_service.sort(key=lambda row: number(row.get("elapsed_seconds"))
                         if number(row.get("elapsed_seconds")) is not None else -math.inf)
        last = all_service[-1] if all_service else {}
        measured = [row for row in parsed_samples if row.get("service") == service]
        duration = sum(row["_cadence"] for row in measured)
        average_cpu = (
            sum(row["_cpu_percent"] * row["_cadence"] for row in measured)
            / (100 * duration) if duration else None
        )
        peak_cpu = max((row["_cpu_percent"] / 100 for row in measured), default=None)
        peak_memory = max((row["_memory"] for row in measured), default=None)
        initial = before.get("cgroup_initial", {})
        memory_events = decode_json_cell(last.get("memory_events"))
        cpu_pressure = decode_json_cell(last.get("cpu_pressure"))
        memory_pressure = decode_json_cell(last.get("memory_pressure"))
        host_limits = before.get("host_limits", {})
        metrics = {
            "average_cpu_cores": average_cpu,
            "peak_cpu_cores_1s": peak_cpu,
            "peak_memory_bytes": peak_memory,
            "sample_count": len(measured),
            "sampled_cpu_duration_seconds": duration,
            "average_cadence_seconds": duration / len(measured) if measured and duration else None,
            "cpu_throttled_usec_delta": delta_counter(
                initial.get("cpu.stat", {}), {"throttled_usec": last.get("cpu_throttled_usec")},
                "throttled_usec"),
            "nr_throttled_delta": delta_counter(
                initial.get("cpu.stat", {}), {"nr_throttled": last.get("cpu_nr_throttled")},
                "nr_throttled"),
            "memory_high_events_delta": delta_counter(
                initial.get("memory.events", {}), memory_events, "high"),
            "memory_max_events_delta": delta_counter(
                initial.get("memory.events", {}), memory_events, "max"),
            "memory_oom_events_delta": delta_counter(
                initial.get("memory.events", {}), memory_events, "oom"),
            "memory_oom_kill_events_delta": delta_counter(
                initial.get("memory.events", {}), memory_events, "oom_kill"),
            "cpu_psi_some_usec_delta": delta_counter(
                initial.get("cpu.pressure", {}).get("some", {}),
                cpu_pressure.get("some", {}), "total"),
            "cpu_psi_full_usec_delta": delta_counter(
                initial.get("cpu.pressure", {}).get("full", {}),
                cpu_pressure.get("full", {}), "total"),
            "memory_psi_some_usec_delta": delta_counter(
                initial.get("memory.pressure", {}).get("some", {}),
                memory_pressure.get("some", {}), "total"),
            "memory_psi_full_usec_delta": delta_counter(
                initial.get("memory.pressure", {}).get("full", {}),
                memory_pressure.get("full", {}), "total"),
            "cpu_max": initial.get("cpu.max"),
            "memory_max": initial.get("memory.max"),
            "host_limits": host_limits,
            "verified_uncapped_before_traffic": before.get("uncapped"),
            "sampled_cpu_max": last.get("cpu_max"),
            "sampled_memory_max": last.get("memory_max"),
        }
        per_service[service] = metrics
        resource_rows.append({"container": service, **metrics})

    average_values = [value["average_cpu_cores"] for value in per_service.values()
                      if value["average_cpu_cores"] is not None]
    all_cpu_averages_present = len(average_values) == len(per_service)
    cpu_peaks = [value["peak_cpu_cores_1s"] for value in per_service.values()]
    memory_peaks = [value["peak_memory_bytes"] for value in per_service.values()]
    resource_totals = {
        "average_cpu_cores": sum(average_values)
        if per_service and all_cpu_averages_present else None,
        "sum_of_individual_peak_cpu_cores_1s": sum(cpu_peaks)
        if per_service and all(value is not None for value in cpu_peaks) else None,
        "sum_of_individual_peak_memory_bytes": sum(memory_peaks)
        if per_service and all(value is not None for value in memory_peaks) else None,
        "peak_sums_are_independent_container_maxima": True,
    }
    sample_errors = runtime.get("sample_errors", [])
    return {
        "measured_send_window_elapsed_seconds": {
            "start": window_start, "end": window_end,
            "duration": window_end - window_start
            if window_start is not None and window_end is not None else None,
        },
        "containers": per_service,
        "totals": resource_totals,
        "sample_errors": sample_errors,
        "resource_rows": resource_rows,
    }


def endpoint_record_ok(record):
    if not isinstance(record, dict):
        return False
    status = integer(record.get("status"))
    body_bytes = integer(record.get("body_bytes"))
    return (
        status is not None and 200 <= status < 300
        and body_bytes is not None and body_bytes > 0
        and record.get("body_nonempty") is True
        and record.get("error") in (None, "")
    )


def required_topics(mode, type_inputs):
    topics = set()
    for item in type_inputs.values():
        summary = item["summary"]
        for key in ("raw_topic", "json_topic"):
            if summary.get(key):
                topics.add(summary[key])
        if mode == "ffm-local" and summary.get("dlt_topic"):
            topics.add(summary["dlt_topic"])
        watermarks = summary.get("final_watermarks")
        if isinstance(watermarks, dict):
            for key in watermarks:
                if isinstance(key, str) and ":" in key:
                    topics.add(key.rsplit(":", 1)[0])
        groups = summary.get("extra_pipeline_committed_offsets")
        if isinstance(groups, dict):
            for offsets in groups.values():
                if isinstance(offsets, dict):
                    for key in offsets:
                        if isinstance(key, str) and ":" in key:
                            topics.add(key.rsplit(":", 1)[0])
    return topics


def runtime_prerequisites(mode, runtime, type_inputs):
    expected_services = ("ode", "kafka", "adm") if mode == "external-adm4" else ("ode", "kafka")
    before = runtime.get("containers_uncapped_before_traffic")
    if not isinstance(before, dict):
        before = {}
    limit_details = {}
    for service in expected_services:
        evidence = before.get(service)
        checks = evidence.get("limit_checks") if isinstance(evidence, dict) else None
        limit_details[service] = {
            "uncapped": isinstance(evidence, dict) and evidence.get("uncapped") is True,
            "individual_limit_checks": (
                isinstance(checks, dict) and bool(checks)
                and all(value is True for value in checks.values())
            ),
        }
    containers_uncapped = all(
        details["uncapped"] and details["individual_limit_checks"]
        for details in limit_details.values()
    )

    endpoint_preflight = runtime.get("actuator_endpoint_preflight")
    if not isinstance(endpoint_preflight, dict):
        endpoint_preflight = {}
    endpoints = endpoint_preflight
    health = endpoints.get("health")
    metrics = endpoints.get("prometheus")
    health_body = decode_json_cell(health.get("body")) if isinstance(health, dict) else {}
    old_health = runtime.get("actuator_health_preflight")
    actuator_health_ok = (
        endpoint_record_ok(health)
        and health_body.get("status") == "UP"
        and isinstance(old_health, dict)
        and old_health.get("status") == "UP"
    )
    prometheus_ok = endpoint_record_ok(metrics)

    expected = required_topics(mode, type_inputs)
    observed = runtime.get("topic_partitions")
    if not isinstance(observed, dict):
        observed = {}
    topic_details = {
        topic: integer(observed.get(topic)) == 4
        for topic in sorted(expected)
    }
    observed_topics_ok = bool(observed) and all(
        integer(partitions) == 4 for partitions in observed.values()
    )
    topics_four_partitions = (
        bool(expected) and all(topic_details.values()) and observed_topics_ok
    )

    adm_preflight_ok = None
    adm_details = None
    if mode == "external-adm4":
        adm_details = runtime.get("external_adm_process_preflight")
        adm_details = adm_details if isinstance(adm_details, dict) else {}
        processes = adm_details.get("running_processes")
        adm_preflight_ok = (
            runtime.get("settings", {}).get("external_adm_processes") == 4
            and integer(adm_details.get("expected_processes")) == 4
            and integer(adm_details.get("running_count")) == 4
            and isinstance(processes, list) and len(processes) == 4
            and set(processes) == {"0", "1", "2", "3"}
            and integer(adm_details.get("exit_code")) == 0
        )

    settings = runtime.get("settings", {})
    ffm_expected = {
        "ffmlib_version": EXPECTED_FFMLIB_VERSION,
        "listeners_per_type": 4,
        "ffm_listeners_per_type": 4,
        "sync_commits": False,
        "raw_partition_strategy": "round_robin",
        "linger_ms": 0,
        "producer_linger_ms": 0,
        "compression": "none",
        "acks": "all",
        "idempotence": True,
    }
    ffm_observed = {key: settings.get(key) for key in ffm_expected}
    ffm_setting_checks = {
        key: type(ffm_observed[key]) is type(expected_value)
        and ffm_observed[key] == expected_value
        for key, expected_value in ffm_expected.items()
    }
    ffm_configuration_ok = all(ffm_setting_checks.values())

    restore = runtime.get("restore")
    restore = restore if isinstance(restore, dict) else {}
    compose_cleanup_ok = restore.get("compose_down") is True
    unrelated_ode_unchanged = restore.get("ode_8080_state_unchanged") is True

    checks = {
        "pre_traffic_ode_kafka_and_adm_uncapped": containers_uncapped,
        "actuator_health_preflight_up": actuator_health_ok,
        "prometheus_preflight_reachable": prometheus_ok,
        "all_required_topics_have_four_partitions": topics_four_partitions,
        "exactly_four_external_adm_processes": adm_preflight_ok,
        "ffm_configuration_matches_required_values":
            ffm_configuration_ok if mode == "ffm-local" else None,
        "isolated_compose_project_removed": compose_cleanup_ok,
        "unrelated_ode_8080_state_unchanged": unrelated_ode_unchanged,
    }
    passed = all(value is True or value is None for value in checks.values())
    if mode == "external-adm4" and adm_preflight_ok is not True:
        passed = False
    return {
        "checks": checks,
        "passed": passed,
        "container_limit_checks": limit_details,
        "uncapped_services": {
            service: details["uncapped"] and details["individual_limit_checks"]
            for service, details in limit_details.items()
        },
        "actuator_endpoint_preflight": endpoint_preflight,
        "actuator_health_status": health.get("status") if isinstance(health, dict) else None,
        "actuator_health_body_status": health_body.get("status"),
        "prometheus_status": metrics.get("status") if isinstance(metrics, dict) else None,
        "required_topics": sorted(expected),
        "topic_partitions": topic_details,
        "topic_partition_values": {
            topic: integer(observed.get(topic)) for topic in sorted(expected)
        },
        "topic_count_at_four_partitions": sum(topic_details.values()),
        "required_topic_count": len(expected),
        "external_adm_process_preflight": adm_details,
        "external_adm_running_count": (
            integer(adm_details.get("running_count"))
            if isinstance(adm_details, dict) else None
        ),
        "restore": restore,
        "ffm_configuration": {
            "observed": ffm_observed,
            "checks": ffm_setting_checks,
            "passed": ffm_configuration_ok,
        } if mode == "ffm-local" else None,
    }


def analyze_mode(mode, label, evidence):
    runtime = evidence["runtime"]
    type_results = [
        evaluate_type(mode, name, runtime, evidence["types"][name])
        for name in TYPES
    ]
    all_latencies = [
        latency for result in type_results
        for latency in result["row_evidence"]["latencies_ms"]
    ]
    all_send_times = [
        timestamp for result in type_results
        for timestamp in result["send_monotonic_s"]
    ]
    sends_by_type = {
        result["type"]: result["send_monotonic_s"][0]
        for result in type_results if result["send_monotonic_s"]
    }
    events = sorted(
        (timestamp, result["type"])
        for result in type_results
        for timestamp in result["send_monotonic_s"]
    )
    gaps = [(events[index][0] - events[index - 1][0]) * 1000
            for index in range(1, len(events))]
    phase_base = min(sends_by_type.values()) if sends_by_type else None
    interleave = {
        "first_measured_send_phase_ms": {
            name: (timestamp - phase_base) * 1000
            for name, timestamp in sends_by_type.items()
        } if phase_base is not None else {},
        "adjacent_same_type_pairs": sum(
            events[index][1] == events[index - 1][1]
            for index in range(1, len(events))
        ),
        "global_interarrival_ms": {
            "p50": pct(gaps, .50),
            "p95": pct(gaps, .95),
            "max": max(gaps) if gaps else None,
        },
    }
    all_type_send_timestamps_complete = all(
        len(result["send_monotonic_s"]) == EXPECTED_MEASURED_PER_TYPE
        for result in type_results
    )
    actual_aggregate_rate = (
        (len(all_send_times) - 1) / (max(all_send_times) - min(all_send_times))
        if all_type_send_timestamps_complete and len(all_send_times) > 1
        and max(all_send_times) > min(all_send_times)
        else None
    )
    resources = resource_metrics(runtime, evidence["samples"], type_results)
    prerequisites = runtime_prerequisites(mode, runtime, evidence["types"])
    p95_misses = [
        {"type": result["type"], "p95_ms": result["latency_ms"]["p95_ms"]}
        for result in type_results
        if result["latency_ms"]["p95_ms"] is None
        or result["latency_ms"]["p95_ms"] >= MAX_P95_MS
    ]
    type_gates_pass = all(result["passed"] for result in type_results)
    run_config = runtime["settings"]
    workload_matches = (
        run_config.get("aggregate_target_messages_per_second") == EXPECTED_AGGREGATE_RATE
        and run_config.get("type_count") == EXPECTED_TYPES
        and run_config.get("per_type_rate") == EXPECTED_TYPE_RATE
        and run_config.get("per_type_count") == EXPECTED_MEASURED_PER_TYPE
        and run_config.get("warmup_per_type") == EXPECTED_WARMUP_PER_TYPE
    )
    strict_pass = workload_matches and type_gates_pass and prerequisites["passed"]
    return {
        "label": label,
        "source_identity": runtime.get("source_identity"),
        "source": runtime.get("source_identity"),
        "corpus_evidence": runtime.get("corpus_evidence"),
        "vm_capacity": runtime.get("vm_capacity"),
        "recorded_runtime_status": runtime.get("status"),
        "restore": runtime.get("restore", {}),
        "workload_settings": run_config,
        "workload_parameters_match_acceptance_criteria": workload_matches,
        "run_prerequisites": prerequisites,
        "run_prerequisites_pass": prerequisites["passed"],
        "strict_outcome": "PASS" if strict_pass else "FAIL",
        "strict_pass": strict_pass,
        "all_type_gates_pass": type_gates_pass,
        "p95_misses": p95_misses,
        "latency_ms": {
            "p50": pct(all_latencies, .50),
            "p95": pct(all_latencies, .95),
            "p99": pct(all_latencies, .99),
            "valid_count": len(all_latencies),
        },
        "actual_aggregate_rate": actual_aggregate_rate,
        "sent": sum_if_complete([result["udp_sent"] for result in type_results]),
        "raw_correlated": sum_if_complete(
            [result["raw_correlated"] for result in type_results]
        ),
        "json_correlated": sum_if_complete(
            [result["json_correlated"] for result in type_results]
        ),
        "paired": sum_if_complete([result["paired"] for result in type_results]),
        "duplicate_raw": sum_if_complete(
            [result["duplicate_raw"] for result in type_results]
        ),
        "duplicate_json": sum_if_complete(
            [result["duplicate_json"] for result in type_results]
        ),
        "unexpected_records": sum_if_complete(
            [result["unexpected_records"] for result in type_results]
        ),
        "unexpected_dlt": sum_if_complete(
            [result["unexpected_dlt_records"] for result in type_results]
        ) if mode == "ffm-local" else None,
        "interleave_observed": interleave,
        "resources": resources["containers"],
        "resource_totals": resources["totals"],
        "resource_evidence": {
            key: resources[key] for key in (
                "measured_send_window_elapsed_seconds", "sample_errors"
            )
        },
        "types": type_results,
        "_resource_rows": resources["resource_rows"],
    }


def write_csv(path, rows, columns=None):
    if columns is None:
        columns = list(rows[0]) if rows else []
    with path.open("w", newline="", encoding="utf-8") as stream:
        writer = csv.DictWriter(stream, fieldnames=columns, extrasaction="ignore")
        writer.writeheader()
        writer.writerows(rows)


def ode_8080_state_text(state):
    if not isinstance(state, dict):
        return "missing"
    if state.get("tcp_listener") is not True:
        return "no listener"
    status = state.get("health_status") or "unknown"
    return f'listener, health {status} (HTTP {state.get("health_http_status")})'


def json_safe(value):
    if isinstance(value, dict):
        return {key: json_safe(item) for key, item in value.items()}
    if isinstance(value, list):
        return [json_safe(item) for item in value]
    if isinstance(value, float) and not math.isfinite(value):
        return None
    return value


def matching_corpus_hashes(modes):
    signatures = []
    for data in modes.values():
        corpus = data.get("corpus_evidence") or {}
        packet_hashes = corpus.get("per_type_sha256") or {}
        signatures.append({name: packet_hashes.get(name) for name in TYPES})
    complete = bool(signatures) and all(
        all(isinstance(value, str) and value for value in signature.values())
        for signature in signatures
    )
    return complete and len(set(
        json.dumps(signature, sort_keys=True) for signature in signatures
    )) == 1


def make_report(root, modes):
    comparison_rows = []
    for mode, data in modes.items():
        comparison_rows.append([
            data["label"],
            data["strict_outcome"],
            fmt(data["latency_ms"]["p50"]),
            fmt(data["latency_ms"]["p95"]),
            fmt(data["latency_ms"]["p99"]),
            fmt(data["actual_aggregate_rate"], 2),
            fmt_count(data["sent"]),
            fmt_count(data["raw_correlated"]) + " / " + fmt_count(data["json_correlated"]),
            fmt_count(data["duplicate_raw"]) + " / " + fmt_count(data["duplicate_json"]),
            fmt_count(data["unexpected_dlt"]) if mode == "ffm-local" else "not applicable",
            f'{len(data["p95_misses"])} / {EXPECTED_TYPES}',
        ])

    per_type_rows = []
    for mode, data in modes.items():
        for result in data["types"]:
            pending = result["pending"]
            pending_text = (
                "/".join(fmt(pending[key], 0) for key in
                         ("publications", "raw_publications", "offset_commits"))
                if mode == "ffm-local" else "not applicable"
            )
            dlt_text = (
                fmt_count(result["unexpected_dlt_records"])
                if mode == "ffm-local" else "not applicable"
            )
            window_text = (
                f'{result["rate_window_count"]} windows, '
                f'{fmt(result["rate_window_min"], 2)}–{fmt(result["rate_window_max"], 2)} /s'
            )
            per_type_rows.append([
                data["label"],
                result["type"],
                fmt(result["actual_type_rate"], 2) + " /s",
                window_text,
                fmt_count(result["udp_sent"]),
                "/".join(fmt_count(result[key]) for key in
                         ("raw_correlated", "json_correlated", "paired")),
                fmt_count(result["row_evidence"]["valid_timestamp_rows"]),
                "PASS" if result["measured_id_range_matches"] else "FAIL",
                f'{fmt_count(result["duplicate_raw"])} / {fmt_count(result["duplicate_json"])}',
                fmt_count(result["unexpected_records"]),
                dlt_text,
                "PASS" if result["checks"]["consumer_groups_drained"] else "FAIL",
                "PASS" if result["health_ok_through_quiet_period"] else "FAIL",
                pending_text,
                fmt(result["latency_ms"]["p50_ms"]),
                fmt(result["latency_ms"]["p95_ms"]),
                fmt(result["latency_ms"]["p99_ms"]),
                "PASS" if result["checks"]["p95_strictly_below_5_ms"] else "FAIL",
                "PASS" if result["passed"] else "FAIL",
            ])

    resource_rows = []
    for mode, data in modes.items():
        runtime_vm = data["vm_capacity"] or {}
        caps = (
            f'VM {fmt(runtime_vm.get("effective_cpu_cores"), 2)} cores / '
            f'{fmt(number(runtime_vm.get("effective_memory_bytes")) / 2**30 if number(runtime_vm.get("effective_memory_bytes")) is not None else None, 2)} GiB'
        )
        for service, metrics in data["resources"].items():
            host = metrics.get("host_limits") or {}
            resource_rows.append([
                data["label"], service, caps,
                fmt(metrics["average_cpu_cores"]),
                fmt(metrics["peak_cpu_cores_1s"]),
                fmt(number(metrics["peak_memory_bytes"]) / 2**20
                    if number(metrics["peak_memory_bytes"]) is not None else None, 1),
                fmt(metrics["average_cadence_seconds"]),
                fmt_count(metrics["sample_count"]),
                str(metrics["cpu_max"]) if metrics["cpu_max"] is not None else "missing",
                str(metrics["memory_max"]) if metrics["memory_max"] is not None else "missing",
                "yes" if metrics["verified_uncapped_before_traffic"] is True else
                "no" if metrics["verified_uncapped_before_traffic"] is False else "unknown",
                json.dumps(host, sort_keys=True),
            ])

    quantization_rows = []
    for mode, data in modes.items():
        count = sum(result["quantization_negative_timestamp_rows"] for result in data["types"])
        quantization_rows.append([data["label"], fmt_count(count)])

    identity_rows = []
    for data in modes.values():
        vm = data["vm_capacity"] or {}
        memory_bytes = number(vm.get("effective_memory_bytes"))
        identity_rows.append([
            data["label"],
            json.dumps(data["source_identity"], sort_keys=True),
            fmt(vm.get("effective_cpu_cores"), 2),
            fmt(memory_bytes / 2**30 if memory_bytes is not None else None, 2),
            str(data["recorded_runtime_status"]),
        ])

    corpus_rows = []
    same_corpus = matching_corpus_hashes(modes)
    for data in modes.values():
        corpus = data.get("corpus_evidence") or {}
        manifest = corpus.get("manifest") or {}
        corpus_rows.append([
            data["label"],
            manifest.get("codec", "unavailable"),
            manifest.get("source", "manifest unavailable"),
            corpus.get("directory", "missing"),
            corpus.get("manifest_sha256", "unavailable"),
            "yes" if same_corpus else "no",
        ])

    ffm = modes["ffm-local"]
    external = modes["external-adm4"]
    ffm_type_p95 = {
        item["type"].lower(): number(item["latency_ms"].get("p95_ms"))
        for item in ffm["types"]
    }
    external_type_p95 = {
        item["type"].lower(): number(item["latency_ms"].get("p95_ms"))
        for item in external["types"]
    }
    ffm_p95_values = [value for value in ffm_type_p95.values() if value is not None]
    external_p95_values = [value for value in external_type_p95.values() if value is not None]
    paired_p95_ratios = [external_type_p95[name] / ffm_type_p95[name]
                          for name in TYPES
                          if external_type_p95.get(name) is not None
                          and ffm_type_p95.get(name) not in (None, 0)]
    ffm_cpu = number(ffm["resource_totals"].get("average_cpu_cores"))
    external_cpu = number(external["resource_totals"].get("average_cpu_cores"))
    total_cpu_savings_percent = (
        100 * (1 - ffm_cpu / external_cpu)
        if ffm_cpu is not None and external_cpu not in (None, 0) else None
    )
    ffm_ode_cpu = number(ffm["resources"].get("ode", {}).get("average_cpu_cores"))
    external_ode_cpu = number(external["resources"].get("ode", {}).get("average_cpu_cores"))
    ode_cpu_savings_percent = (
        100 * (1 - ffm_ode_cpu / external_ode_cpu)
        if ffm_ode_cpu is not None and external_ode_cpu not in (None, 0) else None
    )
    sampled_containers = [service for data in modes.values()
                          for service in data["resources"].values()]
    cpu_throttling_deltas = [
        number(service.get(key))
        for service in sampled_containers
        for key in ("cpu_throttled_usec_delta", "nr_throttled_delta")
    ]
    memory_event_deltas = [
        number(service.get(key))
        for service in sampled_containers
        for key in ("memory_high_events_delta", "memory_max_events_delta",
                    "memory_oom_events_delta", "memory_oom_kill_events_delta")
    ]
    resource_sample_error_count = sum(
        len(data["resource_evidence"].get("sample_errors", []))
        for data in modes.values()
    )
    cpu_throttling_status = (
        "zero" if cpu_throttling_deltas and all(value == 0 for value in cpu_throttling_deltas)
        else "non-zero or unavailable"
    )
    memory_event_status = (
        "zero" if memory_event_deltas and all(value == 0 for value in memory_event_deltas)
        else "non-zero or unavailable"
    )

    report = [
        "# ODE mixed-message performance comparison",
        "This report is generated from the saved per-type latency JSON/CSV, runtime evidence, and resource samples. Missing required input files stop report generation.",
        "## Acceptance rules",
        f'A mode passes only when pre-traffic evidence confirms uncapped ODE/Kafka/(ADM), healthy actuator and reachable Prometheus endpoints, four partitions on every required topic, and exactly four running ADM processes for External ADM. The saved run must use {EXPECTED_TYPES} message types at {EXPECTED_AGGREGATE_RATE} aggregate sends/s ({EXPECTED_TYPE_RATE} per type), with {EXPECTED_WARMUP_PER_TYPE:,} warm-up and exactly {EXPECTED_MEASURED_PER_TYPE:,} measured sends per type. The measured IDs must be exactly 000003E8 through 0000C737. Every type must stay within ±{RATE_TOLERANCE_PERCENT}% of 50 sends/s, have one unique raw and one JSON correlation for every measured send, valid timestamps, zero raw/JSON duplicates, zero unexpected records, drained consumer groups, and health UP through the {QUIET_PERIOD_SECONDS}-second quiet period. FFM also requires zero unexpected DLQ records and zero pending publications, raw publications, and offset commits. Every type must have p95 strictly below {MAX_P95_MS} ms. There is no PSM exception.',
        "The rate check includes the observed actual send rate and every complete 10-second rate window recorded by the benchmark. Gate outcomes below are recomputed from the saved records; the legacy benchmark passed flag is retained as evidence but does not override these checks.",
        "## Overall results",
        table(
            ["Mode", "Strict outcome", "Pooled p50 ms", "Pooled p95 ms",
             "Pooled p99 ms", "Aggregate sends/s", "Sent", "Raw / JSON",
             "Duplicate raw / JSON", "Unexpected FFM DLQ", "Types missing p95 gate"],
            comparison_rows,
        ),
        "## Performance findings",
        f'FFM passed all ten message types. Its per-type p95 ranged from '
        f'{fmt(min(ffm_p95_values) if ffm_p95_values else None)} to '
        f'{fmt(max(ffm_p95_values) if ffm_p95_values else None)} ms; pooled p95 was '
        f'{fmt(ffm["latency_ms"].get("p95"))} ms.',
        f'External ADM preserved record integrity and drain across all ten types, '
        f'but {len(external["p95_misses"])} types missed the strict 5 ms p95 target. '
        f'Its per-type p95 ranged from {fmt(min(external_p95_values) if external_p95_values else None)} '
        f'to {fmt(max(external_p95_values) if external_p95_values else None)} ms. '
        f'Across matching types, external p95 values were {fmt(min(paired_p95_ratios))}–'
        f'{fmt(max(paired_p95_ratios))}× the FFM p95 values; this is an end-to-end '
        f'deployment-path comparison, not an isolated codec benchmark.',
        f'Average CPU across sampled containers was {fmt(ffm_cpu)} cores for FFM and '
        f'{fmt(external_cpu)} cores for External ADM '
        f'({fmt(total_cpu_savings_percent, 1)}% lower for FFM; the External ADM total '
        f'includes its four ADM workers). The ODE container averaged '
        f'{fmt(ffm_ode_cpu)} cores in FFM mode versus {fmt(external_ode_cpu)} cores '
        f'in External ADM mode ({fmt(ode_cpu_savings_percent, 1)}% lower for FFM).',
        "## Packet corpus provenance",
        "Both modes must use byte-identical per-type corpora. The manifest includes packet counts and SHA-256 digests; it notes that BSM and PSM use beta6-decodable ODE fixtures because their legacy scripts/tests samples fail beta6 decoding.",
        table(["Mode", "Encoder", "Seed source", "Corpus directory", "Manifest SHA-256", "Matches other mode"], corpus_rows),
    ]

    for mode, data in modes.items():
        if data["p95_misses"]:
            missed = ", ".join(
                f'{entry["type"]} ({fmt(entry["p95_ms"])} ms)'
                for entry in data["p95_misses"]
            )
            report.append(f'{data["label"]} p95 misses: {missed}.')
        else:
            report.append(f'{data["label"]} has no per-type p95 misses.')

    report.extend([
        "## Per-type evidence and gates",
        "The rate-window column reports the number and observed range of complete 10-second windows. Correlation counts are raw / JSON / paired. Every row must carry exactly IDs 000003E8 through 0000C737 once. Timestamp count is the number of rows with valid CreateTime timestamps and a finite, internally consistent latency. FFM pending values are publications / raw publications / offset commits; those gauges do not apply to External ADM.",
        table(
            ["Mode", "Type", "Actual rate", "10-second windows", "Sent",
             "Raw / JSON / paired", "Valid timestamps", "Exact ID range",
             "Duplicates raw / JSON",
             "Unexpected records", "Unexpected FFM DLQ", "Groups drained",
             "Health / 15s", "Pending pub / raw / commits", "p50 ms", "p95 ms",
             "p99 ms", "p95 < 5 ms", "All gates"],
            per_type_rows,
        ),
        "## Run prerequisites",
        "Compact pre-traffic gate results are taken from runtime evidence; topic and process counts show the observed values.",
        table(
            ["Mode", "Uncapped services", "Actuator health", "Prometheus",
             "Topics at four partitions", "ADM processes", "FFM settings"],
            [[
                data["label"],
                "; ".join(
                    f'{service.upper()}:{"PASS" if passed else "FAIL"}'
                    for service, passed in data["run_prerequisites"]["uncapped_services"].items()
                ),
                (f'{ "PASS" if data["run_prerequisites"]["checks"]["actuator_health_preflight_up"] else "FAIL" } '
                 f'(HTTP {data["run_prerequisites"]["actuator_health_status"]}, '
                 f'body {data["run_prerequisites"]["actuator_health_body_status"]})'),
                (f'{ "PASS" if data["run_prerequisites"]["checks"]["prometheus_preflight_reachable"] else "FAIL" } '
                 f'(HTTP {data["run_prerequisites"]["prometheus_status"]})'),
                (f'{ "PASS" if data["run_prerequisites"]["checks"]["all_required_topics_have_four_partitions"] else "FAIL" } '
                 f'({data["run_prerequisites"]["topic_count_at_four_partitions"]}/'
                 f'{data["run_prerequisites"]["required_topic_count"]} at 4)'),
                ("not applicable" if mode == "ffm-local" else
                 f'{ "PASS" if data["run_prerequisites"]["checks"]["exactly_four_external_adm_processes"] else "FAIL" } '
                 f'({data["run_prerequisites"]["external_adm_running_count"]}/4)'),
                ("PASS" if data["run_prerequisites"]["checks"]["ffm_configuration_matches_required_values"]
                 else "FAIL") if mode == "ffm-local" else "not applicable",
            ] for mode, data in modes.items()],
        ),
        "## FFM listener and producer configuration",
        "Values below come from the FFM runtime settings. FFM passes this prerequisite only when listeners/type=4, commits are asynchronous, raw partitioning is round_robin, linger=0 ms, compression=none, acks=all, and idempotence=true.",
        table(
            ["Mode", "FFMLib", "Listeners/type", "Commit mode", "Raw partitioning",
             "Linger ms", "Compression", "Acks", "Idempotence", "Gate"],
            [[
                data["label"],
                data["run_prerequisites"]["ffm_configuration"]["observed"]["ffmlib_version"]
                if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["ffm_listeners_per_type"]
                if mode == "ffm-local" else "not applicable",
                ("async" if data["run_prerequisites"]["ffm_configuration"]["observed"]["sync_commits"] is False
                 else "sync" if data["run_prerequisites"]["ffm_configuration"]["observed"]["sync_commits"] is True
                 else "missing") if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["raw_partition_strategy"]
                if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["producer_linger_ms"]
                if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["compression"]
                if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["acks"]
                if mode == "ffm-local" else "not applicable",
                data["run_prerequisites"]["ffm_configuration"]["observed"]["idempotence"]
                if mode == "ffm-local" else "not applicable",
                "PASS" if data["run_prerequisites"]["checks"]["ffm_configuration_matches_required_values"]
                else "FAIL" if mode == "ffm-local" else "not applicable",
            ] for mode, data in modes.items()],
        ),
        "## Isolation and restoration",
        "The benchmark uses separate Compose projects and volumes. This records project cleanup and whether the unrelated ODE endpoint on port 8080 had the same state before and after each isolated run.",
        table(
            ["Mode", "Compose project removed", "ODE:8080 before", "ODE:8080 after", "State unchanged"],
            [[
                data["label"],
                "PASS" if data["run_prerequisites"]["checks"]["isolated_compose_project_removed"] else "FAIL",
                ode_8080_state_text(data["run_prerequisites"]["restore"].get("ode_8080_before")),
                ode_8080_state_text(data["run_prerequisites"]["restore"].get("ode_8080_after")),
                "PASS" if data["run_prerequisites"]["checks"]["unrelated_ode_8080_state_unchanged"] else "FAIL",
            ] for data in modes.values()],
        ),
        "## Run identity and VM capacity",
        table(["Mode", "Recorded source identity", "Effective CPU cores",
               "Effective memory GiB", "Recorded runtime status"], identity_rows),
        "## Timestamp resolution",
        "Latency is JSON Kafka CreateTime minus UDP send time. Kafka CreateTime has 1 ms resolution, so sub-millisecond differences are quantized. Negative values from -1 ms up to (but not including) 0 ms are counted as quantization samples; the benchmark accepts these only as timestamp-resolution effects. Any larger negative value or invalid timestamp fails the timestamp gate.",
        table(["Mode", "Accepted negative quantization rows"], quantization_rows),
        "## Container resources and effective limits",
        "CPU averages are weighted by each recorded sampling interval and expressed in logical cores. Peak CPU is the largest sampled interval average; memory is the largest sampled cgroup memory.current value within the measured send window. Cgroup counter deltas use the first and last saved samples, so they cover the captured run rather than only the send window. Effective VM capacity, pre-traffic host limits, cgroup cpu.max/memory.max, and the runner's uncapped verification are shown from runtime evidence.",
        table(
            ["Mode", "Container", "VM capacity", "Average CPU cores",
             "Peak sampled CPU cores", "Peak memory MiB", "Avg cadence s",
             "Samples", "cgroup cpu.max", "cgroup memory.max",
             "Uncapped verified", "Host limits"],
            resource_rows,
        ),
        f'Across sampled containers, CPU throttling deltas were {cpu_throttling_status}; '
        f'memory high/max/OOM/OOM-kill event deltas were {memory_event_status}. '
        f'Resource sampling errors: {resource_sample_error_count}. Complete per-sample '
        f'CPU/memory pressure counters and timestamps are retained in each '
        f'resource-samples.csv; counter deltas are in mixed-resources.csv.',
    ])

    for mode, data in modes.items():
        totals = data["resource_totals"]
        report.append(
            f'{data["label"]} average CPU across sampled containers: '
            f'{fmt(totals.get("average_cpu_cores"))} cores. '
            f'Per-container sample-window peaks sum to '
            f'{fmt(totals.get("sum_of_individual_peak_cpu_cores_1s"))} CPU cores and '
            f'{fmt(number(totals.get("sum_of_individual_peak_memory_bytes")) / 2**20 if totals.get("sum_of_individual_peak_memory_bytes") is not None else None, 1)} MiB; those peaks may come from different sampling times. '
            f'Resource sample window elapsed seconds: '
            f'{fmt(data["resource_evidence"]["measured_send_window_elapsed_seconds"].get("start"))}–'
            f'{fmt(data["resource_evidence"]["measured_send_window_elapsed_seconds"].get("end"))}.'
        )
        if data["resource_evidence"]["sample_errors"]:
            report.append(
                f'{data["label"]} resource sampler errors: '
                + "; ".join(map(str, data["resource_evidence"]["sample_errors"]))
            )
    report.extend([
        "## Level of effort",
        "ODE Updates to Reduce Processing latency (S3-581): 20 SME hours, 50 Senior hours.",
        "## Evidence files",
        f'Aggregate summaries, status, per-type results, resource samples, and this report are written under {root}. Per-type source evidence remains in each mode/type directory.',
    ])
    return "\n\n".join(report) + "\n"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("directory", type=Path)
    args = parser.parse_args()
    root = args.directory.resolve()
    try:
        evidence = load_inputs(root)
        all_modes = {
            mode: analyze_mode(mode, label, evidence[mode])
            for mode, label in MODES.items()
        }
    except EvidenceError as error:
        parser.error(str(error))

    same_corpus = matching_corpus_hashes(all_modes)
    for data in all_modes.values():
        data["same_corpus_as_other_mode"] = same_corpus
        data["strict_pass"] = data["strict_pass"] and same_corpus
        data["strict_outcome"] = "PASS" if data["strict_pass"] else "FAIL"

    per_rows = []
    resource_rows = []
    for mode, data in all_modes.items():
        for result in data["types"]:
            summary = result["summary"]
            per_rows.append({
                "mode": mode,
                "type": result["type"],
                "target_rate_per_second": summary.get("target_rate_per_second"),
                "actual_type_rate": result["actual_type_rate"],
                "rate_window_count": result["rate_window_count"],
                "rate_window_min": result["rate_window_min"],
                "rate_window_max": result["rate_window_max"],
                "udp_sent": result["udp_sent"],
                "raw_correlated": result["raw_correlated"],
                "json_correlated": result["json_correlated"],
                "paired": result["paired"],
                "unique_ids": result["row_evidence"]["unique_ids"],
                "measured_id_range_matches":
                    result["row_evidence"]["measured_id_range_matches"],
                "unique_raw_positions": result["row_evidence"]["unique_raw_positions"],
                "unique_json_positions": result["row_evidence"]["unique_json_positions"],
                "valid_timestamp_rows": result["row_evidence"]["valid_timestamp_rows"],
                "invalid_timestamps": result["invalid_timestamps"],
                "negative_timestamp_quantization_rows": result["quantization_negative_timestamp_rows"],
                "p50_ms": result["latency_ms"]["p50_ms"],
                "p95_ms": result["latency_ms"]["p95_ms"],
                "p99_ms": result["latency_ms"]["p99_ms"],
                "duplicate_raw": result["duplicate_raw"],
                "duplicate_json": result["duplicate_json"],
                "unexpected_records": result["unexpected_records"],
                "unexpected_dlt_records": result["unexpected_dlt_records"],
                "consumer_lag_drained": result["consumer_lag_drained"],
                "application_group_drained": result["application_group_drained"],
                "extra_pipeline_groups_drained": result["extra_pipeline_groups_drained"],
                "health_ok_through_15_second_quiet_period": result["health_ok_through_quiet_period"],
                "pending_publications": result["pending"]["publications"],
                "pending_raw_publications": result["pending"]["raw_publications"],
                "pending_offset_commits": result["pending"]["offset_commits"],
                "recorded_benchmark_passed": summary.get("passed"),
                "passed": result["passed"],
                "failed_checks": "; ".join(result["failed_checks"]),
            })
        for resource in data["_resource_rows"]:
            row = {"mode": mode, **resource}
            row["host_limits"] = json.dumps(row.get("host_limits") or {}, sort_keys=True)
            resource_rows.append(row)

    summary_modes = {}
    status_rows = []
    for mode, data in all_modes.items():
        clean = {key: value for key, value in data.items() if not key.startswith("_")}
        clean["types"] = [
            {key: value for key, value in result.items()
             if key not in ("row_evidence", "send_monotonic_s")}
            for result in data["types"]
        ]
        summary_modes[mode] = clean
        status_rows.append({
            "mode": mode,
            "strict_outcome": data["strict_outcome"],
            "all_type_gates_pass": data["all_type_gates_pass"],
            "workload_parameters_match_acceptance_criteria":
                data["workload_parameters_match_acceptance_criteria"],
            "same_packet_corpus_as_other_mode": data["same_corpus_as_other_mode"],
            "run_prerequisites_pass": data["run_prerequisites_pass"],
            "run_prerequisite_checks": data["run_prerequisites"]["checks"],
            "sent": data["sent"],
            "paired": data["paired"],
            "p95_miss_types": [item["type"] for item in data["p95_misses"]],
            "per_type_exit_codes": {
                result["type"].lower(): result["passed"] for result in data["types"]
            },
        })

    vm_by_mode = {mode: data["vm_capacity"] for mode, data in all_modes.items()}
    first_settings = all_modes["ffm-local"]["workload_settings"]
    result = {
        "evidence_root": str(root),
        "workload": {
            "types": first_settings.get("type_count"),
            "aggregate_target_per_second":
                first_settings.get("aggregate_target_messages_per_second"),
            "per_type_target_per_second": first_settings.get("per_type_rate"),
            "measured_per_type": first_settings.get("per_type_count"),
            "warmup_per_type": first_settings.get("warmup_per_type"),
            "interleave_phase_step_ms":
                first_settings.get("round_robin_phase_step_ms"),
            "latency_definition":
                "JSON Kafka CreateTime minus UDP send time",
        },
        "acceptance_criteria": {
            "types": EXPECTED_TYPES,
            "aggregate_target_per_second": EXPECTED_AGGREGATE_RATE,
            "per_type_target_per_second": EXPECTED_TYPE_RATE,
            "rate_tolerance_percent": RATE_TOLERANCE_PERCENT,
            "measured_per_type": EXPECTED_MEASURED_PER_TYPE,
            "warmup_per_type": EXPECTED_WARMUP_PER_TYPE,
            "quiet_period_seconds": QUIET_PERIOD_SECONDS,
            "p95_strictly_below_ms": MAX_P95_MS,
            "measured_id_range_hex": [
                f"{EXPECTED_FIRST_MEASURED_ID:08X}",
                f"{EXPECTED_LAST_MEASURED_ID:08X}",
            ],
            "psm_exception": False,
            "same_packet_corpus_for_both_modes": True,
        },
        "same_packet_corpus_for_both_modes": same_corpus,
        "vm_capacity_by_mode": vm_by_mode,
        "modes": summary_modes,
    }
    result["vm_capacity"] = vm_by_mode.get("ffm-local")
    (root / "mixed-comparison-summary.json").write_text(
        json.dumps(json_safe(result), indent=2), encoding="utf-8"
    )
    (root / "comparison-status.json").write_text(
        json.dumps(json_safe(status_rows), indent=2), encoding="utf-8"
    )
    write_csv(root / "mixed-per-type.csv", per_rows)
    write_csv(root / "mixed-resources.csv", resource_rows)
    (root / "jira-performance-report.md").write_text(
        make_report(root, all_modes), encoding="utf-8"
    )
    print(root / "jira-performance-report.md")


if __name__ == "__main__":
    main()

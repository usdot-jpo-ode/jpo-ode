"""Correlate UDP messages with durable raw and decoded JSON Kafka records.

Latency (``p95_ms``) is Kafka JSON record creation time minus UDP send time. FFM runs also report
producer acknowledgement latency separately from the end-to-end measurement. The runner requires
every raw and JSON record, checks duplicates and quarantine traffic, and waits for consumer lag and
late publications to drain before writing its result.

Requires confluent-kafka. For the strict FFM gate, use ``--max-p95-ms 5`` with 1,000 warmup and
300,000 measured packets at a target rate of 1,000 packets/second.
"""

import argparse
import ast
import csv
from dataclasses import dataclass, field
import json
import math
import multiprocessing
import os
import queue
import random
import re
import socket
import time
from pathlib import Path
from urllib.request import urlopen


@dataclass(frozen=True)
class MetricSnapshot:
    confirmation_buckets: dict = field(default_factory=dict)
    pending_output: float | None = None
    pending_raw: float | None = None
    pending_commits: float | None = None
    stage_buckets: dict = field(default_factory=dict)


def empty_metric_snapshot():
    return MetricSnapshot()


class QuietPeriod:
    """Require continuous drain, healthy ODE, and no in-flight FFM work."""

    def __init__(self, duration):
        self.duration = duration
        self.started_at = None

    def observe(self, now, drained, health_ok, metrics, ffm_mode, watermarks_changed=False):
        pending_ok = not ffm_mode or (
            metrics.pending_output == 0 and metrics.pending_raw == 0
            and metrics.pending_commits == 0)
        if watermarks_changed or not drained or not health_ok or not pending_ok:
            self.started_at = None
            return False
        if self.started_at is None:
            self.started_at = now
        return now - self.started_at >= self.duration


def fixture_bytes(path):
    tree = ast.parse(path.read_text(encoding="utf-8"))
    return bytes.fromhex(next(ast.literal_eval(node.value) for node in tree.body
        if isinstance(node, ast.Assign) and any(isinstance(target, ast.Name)
        and target.id == "MESSAGE" for target in node.targets)))


def with_id(packet, value):
    # This fixture's UPER BSM TemporaryID starts at bit 10 after frame byte 34.
    # Preserve the surrounding msgCnt/secMark bits while replacing all 32 ID bits.
    message = bytearray(packet)
    field = int.from_bytes(message[35:40], "big")
    message[35:40] = ((field & ((3 << 38) | 63)) | (value << 6)).to_bytes(5, "big")
    return bytes(message)


def packet_id(packet):
    return (int.from_bytes(packet[35:40], "big") >> 6) & 0xffffffff


def measured_message_ids(first_id, warmup, count):
    """Return measured IDs, excluding the warmup prefix."""
    return set(range(first_id + warmup, first_id + warmup + count))


def send_packets(packets, host, port, rate, timing, identities=None,
                 scheduled_start=None, phase_offset_seconds=0.0):
    batch = {}
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as udp:
        if scheduled_start is None:
            started = time.perf_counter()
        else:
            started = scheduled_start + phase_offset_seconds
            remaining = started - time.perf_counter()
            if remaining > 0:
                time.sleep(remaining)
            started = time.perf_counter()
        timing.put(("started", started))
        try:
            for index, packet in enumerate(packets):
                remaining = started + index / rate - time.perf_counter()
                if remaining > 0:
                    time.sleep(remaining)
                udp.sendto(packet, (host, port))
                batch[identities[packet] if identities is not None else packet_id(packet)] = {
                    "wall_time_ms": time.time() * 1000,
                    "monotonic_s": time.perf_counter(),
                }
                if len(batch) >= 500:
                    timing.put(("batch", batch))
                    batch = {}
        finally:
            if batch:
                timing.put(("batch", batch))
            timing.put(("finished", time.perf_counter()))


def percent(values, fraction):
    """Linearly interpolate a percentile at the ``(n - 1) * p`` position."""
    ordered = sorted(float(value) for value in values)
    if not ordered:
        return None
    if not 0 <= fraction <= 1 or any(not math.isfinite(value) for value in ordered):
        raise ValueError("percentile input must be finite and fraction must be in [0, 1]")
    position = (len(ordered) - 1) * fraction
    low = int(position)
    high = min(low + 1, len(ordered) - 1)
    return ordered[low] + (ordered[high] - ordered[low]) * (position - low)


def classify_latency(json_create_time_ms, udp_send_time_ms):
    """Retain sub-ms CreateTime quantization negatives and reject larger clock faults."""
    latency = json_create_time_ms - udp_send_time_ms
    if not math.isfinite(latency) or latency < -1.0:
        return None, "invalid"
    return latency, "quantization_negative" if latency < 0 else "valid"


def is_create_time_timestamp(timestamp_type):
    """Kafka timestamp type 1 is producer CreateTime; all others are invalid here."""
    return timestamp_type == 1


def confirmed_percent(before, after, fraction):
    """Estimate producer acknowledgement latency from Prometheus histogram deltas."""
    counts = {limit: count - before.get(limit, 0) for limit, count in after.items()}
    total = counts.get(float("inf"), 0)
    if total <= 0:
        return None
    previous_limit = 0.0
    previous_count = 0.0
    target = total * fraction
    for limit, count in sorted(counts.items()):
        if count >= target:
            if limit == float("inf") or count == previous_count:
                return previous_limit * 1000
            return (previous_limit + (limit - previous_limit)
                    * (target - previous_count) / (count - previous_count)) * 1000
        previous_limit, previous_count = limit, count
    return None


def metric_estimates(before, after, ffm_mode):
    """Return FFM acknowledgement and stage estimates; external mode has neither metric set."""
    if not ffm_mode:
        return ({f"confirmed_p{quantile}_estimate_ms": None
                 for quantile in (50, 95, 99)}, {})
    ack = {f"confirmed_p{quantile}_estimate_ms": confirmed_percent(
        before.confirmation_buckets, after.confirmation_buckets, fraction)
        for quantile, fraction in ((50, .50), (95, .95), (99, .99))}
    stages = {stage: confirmed_percent(before.stage_buckets.get(stage, {}),
        after.stage_buckets.get(stage, {}), .95)
        for stage in sorted(set(before.stage_buckets) | set(after.stage_buckets))}
    return ack, stages


def validation_errors(expected_ids, raw, output, send_times, duplicate_raw, duplicate_json,
                      unexpected_records, dlt_count, invalid_timestamps, actual_rate,
                      target_rate, window_rates, max_send_gap_ms, max_send_gap_limit_ms, p95_ms,
                      max_p95_ms, pending_publications, pending_raw_publications,
                      pending_offset_commits, app_group_drained, drained, health_ok=True,
                      ffm_checks=True):
    """Evaluate the same acceptance gates used by the command-line benchmark."""
    expected = set(expected_ids)
    errors = []
    missing_raw = expected - set(raw)
    missing_json = expected - set(output)
    missing_send_times = expected - set(send_times)
    if missing_raw:
        errors.append(f"missing {len(missing_raw)} raw records")
    if missing_json:
        errors.append(f"missing {len(missing_json)} JSON records")
    if missing_send_times:
        errors.append(f"missing {len(missing_send_times)} UDP send timestamps")
    if duplicate_raw:
        errors.append(f"found {duplicate_raw} duplicate raw records")
    if duplicate_json:
        errors.append(f"found {duplicate_json} duplicate JSON records")
    if unexpected_records:
        errors.append(f"found {unexpected_records} records outside the benchmark ID set")
    if ffm_checks and dlt_count:
        errors.append(f"found {dlt_count} unexpected quarantine records")
    if invalid_timestamps:
        errors.append(f"found {invalid_timestamps} invalid timestamps or negative latencies")
    if actual_rate is None or not math.isfinite(actual_rate):
        errors.append("actual UDP rate is missing or invalid")
    elif not 0.95 * target_rate <= actual_rate <= 1.05 * target_rate:
        errors.append(f"actual UDP rate {actual_rate:.2f}/s is outside +/-5% of target")
    if not window_rates:
        errors.append("complete 10-second UDP send-rate windows are missing")
    elif any(not math.isfinite(rate) or not 0.95 * target_rate <= rate <= 1.05 * target_rate
             for rate in window_rates):
        errors.append("one or more complete 10-second UDP send-rate windows are outside +/-5%")
    if max_send_gap_ms is None or not math.isfinite(max_send_gap_ms):
        errors.append("maximum inter-packet send gap is missing or invalid")
    elif max_send_gap_ms >= max_send_gap_limit_ms:
        errors.append(
            f"maximum inter-packet send gap {max_send_gap_ms:.1f} ms is not below "
            f"{max_send_gap_limit_ms:.1f} ms")
    if max_p95_ms is not None:
        if p95_ms is None or not math.isfinite(p95_ms):
            errors.append("p95_ms is missing or invalid")
        elif p95_ms >= max_p95_ms:
            errors.append(f"p95_ms {p95_ms:.3f} is not below {max_p95_ms:.3f} ms")
    if ffm_checks:
        if pending_publications is None or pending_publications != 0:
            errors.append("pending FFM publications are missing or nonzero")
        if pending_raw_publications is None or pending_raw_publications != 0:
            errors.append("pending raw publications are missing or nonzero")
        if pending_offset_commits is None or pending_offset_commits != 0:
            errors.append("pending offset commits are missing or nonzero")
    if not app_group_drained:
        errors.append("application consumer-group offsets did not reach raw-topic watermarks")
    if not drained:
        errors.append("Kafka consumer lag did not drain before timeout")
    if not health_ok:
        errors.append("ODE health was not UP throughout the quiet period")
    return errors


def scrape_ffm_metrics(url, topic):
    """Read confirmation latency and raw/output/offset in-flight metrics."""
    text = urlopen(url, timeout=5).read().decode("utf-8")
    buckets, pending_output = parse_ffmlib_metrics(text, topic)
    pending_raw, pending_commits = parse_outstanding_metrics(text)
    return MetricSnapshot(buckets, pending_output, pending_raw, pending_commits,
                          parse_stage_metrics(text))


def check_health(url):
    """Require the configured Spring Boot health endpoint to report UP."""
    with urlopen(url, timeout=5) as response:
        status = json.loads(response.read().decode("utf-8")).get("status")
    return status == "UP"


def parse_ffmlib_metrics(text, topic):
    """Parse labeled Micrometer histogram and gauge samples for one JSON topic."""
    buckets = {}
    pending_values = []
    for line in text.splitlines():
        if line.startswith("ode_ffmlib_output_confirmation_seconds_bucket{") \
                and f'topic="{topic}"' in line:
            label = re.search(r'le="([^"]+)"', line)
            if label:
                limit = float(label.group(1).replace("+Inf", "inf"))
                buckets[limit] = buckets.get(limit, 0.0) + float(line.rsplit(" ", 1)[1])
        elif line.startswith("ode_ffmlib_output_in_flight"):
            match = re.fullmatch(r"ode_ffmlib_output_in_flight(?:\{[^}]*\})?\s+([^\s]+)",
                                 line)
            if match:
                pending_values.append(float(match.group(1)))
    return buckets, max(pending_values) if pending_values else None


def parse_outstanding_metrics(text):
    """Read raw-send and async offset-commit gauges from a Prometheus scrape."""
    values = {}
    names = {
        "ode_ffmlib_raw_publication_in_flight": "raw",
        "ode_ffmlib_offset_commit_in_flight": "commits",
    }
    for line in text.splitlines():
        for metric, name in names.items():
            if line.startswith(metric):
                match = re.fullmatch(metric + r"(?:\{[^}]*\})?\s+([^\s]+)", line)
                if match:
                    values[name] = float(match.group(1))
    return values.get("raw"), values.get("commits")


def parse_stage_metrics(text):
    """Parse bounded ODE histograms for decode, ingestion, and commit stages."""
    metrics = {}
    for line in text.splitlines():
        if not line.startswith("ode_ffmlib_") or "_seconds_bucket{" not in line:
            continue
        stage = re.search(r'stage="([^"]+)"', line)
        if stage is None:
            metric = re.match(r"(ode_ffmlib_[a-z0-9_.]+)_seconds_bucket", line)
            stage_name = metric.group(1) if metric else None
        else:
            stage_name = stage.group(1)
        limit = re.search(r'le="([^"]+)"', line)
        if stage_name is None or limit is None:
            continue
        seconds = float(limit.group(1).replace("+Inf", "inf"))
        buckets = metrics.setdefault(stage_name, {})
        buckets[seconds] = buckets.get(seconds, 0.0) + float(line.rsplit(" ", 1)[1])
    return metrics


def is_drained_to_watermarks(positions, end_offsets, start_offsets):
    """Ignore untouched partitions and require every new record to be consumed."""
    by_partition = {(position.topic, position.partition): position.offset
                    for position in positions}
    for key, end in end_offsets.items():
        if end <= start_offsets[key]:
            continue
        if by_partition.get(key, -1) < end:
            return False
    return True


def is_group_committed_to_watermarks(committed_offsets, raw_topic, end_offsets,
                                     start_offsets):
    """Require application-group committed next offsets to reach raw end offsets."""
    for (topic, partition), end in end_offsets.items():
        if topic != raw_topic or end <= start_offsets[(topic, partition)]:
            continue
        if committed_offsets.get((topic, partition), -1) < end:
            return False
    return True


def complete_window_rates(monotonic_send_times, target_rate, window_seconds=10):
    """Return rates for complete fixed-width windows, excluding a partial tail."""
    if not monotonic_send_times:
        return []
    start = min(monotonic_send_times)
    end = max(monotonic_send_times)
    window_count = int((end - start + 1 / target_rate) // window_seconds)
    rates = []
    for window in range(window_count):
        lower = start + window * window_seconds
        upper = lower + window_seconds
        count = sum(lower <= value < upper for value in monotonic_send_times)
        rates.append(count / window_seconds)
    return rates


def latency_windows(send_times, latencies, first_id, count, target_rate,
                    window_seconds=10):
    """Summarize paired latency by complete measured-send windows, excluding warmup."""
    ids = [message_id for message_id in range(first_id, first_id + count)
           if message_id in send_times and message_id in latencies]
    if not ids:
        return []
    start = min(send_times[message_id]["monotonic_s"] for message_id in ids)
    end = max(send_times[message_id]["monotonic_s"] for message_id in ids)
    windows = int((end - start + 1 / target_rate) // window_seconds)
    summaries = []
    for index in range(windows):
        lower = start + index * window_seconds
        upper = lower + window_seconds
        values = [latencies[message_id] for message_id in ids
                  if lower <= send_times[message_id]["monotonic_s"] < upper]
        if values:
            summaries.append({"window": index, "paired": len(values),
                              "p50_ms": percent(values, .50),
                              "p95_ms": percent(values, .95),
                              "p99_ms": percent(values, .99)})
    return summaries


def extract_message_id(topic, value, identities=None):
    record = json.loads(value)
    if identities is not None:
        return identities[bytes.fromhex(record["metadata"]["asn1"])]
    if topic.startswith("topic.OdeRawEncoded"):
        raw_hex = record["metadata"]["asn1"]
        return packet_id(bytes.fromhex(raw_hex))
    bsm = record["payload"]["data"]["value"]["BasicSafetyMessage"]
    return int(bsm["coreData"]["id"], 16)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", choices=("external", "ffm"), required=True)
    parser.add_argument("--broker", required=True)
    inputs = parser.add_mutually_exclusive_group(required=True)
    inputs.add_argument("--fixture", type=Path)
    inputs.add_argument("--packet-file", type=Path, help="unique valid datagrams, one hex string per line")
    parser.add_argument("--message-type", default="BSM")
    parser.add_argument("--drain-group", action="append", default=[],
                        help="additional external pipeline group:topic to drain")
    parser.add_argument("--udp-host", default=os.getenv("DECODE_BENCHMARK_UDP_HOST", "127.0.0.1"))
    parser.add_argument("--udp-port", type=int, default=46800)
    parser.add_argument("--raw-topic", default="topic.OdeRawEncodedBSMJson")
    parser.add_argument("--json-topic", default="topic.OdeBsmJson")
    parser.add_argument("--dlt-topic", help="defaults to <raw-topic>.FFM.DLT in FFM mode")
    parser.add_argument("--consumer-group", default="RawEncodedBSMJsonRouter",
                        help="application group whose raw offsets must reach the end watermarks")
    parser.add_argument("--metrics-url", default=os.getenv(
        "DECODE_BENCHMARK_METRICS_URL", "http://127.0.0.1:8080/actuator/prometheus"))
    parser.add_argument("--health-url", default=os.getenv(
        "DECODE_BENCHMARK_HEALTH_URL", "http://127.0.0.1:8080/actuator/health"))
    parser.add_argument("--count", type=int, default=300000)
    parser.add_argument("--warmup", type=int, default=1000)
    parser.add_argument("--rate", type=float, default=1000)
    parser.add_argument("--warmup-start-monotonic", type=float,
                        help="shared monotonic start time for a coordinated mixed workload")
    parser.add_argument("--measured-start-monotonic", type=float,
                        help="shared monotonic start time for measured packets")
    parser.add_argument("--phase-offset-ms", type=float, default=0,
                        help="per-type phase offset used to evenly interleave senders")
    parser.add_argument("--max-send-gap-ms", type=float, default=1000,
                        help="fail when the measured sender pauses for this long")
    parser.add_argument("--max-p95-ms", type=float,
                        help="fail when p95 is missing or greater than or equal to this value")
    parser.add_argument("--quiet-period-seconds", type=float, default=15)
    parser.add_argument("--drain-timeout-seconds", type=float, default=60)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    if args.mode == "ffm" and args.max_p95_ms is None:
        parser.error("--max-p95-ms is required for the FFM acceptance benchmark")
    if args.count <= 0 or args.warmup < 0 or not math.isfinite(args.rate) or args.rate <= 0:
        parser.error("count and rate must be positive and warmup must be nonnegative")
    if not math.isfinite(args.max_send_gap_ms) or args.max_send_gap_ms <= 0:
        parser.error("max-send-gap-ms must be a finite positive value")
    if args.max_p95_ms is not None and (
            not math.isfinite(args.max_p95_ms) or args.max_p95_ms <= 0):
        parser.error("max-p95-ms must be a finite positive value")

    try:
        from confluent_kafka import Consumer, TopicPartition
    except ImportError as error:
        raise SystemExit("confluent-kafka is required: install it with pip") from error

    identities = None
    if args.packet_file:
        packets = [bytes.fromhex(line.strip()) for line in args.packet_file.read_text().splitlines()]
        if len(packets) != args.count + args.warmup or len(set(packets)) != len(packets):
            raise ValueError("packet-file must contain exactly count+warmup unique datagrams")
        identities = {packet: index for index, packet in enumerate(packets)}
        first_id = 0
    else:
        fixture = fixture_bytes(args.fixture)
        if packet_id(fixture) != 0x31325433:
            raise ValueError("BSM fixture changed; verify the TemporaryID bit offset")
        first_id = random.randrange(0, 0xffffffff - args.count - args.warmup)
        packets = [with_id(fixture, first_id + index)
                   for index in range(args.count + args.warmup)]
    expected_all = set(range(first_id, first_id + len(packets)))
    expected_measured = measured_message_ids(first_id, args.warmup, args.count)
    dlt_topic = args.dlt_topic or f"{args.raw_topic}.FFM.DLT"

    consumer = Consumer({"bootstrap.servers": args.broker,
                         "group.id": f"decode-benchmark-{time.time_ns()}",
                         "enable.auto.commit": False,
                         "client.id": "ode-decode-benchmark"})
    topic_names = [args.raw_topic, args.json_topic]
    if args.mode == "ffm":
        topic_names.append(dlt_topic)
    extra_groups = [item.split(":", 1) for item in args.drain_group]
    topic_names.extend(topic for _, topic in extra_groups if topic not in topic_names)
    assigned = []
    partitions = {}
    starts = {}
    end_by_partition = {}
    for topic in topic_names:
        info = consumer.list_topics(topic, timeout=10).topics[topic]
        if info.error is not None:
            raise RuntimeError(f"Topic {topic} is not available: {info.error}")
        partitions[topic] = len(info.partitions)
        for part in info.partitions:
            _, end = consumer.get_watermark_offsets(TopicPartition(topic, part), timeout=10)
            starts[(topic, part)] = end
            end_by_partition[(topic, part)] = end
            assigned.append(TopicPartition(topic, part, end))
    consumer.assign(assigned)
    app_consumer = Consumer({"bootstrap.servers": args.broker,
                             "group.id": args.consumer_group,
                             "enable.auto.commit": False,
                             "client.id": "ode-benchmark-app-offset-check"})

    extra_consumers = [(Consumer({"bootstrap.servers": args.broker, "group.id": group,
                       "enable.auto.commit": False}), group, topic)
                       for group, topic in extra_groups]
    extra_commits = {}

    raw = {}
    output = {}
    duplicates = {"raw": 0, "json": 0}
    timestamp_type_counts = {"raw": {}, "json": {}}
    unexpected_records = 0
    invalid_timestamps = 0
    dlt_count = 0

    def accept(record):
        nonlocal unexpected_records, invalid_timestamps, dlt_count
        if record.error():
            raise RuntimeError(record.error())
        topic = record.topic()
        if topic == dlt_topic:
            dlt_count += 1
            return
        if topic not in (args.raw_topic, args.json_topic):
            return
        target = raw if topic == args.raw_topic else output
        kind = "raw" if topic == args.raw_topic else "json"
        try:
            message_id = extract_message_id(topic, record.value(), identities)
        except (KeyError, TypeError, ValueError, json.JSONDecodeError):
            invalid_timestamps += 1
            return
        if message_id not in expected_all:
            unexpected_records += 1
            return
        timestamp_type, timestamp = record.timestamp()
        type_counts = timestamp_type_counts[kind]
        type_key = str(timestamp_type)
        type_counts[type_key] = type_counts.get(type_key, 0) + 1
        if message_id in target:
            duplicates[kind] += 1
            return
        target[message_id] = (record.partition(), record.offset(), timestamp_type,
                              timestamp, record.key())

    def capture_until(required, deadline, poll_callback=None):
        while time.monotonic() < deadline:
            if poll_callback is not None:
                poll_callback()
            if len(raw) >= required and len(output) >= required:
                return
            record = consumer.poll(0.2)
            if record is not None:
                accept(record)
        raise TimeoutError(
            f"Timed out awaiting {required} correlated records (raw={len(raw)}, json={len(output)})")

    def current_watermarks():
        result = {}
        for topic, part in starts:
            _, end = consumer.get_watermark_offsets(TopicPartition(topic, part), timeout=10)
            result[(topic, part)] = end
        return result

    def drained_to(end_offsets):
        return is_drained_to_watermarks(consumer.position(assigned), end_offsets, starts)

    def committed_offsets():
        raw_partitions = [TopicPartition(args.raw_topic, part)
                          for topic, part in starts if topic == args.raw_topic]
        committed = app_consumer.committed(raw_partitions, timeout=10)
        return {(args.raw_topic, item.partition): item.offset
                for item in committed if item.offset >= 0}

    def extra_groups_drained(watermarks):
        for checker, group, topic in extra_consumers:
            offsets = checker.committed([TopicPartition(topic, part) for name, part in starts
                                         if name == topic], timeout=10)
            actual = {(topic, item.partition): item.offset for item in offsets}
            extra_commits[group] = {f"{name}:{part}": value for (name, part), value in actual.items()}
            if not is_group_committed_to_watermarks(actual, topic, watermarks, starts):
                return False
        return True

    def drain_until_quiet():
        deadline = time.monotonic() + args.drain_timeout_seconds
        last_watermarks = current_watermarks()
        quiet_period = QuietPeriod(args.quiet_period_seconds)
        latest_metrics = empty_metric_snapshot()
        quiet_health_ok = False
        health_error = None
        while time.monotonic() < deadline:
            record = consumer.poll(0.2)
            if record is not None:
                accept(record)
            watermarks = current_watermarks()
            offsets = committed_offsets()
            is_app_drained = is_group_committed_to_watermarks(
                offsets, args.raw_topic, watermarks, starts)
            is_drained = drained_to(watermarks) and is_app_drained and extra_groups_drained(watermarks)
            try:
                quiet_health_ok = check_health(args.health_url)
                health_error = None if quiet_health_ok else "health endpoint status was not UP"
            except Exception as error:
                quiet_health_ok = False
                health_error = f"{type(error).__name__}: {error}"
            latest_metrics = scrape_ffm_metrics(args.metrics_url, args.json_topic) \
                if args.mode == "ffm" else empty_metric_snapshot()
            quiet_ok = quiet_period.observe(time.monotonic(), is_drained, quiet_health_ok,
                latest_metrics, args.mode == "ffm", watermarks != last_watermarks)
            last_watermarks = watermarks
            if quiet_ok:
                return True, watermarks, offsets, latest_metrics, quiet_health_ok, health_error
        return (False, last_watermarks, committed_offsets(), latest_metrics,
                quiet_health_ok, health_error)

    send_times = {}

    def run_send(subset, required_total, scheduled_start=None):
        timing = multiprocessing.Queue()
        timing_state = {"started": None, "finished": None}

        def collect_sender_timing():
            while True:
                try:
                    kind, value = timing.get_nowait()
                except queue.Empty:
                    return
                if kind == "batch":
                    send_times.update(value)
                else:
                    timing_state[kind] = value

        sender = multiprocessing.Process(target=send_packets,
            args=(subset, args.udp_host, args.udp_port, args.rate, timing, identities,
                  scheduled_start, args.phase_offset_ms / 1000.0))
        sender.start()
        try:
            capture_until(required_total, time.monotonic() + len(subset) / args.rate + 120,
                          collect_sender_timing)
            sender.join(timeout=30)
            collect_sender_timing()
            if sender.is_alive():
                raise RuntimeError("UDP sender did not exit after sending packets")
            if sender.exitcode != 0:
                raise RuntimeError("UDP sender failed")
            if timing_state["started"] is None or timing_state["finished"] is None:
                raise RuntimeError("UDP sender did not report pacing timestamps")
        finally:
            if sender.is_alive():
                sender.terminate()
                sender.join(timeout=5)
            collect_sender_timing()
            timing.close()
            timing.join_thread()
        return ((len(subset) - 1) / (timing_state["finished"] - timing_state["started"])
                if len(subset) > 1 and timing_state["finished"] is not None else None)

    before_confirmations = empty_metric_snapshot()
    after_confirmations = empty_metric_snapshot()
    actual_rate = None
    drained = False
    health_ok = False
    health_failure = None
    final_watermarks = end_by_partition.copy()
    final_committed_offsets = committed_offsets()
    benchmark_failure = None
    try:
        health_ok = check_health(args.health_url)
        if not health_ok:
            raise RuntimeError("ODE health endpoint did not report UP before workload")
        run_send(packets[:args.warmup], args.warmup, args.warmup_start_monotonic)
        before_confirmations = scrape_ffm_metrics(args.metrics_url, args.json_topic) \
            if args.mode == "ffm" else empty_metric_snapshot()
        actual_rate = run_send(packets[args.warmup:], len(packets),
                               args.measured_start_monotonic)
        (drained, final_watermarks, final_committed_offsets, after_confirmations,
         health_ok, health_failure) = drain_until_quiet()
    except Exception as error:
        benchmark_failure = f"{type(error).__name__}: {error}"
        try:
            final_watermarks = current_watermarks()
            final_committed_offsets = committed_offsets()
            after_confirmations = scrape_ffm_metrics(args.metrics_url, args.json_topic) \
                if args.mode == "ffm" else empty_metric_snapshot()
            health_ok = check_health(args.health_url)
        except Exception as report_error:
            benchmark_failure += f"; partial status unavailable: {report_error}"
    finally:
        consumer.close()
        app_consumer.close()
        for checker, _, _ in extra_consumers:
            checker.close()

    rows = []
    invalid_timestamp_rows = []
    latencies = []
    latency_by_id = {}
    quantization_negative_timestamps = 0
    key_mismatches = 0
    for message_id in sorted(expected_measured):
        if message_id not in raw or message_id not in output or message_id not in send_times:
            continue
        raw_partition, raw_offset, raw_timestamp_type, raw_timestamp, raw_key = raw[message_id]
        json_partition, json_offset, json_timestamp_type, json_timestamp, json_key = output[message_id]
        if not is_create_time_timestamp(raw_timestamp_type) \
                or not is_create_time_timestamp(json_timestamp_type):
            invalid_timestamps += 1
            invalid_timestamp_rows.append((f"{message_id:08X}", raw_partition, raw_offset,
                raw_timestamp_type, raw_timestamp, json_partition, json_offset,
                json_timestamp_type, json_timestamp, send_times[message_id]["wall_time_ms"],
                send_times[message_id]["monotonic_s"], None, "timestamp_type"))
            continue
        send_timestamp = send_times[message_id]["wall_time_ms"]
        latency, latency_status = classify_latency(json_timestamp, send_timestamp) \
            if json_timestamp is not None else (None, "invalid")
        if not math.isfinite(send_timestamp) or latency is None:
            invalid_timestamps += 1
            invalid_timestamp_rows.append((f"{message_id:08X}", raw_partition, raw_offset,
                raw_timestamp_type, raw_timestamp, json_partition, json_offset,
                json_timestamp_type, json_timestamp, send_timestamp,
                send_times[message_id]["monotonic_s"], None, "invalid_clock"))
            continue
        if latency_status == "quantization_negative":
            quantization_negative_timestamps += 1
        if raw_key != json_key:
            key_mismatches += 1
            if args.mode == "ffm":
                unexpected_records += 1
                continue
        latencies.append(latency)
        latency_by_id[message_id] = latency
        rows.append((f"{message_id:08X}", raw_partition, raw_offset, raw_timestamp_type,
                     raw_timestamp, json_partition, json_offset, json_timestamp_type,
                     json_timestamp, send_timestamp, send_times[message_id]["monotonic_s"],
                     latency, latency_status))

    ordered_send_times = [send_times[message_id]["monotonic_s"] * 1000
                          for message_id in sorted(expected_measured)
                          if message_id in send_times]
    send_gaps = [later - earlier for earlier, later in
                 zip(ordered_send_times, ordered_send_times[1:])]
    invalid_timestamps += sum(gap < 0 or not math.isfinite(gap) for gap in send_gaps)
    max_send_gap_ms = max(send_gaps, default=None)
    measured_monotonic = [send_times[message_id]["monotonic_s"]
                          for message_id in sorted(expected_measured)
                          if message_id in send_times]
    if actual_rate is None and len(measured_monotonic) > 1:
        actual_rate = (len(measured_monotonic) - 1) / (
            max(measured_monotonic) - min(measured_monotonic))
    window_rates = complete_window_rates(measured_monotonic, args.rate, 10)
    second_rates = complete_window_rates(measured_monotonic, args.rate, 1)
    latency_by_window = latency_windows(send_times, latency_by_id,
                                        first_id + args.warmup, args.count, args.rate)
    raw_partition_counts = {}
    partition_sequence = []
    for message_id in sorted(expected_measured):
        if message_id in raw:
            partition = raw[message_id][0]
            raw_partition_counts[partition] = raw_partition_counts.get(partition, 0) + 1
            partition_sequence.append(partition)
    max_partition_streak = 0
    current_partition = None
    current_streak = 0
    for partition in partition_sequence:
        current_streak = current_streak + 1 if partition == current_partition else 1
        current_partition = partition
        max_partition_streak = max(max_partition_streak, current_streak)
    measured_duration = (max(measured_monotonic) - min(measured_monotonic)
                         if len(measured_monotonic) > 1 else None)
    raw_partition_rates = ({str(partition): count / measured_duration
                            for partition, count in raw_partition_counts.items()}
                           if measured_duration and measured_duration > 0 else {})

    p50 = percent(latencies, .50)
    p95 = percent(latencies, .95)
    p99 = percent(latencies, .99)
    ack, stage_p95 = metric_estimates(before_confirmations, after_confirmations,
                                      args.mode == "ffm")
    errors = validation_errors(expected_measured, raw, output, send_times,
        duplicates["raw"], duplicates["json"], unexpected_records, dlt_count,
        invalid_timestamps, actual_rate, args.rate, window_rates, max_send_gap_ms,
        args.max_send_gap_ms,
        p95, args.max_p95_ms,
        after_confirmations.pending_output, after_confirmations.pending_raw,
        after_confirmations.pending_commits,
        is_group_committed_to_watermarks(final_committed_offsets, args.raw_topic,
                                         final_watermarks, starts), drained,
        health_ok=health_ok and health_failure is None, ffm_checks=args.mode == "ffm")
    if benchmark_failure:
        errors.append("benchmark interrupted: " + benchmark_failure)

    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream)
        writer.writerow(("message_id" if identities is not None else "bsm_id", "raw_partition", "raw_offset", "raw_timestamp_type",
                         "raw_timestamp_ms", "json_partition", "json_offset",
                         "json_timestamp_type", "json_timestamp_ms", "udp_send_time_ms",
                         "udp_send_monotonic_s", "udp_to_json_record_ms", "timestamp_status"))
        writer.writerows(rows)
        writer.writerows(invalid_timestamp_rows)

    summary = {"message_type": args.message_type, "packet_file": str(args.packet_file) if args.packet_file else None,
        "extra_pipeline_committed_offsets": extra_commits,
        "mode": args.mode, "raw_topic": args.raw_topic, "json_topic": args.json_topic,
        "dlt_topic": dlt_topic if args.mode == "ffm" else None,
        "target_rate_per_second": args.rate, "actual_udp_rate_per_second": actual_rate,
        "complete_1s_window_rates_per_second": second_rates,
        "complete_10s_window_rates_per_second": window_rates,
        "rate_tolerance_percent": 5, "max_inter_packet_send_gap_ms": max_send_gap_ms,
        "max_send_gap_ms": args.max_send_gap_ms,
        "warmup": args.warmup, "measured_requested_count": args.count,
        "udp_sent": sum(message_id in send_times for message_id in expected_measured),
        "raw_correlated": sum(message_id in raw for message_id in expected_measured),
        "json_correlated": sum(message_id in output for message_id in expected_measured),
        "paired": len(rows), "duplicate_raw": duplicates["raw"],
        "raw_records_by_partition": raw_partition_counts,
        "raw_records_per_partition_per_second": raw_partition_rates,
        "max_consecutive_raw_partition_records": max_partition_streak,
        "latency_by_complete_10s_window": latency_by_window,
        "raw_json_key_mismatches": key_mismatches,
        "duplicate_json": duplicates["json"], "unexpected_records": unexpected_records,
        "unexpected_dlt_records": dlt_count, "invalid_timestamps": invalid_timestamps,
        "timestamp_type_counts": timestamp_type_counts,
        "negative_timestamp_quantization_samples": quantization_negative_timestamps,
        "benchmark_failure": benchmark_failure,
        "health_url": args.health_url, "health_ok_through_quiet_period": health_ok,
        "health_failure": health_failure,
        "consumer_lag_drained": drained,
        "final_watermarks": {f"{topic}:{part}": end
                              for (topic, part), end in final_watermarks.items()},
        "pending_publications": after_confirmations.pending_output,
        "pending_raw_publications": after_confirmations.pending_raw,
        "pending_offset_commits": after_confirmations.pending_commits,
        "application_consumer_group": args.consumer_group,
        "application_committed_offsets": {
            f"{topic}:{part}": offset
            for (topic, part), offset in final_committed_offsets.items()},
        "partitions": partitions, "p50_ms": p50, "p95_ms": p95, "p99_ms": p99,
        **ack, "stage_p95_estimate_ms": stage_p95,
        "max_p95_ms": args.max_p95_ms, "passed": not errors, "errors": errors}
    args.output.with_suffix(".json").write_text(json.dumps(summary, indent=2), encoding="utf-8")
    print(json.dumps(summary, indent=2))
    if errors:
        raise SystemExit("Benchmark acceptance failed: " + "; ".join(errors))


if __name__ == "__main__":
    main()

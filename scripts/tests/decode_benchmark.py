"""Correlate UDP BSMs with durable raw and decoded JSON Kafka records.

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
import json
import math
import multiprocessing
import queue
import random
import re
import socket
import time
from pathlib import Path
from urllib.request import urlopen


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


def send_packets(packets, host, port, rate, timing):
    sent_at = {}
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as udp:
        started = time.perf_counter()
        for index, packet in enumerate(packets):
            remaining = started + index / rate - time.perf_counter()
            if remaining > 0:
                time.sleep(remaining)
            sent_at[packet_id(packet)] = time.time() * 1000
            udp.sendto(packet, (host, port))
        timing.put((started, time.perf_counter(), sent_at))


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


def validation_errors(expected_ids, raw, output, send_times, duplicate_raw, duplicate_json,
                      unexpected_records, dlt_count, invalid_timestamps, actual_rate,
                      target_rate, max_send_gap_ms, max_send_gap_limit_ms, p95_ms,
                      max_p95_ms, pending_publications, drained):
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
    if dlt_count:
        errors.append(f"found {dlt_count} unexpected quarantine records")
    if invalid_timestamps:
        errors.append(f"found {invalid_timestamps} invalid timestamps or negative latencies")
    if actual_rate is None or not math.isfinite(actual_rate):
        errors.append("actual UDP rate is missing or invalid")
    elif not 0.95 * target_rate <= actual_rate <= 1.05 * target_rate:
        errors.append(f"actual UDP rate {actual_rate:.2f}/s is outside +/-5% of target")
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
    if pending_publications is None or pending_publications != 0:
        errors.append("pending FFM publications are missing or nonzero")
    if not drained:
        errors.append("Kafka consumer lag did not drain before timeout")
    return errors


def scrape_ffm_metrics(url, topic):
    """Read the FFM output confirmation histogram and in-flight publication gauge."""
    text = urlopen(url, timeout=5).read().decode("utf-8")
    return parse_ffmlib_metrics(text, topic)


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
                buckets[limit] = float(line.rsplit(" ", 1)[1])
        elif line.startswith("ode_ffmlib_output_in_flight"):
            match = re.fullmatch(r"ode_ffmlib_output_in_flight(?:\{[^}]*\})?\s+([^\s]+)",
                                 line)
            if match:
                pending_values.append(float(match.group(1)))
    return buckets, max(pending_values) if pending_values else None


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


def extract_message_id(topic, value):
    record = json.loads(value)
    if topic.startswith("topic.OdeRawEncoded"):
        raw_hex = record["metadata"]["asn1"]
        return packet_id(bytes.fromhex(raw_hex))
    bsm = record["payload"]["data"]["value"]["BasicSafetyMessage"]
    return int(bsm["coreData"]["id"], 16)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", choices=("external", "ffm"), required=True)
    parser.add_argument("--broker", required=True)
    parser.add_argument("--fixture", type=Path, required=True)
    parser.add_argument("--udp-host", default="127.0.0.1")
    parser.add_argument("--udp-port", type=int, default=46800)
    parser.add_argument("--raw-topic", default="topic.OdeRawEncodedBSMJson")
    parser.add_argument("--json-topic", default="topic.OdeBsmJson")
    parser.add_argument("--dlt-topic", help="defaults to <raw-topic>.FFM.DLT in FFM mode")
    parser.add_argument("--metrics-url", default="http://127.0.0.1:8080/actuator/prometheus")
    parser.add_argument("--count", type=int, default=300000)
    parser.add_argument("--warmup", type=int, default=1000)
    parser.add_argument("--rate", type=float, default=1000)
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

    raw = {}
    output = {}
    duplicates = {"raw": 0, "json": 0}
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
        target = raw if topic == args.raw_topic else output
        kind = "raw" if topic == args.raw_topic else "json"
        try:
            message_id = extract_message_id(topic, record.value())
        except (KeyError, TypeError, ValueError, json.JSONDecodeError):
            invalid_timestamps += 1
            return
        if message_id not in expected_all:
            unexpected_records += 1
            return
        timestamp = record.timestamp()[1]
        if timestamp is None or timestamp <= 0:
            invalid_timestamps += 1
        if message_id in target:
            duplicates[kind] += 1
            return
        target[message_id] = (record.partition(), record.offset(), timestamp, record.key())

    def capture_until(required, deadline):
        while time.monotonic() < deadline:
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

    def drain_until_quiet():
        deadline = time.monotonic() + args.drain_timeout_seconds
        last_watermarks = current_watermarks()
        quiet_since = time.monotonic()
        while time.monotonic() < deadline:
            record = consumer.poll(0.2)
            if record is not None:
                accept(record)
            watermarks = current_watermarks()
            is_drained = drained_to(watermarks)
            if watermarks != last_watermarks or not is_drained:
                quiet_since = time.monotonic()
            last_watermarks = watermarks
            if is_drained and time.monotonic() - quiet_since >= args.quiet_period_seconds:
                return True, watermarks
        return False, last_watermarks

    send_times = {}

    def run_send(subset, required_total):
        timing = multiprocessing.Queue()
        sender = multiprocessing.Process(target=send_packets,
            args=(subset, args.udp_host, args.udp_port, args.rate, timing))
        sender.start()
        try:
            capture_until(required_total, time.monotonic() + len(subset) / args.rate + 120)
            started, ended, sent_at = timing.get(timeout=120)
            sender.join(timeout=30)
            if sender.is_alive():
                raise RuntimeError("UDP sender did not exit after sending packets")
            if sender.exitcode != 0:
                raise RuntimeError("UDP sender failed")
        except queue.Empty as error:
            raise RuntimeError("UDP sender did not report send times") from error
        finally:
            if sender.is_alive():
                sender.terminate()
                sender.join(timeout=5)
            timing.close()
            timing.join_thread()
        send_times.update(sent_at)
        return (len(subset) - 1) / (ended - started) if len(subset) > 1 else None

    before_confirmations = {}
    try:
        run_send(packets[:args.warmup], args.warmup)
        before_confirmations, _ = scrape_ffm_metrics(args.metrics_url, args.json_topic) \
            if args.mode == "ffm" else ({}, None)
        actual_rate = run_send(packets[args.warmup:], len(packets))
        drained, final_watermarks = drain_until_quiet()
        after_confirmations, pending_publications = scrape_ffm_metrics(
            args.metrics_url, args.json_topic) if args.mode == "ffm" else ({}, None)
    finally:
        consumer.close()

    rows = []
    latencies = []
    for message_id in sorted(expected_measured):
        if message_id not in raw or message_id not in output or message_id not in send_times:
            continue
        raw_partition, raw_offset, raw_timestamp, raw_key = raw[message_id]
        json_partition, json_offset, json_timestamp, json_key = output[message_id]
        send_timestamp = send_times[message_id]
        latency = json_timestamp - send_timestamp if json_timestamp is not None else float("nan")
        if not math.isfinite(send_timestamp) or not math.isfinite(latency) or latency < 0:
            invalid_timestamps += 1
            continue
        if raw_key != json_key:
            unexpected_records += 1
            continue
        latencies.append(latency)
        rows.append((f"{message_id:08X}", raw_partition, raw_offset, raw_timestamp,
                     json_partition, json_offset, json_timestamp, send_timestamp, latency))

    ordered_send_times = [send_times[message_id] for message_id in sorted(expected_measured)
                          if message_id in send_times]
    send_gaps = [later - earlier for earlier, later in
                 zip(ordered_send_times, ordered_send_times[1:])]
    invalid_timestamps += sum(gap < 0 or not math.isfinite(gap) for gap in send_gaps)
    max_send_gap_ms = max(send_gaps, default=None)

    p50 = percent(latencies, .50)
    p95 = percent(latencies, .95)
    p99 = percent(latencies, .99)
    ack = {f"confirmed_p{quantile}_estimate_ms": confirmed_percent(
        before_confirmations, after_confirmations, fraction)
        for quantile, fraction in ((50, .50), (95, .95), (99, .99))}
    errors = validation_errors(expected_measured, raw, output, send_times,
        duplicates["raw"], duplicates["json"], unexpected_records, dlt_count,
        invalid_timestamps, actual_rate, args.rate, max_send_gap_ms, args.max_send_gap_ms,
        p95, args.max_p95_ms,
        pending_publications if args.mode == "ffm" else 0.0, drained)

    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream)
        writer.writerow(("bsm_id", "raw_partition", "raw_offset", "raw_create_time_ms",
                         "json_partition", "json_offset", "json_create_time_ms",
                         "udp_send_time_ms", "udp_to_json_record_ms"))
        writer.writerows(rows)

    summary = {"mode": args.mode, "raw_topic": args.raw_topic, "json_topic": args.json_topic,
        "dlt_topic": dlt_topic if args.mode == "ffm" else None,
        "target_rate_per_second": args.rate, "actual_udp_rate_per_second": actual_rate,
        "rate_tolerance_percent": 5, "max_inter_packet_send_gap_ms": max_send_gap_ms,
        "max_send_gap_ms": args.max_send_gap_ms,
        "warmup": args.warmup, "udp_sent": args.count,
        "raw_correlated": sum(message_id in raw for message_id in expected_measured),
        "json_correlated": sum(message_id in output for message_id in expected_measured),
        "paired": len(rows), "duplicate_raw": duplicates["raw"],
        "duplicate_json": duplicates["json"], "unexpected_records": unexpected_records,
        "unexpected_dlt_records": dlt_count, "invalid_timestamps": invalid_timestamps,
        "consumer_lag_drained": drained,
        "final_watermarks": {f"{topic}:{part}": end
                              for (topic, part), end in final_watermarks.items()},
        "pending_publications": pending_publications,
        "partitions": partitions, "p50_ms": p50, "p95_ms": p95, "p99_ms": p99,
        **ack, "max_p95_ms": args.max_p95_ms, "passed": not errors, "errors": errors}
    args.output.with_suffix(".json").write_text(json.dumps(summary, indent=2), encoding="utf-8")
    print(json.dumps(summary, indent=2))
    if errors:
        raise SystemExit("Benchmark acceptance failed: " + "; ".join(errors))


if __name__ == "__main__":
    main()

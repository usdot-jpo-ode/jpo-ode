"""Correlate distinct UDP BSMs with JSON Kafka records for codec comparisons.

Latency is the JSON record create time minus the UDP send time. ``--mode ffm`` correlates JSON
only and fails if ``topic.OdeRawEncodedBSMJson`` advances. ``--mode external`` requires a
correlated raw record for every measured BSM. ``--skip-raw`` forces that JSON-only correlation.

Requires confluent-kafka. Example: python scripts/tests/decode_benchmark.py --mode ffm
--broker localhost:9092 --fixture scripts/tests/udpsender_bsm.py --count 300000
--rate 1000 --output ffm-run-1.csv
"""

import argparse
import ast
import csv
import json
import multiprocessing
import queue
import random
import socket
import time
from pathlib import Path
from urllib.request import urlopen

from confluent_kafka import Consumer, TopicPartition


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
    ordered = sorted(values)
    if not ordered:
        return None
    position = (len(ordered) - 1) * fraction
    low = int(position)
    high = min(low + 1, len(ordered) - 1)
    return ordered[low] + (ordered[high] - ordered[low]) * (position - low)


def scrape_confirmed(url):
    try:
        text = urlopen(url, timeout=5).read().decode("utf-8")
    except Exception:
        return {}
    buckets = {}
    for line in text.splitlines():
        if not line.startswith("ode_ffmlib_decode_end_to_end_seconds_bucket{"):
            continue
        if 'type="BSM"' not in line:
            continue
        label = line.split('le="', 1)[1].split('"', 1)[0]
        buckets[float(label)] = float(line.rsplit(" ", 1)[1])
    return buckets


def raw_topic_movement(consumer, topic, start_offsets):
    moved = []
    for part, start in start_offsets.items():
        _, end = consumer.get_watermark_offsets(TopicPartition(topic, part), timeout=10)
        if end > start:
            moved.append({"partition": part, "start": start, "end": end})
    return moved


def confirmed_percent(before, after, fraction):
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


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", choices=("external", "ffm"), required=True)
    parser.add_argument("--broker", required=True)
    parser.add_argument("--fixture", type=Path, required=True)
    parser.add_argument("--udp-host", default="127.0.0.1")
    parser.add_argument("--udp-port", type=int, default=46800)
    parser.add_argument("--raw-topic", default="topic.OdeRawEncodedBSMJson")
    parser.add_argument("--json-topic", default="topic.OdeBsmJson")
    parser.add_argument("--skip-raw", action="store_true",
                        help="correlate JSON only; ffm always does this, external does not infer it")
    parser.add_argument("--metrics-url", default="http://127.0.0.1:8080/actuator/prometheus")
    parser.add_argument("--count", type=int, default=300000)
    parser.add_argument("--warmup", type=int, default=1000)
    parser.add_argument("--rate", type=float, default=1000)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    if args.mode == "ffm":
        args.skip_raw = True

    fixture = fixture_bytes(args.fixture)
    if packet_id(fixture) != 0x31325433:
        raise ValueError("BSM fixture changed; verify the TemporaryID bit offset")
    first_id = random.randrange(0, 0xffffffff - args.count - args.warmup)
    packets = [with_id(fixture, first_id + index)
               for index in range(args.count + args.warmup)]
    expected = set(range(first_id, first_id + len(packets)))
    consumer = Consumer({"bootstrap.servers": args.broker,
                         "group.id": f"decode-benchmark-{time.time_ns()}",
                         "enable.auto.commit": False,
                         "client.id": "ode-decode-benchmark"})
    partitions = {}
    assigned = []
    topics = (args.json_topic,) if args.skip_raw else (args.raw_topic, args.json_topic)
    for topic in topics:
        info = consumer.list_topics(topic, timeout=10).topics[topic]
        if info.error is not None:
            raise RuntimeError(f"Topic {topic} is not available: {info.error}")
        partitions[topic] = len(info.partitions)
        for part in info.partitions:
            _, end = consumer.get_watermark_offsets(TopicPartition(topic, part), timeout=10)
            assigned.append(TopicPartition(topic, part, end))
    consumer.assign(assigned)
    consumer.poll(0.1)
    raw_start = {}
    if args.mode == "ffm":
        raw_info = consumer.list_topics(args.raw_topic, timeout=10).topics[args.raw_topic]
        if raw_info.error is not None:
            raise RuntimeError(f"Topic {args.raw_topic} is not available: {raw_info.error}")
        for part in raw_info.partitions:
            _, end = consumer.get_watermark_offsets(TopicPartition(args.raw_topic, part), timeout=10)
            raw_start[part] = end
    raw = {}
    output = {}
    duplicate_raw = duplicate_json = 0

    def capture_until(required, deadline):
        nonlocal duplicate_raw, duplicate_json
        while time.monotonic() < deadline:
            raw_ready = args.skip_raw or len(raw) >= required
            if raw_ready and len(output) >= required:
                return
            record = consumer.poll(0.2)
            if record is None:
                continue
            if record.error():
                raise RuntimeError(record.error())
            value = json.loads(record.value())
            if record.topic() == args.raw_topic:
                asn1 = value.get("metadata", {}).get("asn1")
                if not asn1:
                    continue
                message_id = packet_id(bytes.fromhex(asn1))
                if message_id not in expected:
                    continue
                if message_id in raw:
                    duplicate_raw += 1
                else:
                    raw[message_id] = (record.partition(), record.offset(),
                                       record.timestamp()[1])
            else:
                try:
                    bsm = value["payload"]["data"]["value"]["BasicSafetyMessage"]
                    message_id = int(bsm["coreData"]["id"], 16)
                except (KeyError, TypeError, ValueError):
                    continue
                if message_id not in expected:
                    continue
                if message_id in output:
                    duplicate_json += 1
                else:
                    output[message_id] = (record.partition(), record.offset(),
                                          record.timestamp()[1])

    raw_movement = []
    send_times = {}

    def run_send(subset):
        timing = multiprocessing.Queue()
        sender = multiprocessing.Process(target=send_packets,
            args=(subset, args.udp_host, args.udp_port, args.rate, timing))
        sender.start()
        seen = len(output) if args.skip_raw else len(raw)
        capture_until(seen + len(subset), time.monotonic() + len(subset) / args.rate + 60)
        try:
            started, ended, sent_at = timing.get(timeout=120)
        except queue.Empty:
            sender.terminate()
            sender.join(timeout=5)
            raise RuntimeError("UDP sender did not report send times")
        sender.join(timeout=30)
        if sender.exitcode != 0:
            raise RuntimeError("UDP sender failed")
        send_times.update(sent_at)
        return (len(subset) - 1) / (ended - started) if len(subset) > 1 else None

    try:
        run_send(packets[:args.warmup])
        raw_warm = args.skip_raw or len(raw) >= args.warmup
        if not raw_warm or len(output) < args.warmup:
            raise RuntimeError("Warmup records did not clear before measurement")
        before = scrape_confirmed(args.metrics_url) if args.mode == "ffm" else {}
        actual_rate = run_send(packets[args.warmup:])
        after = scrape_confirmed(args.metrics_url) if args.mode == "ffm" else {}
        if args.mode == "ffm":
            raw_movement = raw_topic_movement(consumer, args.raw_topic, raw_start)
    finally:
        consumer.close()

    ids = range(first_id + args.warmup, first_id + len(packets))
    rows = []
    for message_id in ids:
        if message_id not in output or message_id not in send_times:
            continue
        if not args.skip_raw and message_id not in raw:
            continue
        if message_id in raw:
            raw_partition, raw_offset, raw_time = raw[message_id]
        else:
            raw_partition = raw_offset = raw_time = ""
        json_partition, json_offset, json_time = output[message_id]
        udp_send_ms = send_times[message_id]
        rows.append((f"{message_id:08X}", raw_partition, raw_offset, raw_time,
                     json_partition, json_offset, json_time,
                     udp_send_ms, json_time - udp_send_ms))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream)
        writer.writerow(("bsm_id", "raw_partition", "raw_offset", "raw_create_time_ms",
                         "json_partition", "json_offset", "json_create_time_ms",
                         "udp_send_time_ms", "udp_to_json_record_ms"))
        writer.writerows(rows)
    latency = [row[-1] for row in rows]
    summary = {"mode": args.mode, "skip_raw": args.skip_raw,
        "target_rate_per_second": args.rate,
        "actual_udp_rate_per_second": actual_rate, "warmup": args.warmup,
        "udp_sent": args.count, "raw_correlated": sum(i in raw for i in ids),
        "json_correlated": sum(i in output for i in ids), "paired": len(rows),
        "duplicate_raw": duplicate_raw, "duplicate_json": duplicate_json,
        "raw_watermark_advanced": raw_movement,
        "partitions": partitions, "p50_ms": percent(latency, .5),
        "p95_ms": percent(latency, .95), "p99_ms": percent(latency, .99),
        "confirmed_p50_estimate_ms": confirmed_percent(before, after, .5),
        "confirmed_p95_estimate_ms": confirmed_percent(before, after, .95),
        "confirmed_p99_estimate_ms": confirmed_percent(before, after, .99)}
    args.output.with_suffix(".json").write_text(json.dumps(summary, indent=2),
        encoding="utf-8")
    print(json.dumps(summary, indent=2))
    if raw_movement:
        raise SystemExit(f"FFM mode wrote raw topic {args.raw_topic}: {raw_movement}")
    if len(rows) != args.count:
        missing = "JSON" if args.skip_raw else "raw or JSON"
        raise SystemExit(f"Missing {missing} records")


if __name__ == "__main__":
    main()

# ODE decoding benchmarks

These tools measure UDP ingestion through decoded JSON publication in Kafka and
compare in-process FFM decoding with External ADM. Run the commands below from
the repository root.

## Files

| File | Purpose |
| --- | --- |
| `decode_benchmark.py` | Send one message type, correlate raw and JSON records, and write latency CSV and JSON results. |
| `run_linux_ffmlib_latency.py` | Run one BSM workload against the local Compose stack, temporarily remove container limits, collect host/container evidence, and restore limits. |
| `run_linux_codec_comparison.py` | Compare message types sequentially using prepared packet corpora on the local Compose stack. Supports a separate four-worker external run. |
| `run_linux_multitype_compare.py` | Compare FFM and four-worker external ADM from the current checkout using isolated Compose projects and ten evenly interleaved message types. |
| `generate_multitype_corpus.py` | Generate and validate the 51,000 unique beta6-encoded packets per type required for the mixed 500/s comparison. |
| `summarize_codec_comparison.py` | Produce comparison CSV, JSON, and Jira Markdown from sequential comparison evidence. |
| `summarize_linux_multitype_mixed.py` | Produce aggregate CSV, JSON, and Jira Markdown from the recorded mixed-message comparison. |
| `test_decode_benchmark.py`, `test_codec_comparison.py` | Unit tests for benchmark logic and comparison helpers. |
| `Dockerfile`, `requirements.txt` | Pinned Python container and Kafka client dependency for the Compose benchmark service. |

UDP fixture scripts remain in `scripts/tests/udpsender_*.py`. Evidence and packet
corpora are stored under `scripts/tests/output/` so historical result paths
continue to work. The Compose service mounts this folder at
`/output`, fixtures at `/tests`, and benchmark code at `/benchmarks`.

## Prerequisites

- A Linux host with Docker, Compose v2 supporting `!reset` and `!override`, and
  permission to inspect containers and read their cgroup v2 statistics.
- The repository's normal `.env`, native library/build prerequisites, and
  external codec checkout when testing ADM. Configure these using the root
  README and `sample.env`; keep credentials out of published evidence.
- Healthy ODE and Kafka services, with UDP receivers and the required Kafka
  topics enabled. Expose ODE's `health` and `prometheus` actuator endpoints.
- For the mixed comparison, build the beta6 native library and generate valid,
  distinct UPER packet corpora with `generate_multitype_corpus.py`.

The host orchestration and summary scripts use the Python standard library.
To run the workload or unit tests directly on the host, install the Kafka client
in a virtual environment:

```bash
python3 -m venv /tmp/ode-benchmark-venv
/tmp/ode-benchmark-venv/bin/pip install -r scripts/benchmarks/requirements.txt
```

Use Compose for measured workloads so the sender, Kafka, and ODE share the Linux
host clock and network. Internal endpoints are `ode:<UDP port>`, `kafka:9094`,
`http://ode:8080/actuator/prometheus`, and `http://ode:8080/actuator/health`.
A different exposed host port does not change these internal endpoints.

## One workload against an existing deployment

Start and configure the application stack for the desired codec mode first.
This command runs the workload only; it does not change limits or collect
container resource samples:

```bash
mkdir -p scripts/tests/output/linux-example
docker compose --profile all --profile benchmark build decode-benchmark
docker compose --profile all --profile benchmark run --rm --no-deps decode-benchmark \
  --mode ffm --broker kafka:9094 \
  --fixture /tests/udpsender_bsm.py \
  --udp-host ode --udp-port 46800 \
  --metrics-url http://ode:8080/actuator/prometheus \
  --health-url http://ode:8080/actuator/health \
  --consumer-group RawEncodedBSMJsonRouter \
  --count 50000 --warmup 1000 --rate 1000 --max-p95-ms 5 \
  --quiet-period-seconds 15 --drain-timeout-seconds 120 \
  --output /output/linux-example/bsm.csv
```

For other types, use `--packet-file /output/<corpus>.packets` instead of
`--fixture`, and set `--message-type`, `--udp-port`, `--raw-topic`, `--json-topic`,
and `--consumer-group` to match that type. Packet files contain one distinct,
valid hex datagram per line and need at least `warmup + count` packets.

For External ADM, configure ODE in external mode and start ADM, then use
`--mode external`. Add `--drain-group GROUP:TOPIC` for each external pipeline
group, for example `AsnDecode:topic.Asn1DecoderInput` and
`Asn1DecodedDataRouter:topic.Asn1DecoderOutput`. Match the input group to the
actual ADM configuration; comparison runners use `AsnDecodeLatencyBenchmark`.
FFM-only metrics and quarantine checks are skipped in external mode.

## One BSM workload with resource collection and temporary uncapping

Against the local Compose deployment:

```bash
python3 scripts/benchmarks/run_linux_ffmlib_latency.py \
  --count 50000 --warmup 1000 --rate 1000
```

Defaults are 300,000 measured BSMs, 1,000 warmups, and 1,000/s. The runner builds
images, uses a temporary Compose override, verifies ODE and Kafka are uncapped
before sending, targets one-second cgroup sampling, and restores the deployment
limits in its cleanup path. It writes a timestamped `linux-*` directory with
latency CSV/JSON, `resource-samples.csv`, `runtime-evidence.json`, and `runner.log`.
Check restoration status in the evidence even if setup or acceptance fails.
This runner recreates services in the existing stack; use a dedicated test
deployment when other work depends on those containers.

## Sequential message-type comparison

Place packet files such as `bsm.packets` and `map.packets` directly in the output
directory, with 1,000 warmups plus the requested measured count per type:

```bash
python3 scripts/benchmarks/run_linux_codec_comparison.py \
  --output-dir scripts/tests/output/linux-sequential-example \
  --message-type bsm --message-type map --count 50000 --rate 1000
```

Omit `--message-type` to select all ten types. To run only External ADM with four
workers and verify four input/output topic partitions:

```bash
python3 scripts/benchmarks/run_linux_codec_comparison.py \
  --output-dir scripts/tests/output/linux-sequential-example \
  --external-only --adm-processes 4 --message-type map --count 50000 --rate 100
```

`--resume` skips existing measured results, including failed ones. These runners
use the local stack and include repository-specific container names and setup
assumptions; review their settings before adapting them to another deployment.
They restore the FFM stack and limits during cleanup.

## Mixed-message FFM versus External ADM comparison

The mixed runner reproduces the completed ten-type workload: BSM, MAP, SPAT, TIM,
PSM, SRM, SSM, SDSM, RTCM, and RSM, at **500 messages/s total**, evenly interleaved
at 50/s per type. Defaults are 1,000 warmups and 50,000 measured messages per type,
or 500,000 measured messages per solution.

Generate the shared corpus first. The generator reads beta6-decodable J2735
examples from `scripts/tests`; the checked-in BSM and PSM sender packets fail
beta6 decoding, so it uses the corresponding valid ODE test fixtures for those
two types. Each packet varies a valid ASN.1 field, and the manifest records the
source and SHA-256 digest for each corpus file.

```bash
python3 scripts/benchmarks/generate_multitype_corpus.py
```

Run each mode once, using the same generated packet files and a new output directory
for each comparison (the runner refuses to overwrite an existing mode):

```bash
python3 scripts/benchmarks/run_linux_multitype_compare.py \
  --output-dir scripts/tests/output/linux-mixed-run-001 \
  --only ffm-local
python3 scripts/benchmarks/run_linux_multitype_compare.py \
  --output-dir scripts/tests/output/linux-mixed-run-001 \
  --only external-adm4
python3 scripts/benchmarks/summarize_linux_multitype_mixed.py \
  scripts/tests/output/linux-mixed-run-001
```

FFM runs select the UDP route with `--only`: `ffm-local` (raw-topic routing, raw
records published and consumed), `ffm-direct-raw` (direct routing with
`ODE_FFM_RAW_PUBLISH=true`), and `ffm-direct-noraw` (direct routing with
`ODE_FFM_RAW_PUBLISH=false`, so no raw records exist and only JSON is correlated).
Direct routing runs no application consumer group, so the consumer-group gate is
not applicable there, and the offset-commit gauge is not checked. The runner sets
`KAFKA_LINGER_MS=0` and `KAFKA_COMPRESSION_TYPE=none` on the ODE service in every
mode. Use one output directory for all modes; each mode runs in its own project.
Summarize mixed route runs, with gates that depend on each route, using:

```bash
python3 scripts/benchmarks/summarize_ffm_routes.py \
  scripts/tests/output/linux-mixed-run-001
```

The runner compares the current checkout in FFM and external ADM modes by
default; `--external-tree` can point the external side at another checkout. It
uses separate Compose projects and volumes, uncaps ODE and Kafka (and ADM for
the external run), verifies cgroups, actuator health and Prometheus, topic
partitions, and all four ADM workers before sending traffic. ODE is exposed on
port 18080 by default. The runner records one-second cgroup samples and removes
only the selected isolated project and its volumes after each run. It refuses
to overwrite evidence for a selected mode; do not rerun a failed workload.

## Summaries and Jira documents

Summary commands read saved evidence without sending traffic:

```bash
python3 scripts/benchmarks/summarize_codec_comparison.py \
  scripts/tests/output/linux-sequential-example
python3 scripts/benchmarks/summarize_linux_multitype_mixed.py \
  scripts/tests/output/linux-20261008-beta6-500hz-verified
```

The mixed summarizer recomputes each gate from the saved per-type CSV/JSON,
runtime preflight, and cgroup evidence. It requires the exact measured identity
range and does not exempt any message type. It writes aggregate CSV/JSON and
`jira-performance-report.md` alongside the original per-type evidence.

The completed beta6 comparison report is at
`scripts/tests/output/linux-20261008-beta6-500hz-verified/jira-performance-report.md`.
FFM passed all ten per-type gates; external ADM met the p95 target only for RTCM.
An earlier external setup attempt stopped at the pre-traffic process-readiness
check and sent no packets; its evidence remains in the sibling
`linux-20261008-beta6-500hz` directory.

## Reading results

Latency is **JSON Kafka CreateTime minus UDP send time**, not client consumption
time. Kafka timestamps have millisecond resolution. CSV files retain individual
correlations; JSON files report p50/p95/p99, actual rate, record counts,
duplicates, drain/health checks, and available stage metrics.

A strict acceptance run requires complete correlation, valid timestamps, zero
duplicates and unexpected DLT records, drained application offsets, healthy
services throughout the quiet period, and p95 **strictly below 5 ms** when using
`--max-p95-ms 5`. FFM also requires zero pending raw publications, output
publications, and commits. A p95 equal to 5 ms fails. Report exceptions explicitly
without changing the original evidence. Do not automatically rerun failures.

Resource evidence records VM capacity, effective limits, actual sampling cadence,
collection errors, CPU usage, memory usage, throttling, and pressure counters.
CPU percentages convert to logical cores by dividing by 100. Total solution
usage includes Kafka and, for external decoding, ADM. Uncapped containers remain
bounded by VM capacity; such results do not establish latency under shipped
deployment limits. A single run is evidence for that workload, not repeatability.

## Unit tests

To run the existing benchmark unit tests explicitly:

```bash
/tmp/ode-benchmark-venv/bin/python -m unittest discover \
  -s scripts/benchmarks -p 'test_*.py'
```

These tests do not launch a measured workload. Each runner also provides
`--help` for its supported command options.

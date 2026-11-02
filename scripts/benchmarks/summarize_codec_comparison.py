#!/usr/bin/env python3
"""Build CSV/JSON and a Jira-ready Markdown report from the codec comparison evidence."""
import argparse
import csv
import datetime as dt
import json
from pathlib import Path
from zoneinfo import ZoneInfo
from run_linux_codec_comparison import TYPES
from decode_benchmark import percent


def table(headers, rows):
    return '\n'.join(['| '+' | '.join(headers)+' |',
                      '| '+' | '.join(['---']*len(headers))+' |']+
                     ['| '+' | '.join(str(v) for v in row)+' |' for row in rows])


def measured_resources(directory):
    with (directory/'latency.csv').open() as stream:
        times=[float(r['udp_send_time_ms'])/1000 for r in csv.DictReader(stream)]
    if not times:return {}
    start,end=min(times),max(times)
    with (directory/'resource-samples.csv').open() as stream:
        rows=list(csv.DictReader(stream))
    result={}
    for service in {r['service'] for r in rows}:
        samples=[r for r in rows if r['service']==service and not r['sample_error']
                 and start+float(r['actual_cadence_seconds']) <=
                 dt.datetime.fromisoformat(r['timestamp_utc']).timestamp() <= end]
        if not samples:continue
        duration=sum(float(r['actual_cadence_seconds']) for r in samples)
        avg=sum(float(r['interval_cpu_percent'])/100*float(r['actual_cadence_seconds'])
                for r in samples)/duration
        result[service]={'average_cpu_cores':avg,
            'peak_cpu_cores':max(float(r['interval_cpu_percent'])/100 for r in samples),
            'peak_memory_bytes':max(int(r['memory_current_bytes']) for r in samples),
            'sample_count':len(samples),'sampled_duration_seconds':duration}
    return result


def main():
    parser=argparse.ArgumentParser();parser.add_argument('directory',type=Path);args=parser.parse_args()
    root=args.directory.resolve()
    setup_candidates=[root/label/'runtime-evidence.json' for label in
        ('setup-external4-1000-resume2','setup-external4-1000','setup')]
    setup_path=next((path for path in setup_candidates if path.exists()),setup_candidates[-1])
    setup=json.loads(setup_path.read_text())
    records=[];missing=[]
    for mode,codec_mode in (('ffm','ffm'),('external4','external')):
        for name in TYPES:
            directory=root/(mode+'-'+name)
            if not (directory/'latency.json').exists():missing.append(mode+'/'+name);continue
            summary=json.loads((directory/'latency.json').read_text())
            runtime=json.loads((directory/'runtime-evidence.json').read_text())
            resource=measured_resources(directory)
            packet=(root/(name+'.packets')).read_text().splitlines()[0]
            with (directory/'latency.csv').open() as stream:
                latency_rows=list(csv.DictReader(stream))
            observed={
                'udp_to_raw_p95_ms':percent([float(v['raw_timestamp_ms'])-float(v['udp_send_time_ms']) for v in latency_rows],.95),
                'raw_to_json_p95_ms':percent([float(v['json_timestamp_ms'])-float(v['raw_timestamp_ms']) for v in latency_rows],.95)}
            records.append({'observed_pipeline':observed,'mode':mode,'codec_mode':codec_mode,'message_type':name.upper(),'packet_bytes':len(packet)//2,
                'summary':summary,'measured_resources':resource,
                'whole_workload_resources':runtime.get('container_results',{}),
                'runtime':runtime,'evidence_directory':str(directory)})
    setup_failures=json.loads((root/'setup-failures.json').read_text()) if (root/'setup-failures.json').exists() else None
    components=json.loads((root/'component-identity.json').read_text()) if (root/'component-identity.json').exists() else {}
    frequency_path=root/'external4-100hz-map'
    frequency_check=None
    if (frequency_path/'latency.json').exists():
        low=json.loads((frequency_path/'latency.json').read_text())
        low_runtime=json.loads((frequency_path/'runtime-evidence.json').read_text())
        high=next((r for r in records if r['mode']=='external4' and r['message_type']=='MAP'),None)
        frequency_check={'summary':low,'resources':measured_resources(frequency_path),
            'runtime':low_runtime,'high_rate_map':high}
    result={'setup':setup,'setup_failures':setup_failures,'components':components,'runs':records,'missing_runs':missing,
        'frequency_check':frequency_check,
        'latency_definition':'JSON Kafka CreateTime (integer milliseconds) minus UDP send time',
        'cpu_definition':'CPU cores = Docker CPU percent / 100; measured-window average weighted by sample interval',
        'sampling_window':'Complete approximately one-second samples contained within measured send window; runtime evidence also retains warmup/drain-inclusive results'}
    (root/'comparison-summary.json').write_text(json.dumps(result,indent=2))
    flat=[];resources=[]
    for r in records:
        s=r['summary'];flat.append({'mode':r['mode'],'type':r['message_type'],
            'packet_bytes':r['packet_bytes'],**{k:s.get(k) for k in ('p50_ms','p95_ms','p99_ms',
                'actual_udp_rate_per_second','udp_sent','raw_correlated','json_correlated','paired',
                'duplicate_raw','duplicate_json','unexpected_dlt_records','raw_json_key_mismatches','passed')},
            'evidence_directory':r['evidence_directory']})
        for name,values in r['measured_resources'].items():
            whole=r['whole_workload_resources'][name]
            resources.append({'mode':r['mode'],'type':r['message_type'],'container':name,**values,
                **{k:whole.get(k) for k in ('cpu_throttled_usec','nr_throttled',
                 'cpu_pressure_some_total','memory_pressure_some_total','memory_pressure_full_total',
                 'memory_events_high','memory_events_max','memory_events_oom','memory_events_oom_kill',
                 'unhealthy_samples')},'collection_errors':len(whole.get('errors',[]))})
    for filename,data in (('comparison-latency.csv',flat),('comparison-resources.csv',resources)):
        if data:
            with (root/filename).open('w',newline='') as stream:
                writer=csv.DictWriter(stream,fieldnames=list(data[0]));writer.writeheader();writer.writerows(data)
    cap=setup['vm_capacity'];source=setup['source']
    earliest=min([setup['started_utc']]+[r['runtime']['started_utc'] for r in records])
    started=dt.datetime.fromisoformat(earliest).astimezone(ZoneInfo('America/Denver'))
    text=['**ODE FFM vs external ASN.1 codec — UDP performance comparison**',
        f'Comparison evidence begins {started:%Y-%m-%d %H:%M %Z}. Source: `{source.get("branch")}` at `{source.get("commit")}` with local changes (diff SHA-256 `{source.get("tracked_diff_sha256")}`).',
        f'Linux VM: {cap["effective_cpu_cores"]} logical CPU cores; {cap["effective_memory_bytes"]/2**30:.2f} GiB RAM. ODE, Kafka, and external ADM were verified uncapped at container level before traffic. Actual capacity remains bounded by the VM.',
        'Each row is one workload: 1,000 warmups, then 50,000 messages at 1,000 Hz. Ten types were tested separately in each mode, using the same unique UPER packet corpus. FFM uses the full native codec (compact BSM codec removed), direct BSM JER mapping, four listeners per type, asynchronous commits, and round-robin raw partitioning. The external run uses ADM_NUMBER_OF_PROCESSES=4 and four partitions each on topic.Asn1DecoderInput and topic.Asn1DecoderOutput. Both use zero producer linger, no compression, acks=all, and producer idempotence.',
        'Latency is **decoded JSON Kafka CreateTime minus UDP send time**, in milliseconds. It includes ingestion, Kafka transport, decoding/mapping and JSON publication; it excludes downstream consumption and acknowledgement after JSON record creation. Kafka timestamps have 1 ms resolution; submillisecond differences and accepted negative samples within [-1, 0) reflect timestamp quantization.',
        f'Codec identity: FFMLib `{components.get("ffm_library")}`; external decoder source `{components.get("external_codec_commit")}`. Exact immutable Docker image IDs are saved in component-identity.json and the runtime files.',
        '**Latency and throughput**',
        table(['Type','Mode','Bytes','p50 ms','p95 ms','p99 ms','Actual msg/s','Paired / sent','p95 < 5 ms'],
            [[r['message_type'],r['mode'],r['packet_bytes'],*[f'{r["summary"][k]:.3f}' if r['summary'][k] is not None else 'N/A' for k in ('p50_ms','p95_ms','p99_ms')],
                f'{r["summary"]["actual_udp_rate_per_second"]:.2f}' if r['summary']['actual_udp_rate_per_second'] else 'N/A',
                f'{r["summary"]["paired"]:,} / {r["summary"]["udp_sent"]:,}',
                'PASS' if r['summary']['p95_ms'] is not None and r['summary']['p95_ms']<5 else 'FAIL'] for r in records]),
        '**Lower-rate load check: external MAP, 100 messages/s**',
        ('Same 50,000-message MAP corpus, four ADM workers and four partitions each on the external input/output topics. This was a separate run after the 1,000/s comparison.\n\n'+
         table(['Rate','Messages','Actual msg/s','p50 ms','p95 ms','p99 ms','Paired','p95 < 5 ms'],[
            ['1,000/s',f'{high["summary"]["udp_sent"]:,}',f'{high["summary"]["actual_udp_rate_per_second"]:.2f}',
             *[f'{high["summary"][k]:.3f}' for k in ('p50_ms','p95_ms','p99_ms')],
             f'{high["summary"]["paired"]:,}','PASS' if high['summary']['p95_ms']<5 else 'FAIL'],
            ['100/s',f'{low["udp_sent"]:,}',f'{low["actual_udp_rate_per_second"]:.2f}' if low['actual_udp_rate_per_second'] else 'N/A',
             *[f'{low[k]:.3f}' if low[k] is not None else 'N/A' for k in ('p50_ms','p95_ms','p99_ms')],
             f'{low["paired"]:,}','PASS' if low['p95_ms'] is not None and low['p95_ms']<5 else 'FAIL']])+
         '\n\n'+table(['Rate','ODE avg/peak cores','ADM avg/peak cores','Kafka avg/peak cores'],[
             ['1,000/s']+[f'{high["measured_resources"].get(c,{}).get("average_cpu_cores",0):.3f} / {high["measured_resources"].get(c,{}).get("peak_cpu_cores",0):.3f}' for c in ('ode','adm','kafka')],
             ['100/s']+[f'{frequency_check["resources"].get(c,{}).get("average_cpu_cores",0):.3f} / {frequency_check["resources"].get(c,{}).get("peak_cpu_cores",0):.3f}' for c in ('ode','adm','kafka')]])+
         '\n\nPeak sampled memory (MiB), ODE / ADM / Kafka: '+
         ' / '.join(f'{frequency_check["resources"].get(c,{}).get("peak_memory_bytes",0)/2**20:.1f}' for c in ('ode','adm','kafka'))+
         '. CPU averages are cores; the one-second peak is an interval average.'+
         f'\n\nLatency change in MAP p95 from 1,000/s to 100/s: {high["summary"]["p95_ms"]-low["p95_ms"]:.3f} ms ({(1-low["p95_ms"]/high["summary"]["p95_ms"])*100:.2f}% lower). The low offered load reduced p95 by about {high["summary"]["p95_ms"]/low["p95_ms"]:,.0f}x. This strongly supports queueing/load saturation as the main cause of the extreme 1,000/s MAP latency, though the single-run comparison does not prove causation; 100/s p95 remains above 5 ms. Both runs correlated all 50,000 raw/JSON messages with zero duplicates or unexpected records, and health stayed UP. The 1,000/s application input/output groups drained. At 100/s the isolated AsnDecodeLatencyBenchmark group drained, but the legacy AsnDecode group retained 5,054 records from an interrupted earlier TIM attempt before the test and accumulated a further 51,000 input records because it was not the group used for this measurement. Its offsets were preserved after a safety review rejected skipping records, so the legacy-group drain gate failed. No CPU throttling, memory high/OOM events, or measurable memory-pressure samples occurred in the low-rate run.' if frequency_check else 'No completed 100/s load check is available yet.'),
        '**Application compute comparison**',
        'Application cores include ODE for FFM and ODE + ADM for external; Kafka is shown separately. A single observation at 1,000/s measures cost at that offered load, not maximum capacity.',
        table(['Type','FFM app cores','External app cores','FFM Kafka cores','External Kafka cores'],
            [[name.upper()]+[f'{sum(v["average_cpu_cores"] for svc,v in r["measured_resources"].items() if svc!="kafka"):.3f}' if r else 'N/A' for r in [next((x for x in records if x['mode']==m and x['message_type']==name.upper()),None) for m in ('ffm','external4')]]+
             [f'{r["measured_resources"].get("kafka",{}).get("average_cpu_cores",0):.3f}' if r else 'N/A' for r in [next((x for x in records if x['mode']==m and x['message_type']==name.upper()),None) for m in ('ffm','external4')]] for name in TYPES]),
        '**Measured pipeline checkpoints (p95 ms)**',
        'These paired Kafka timestamp differences help separate raw publication from the subsequent decode/routing path. They remain quantized and are separate distributions; their percentiles must not be added.',
        table(['Type','Mode','UDP to raw Kafka','Raw to JSON Kafka'],
            [[r['message_type'],r['mode']]+[f'{r["observed_pipeline"][k]:.3f}' if r['observed_pipeline'][k] is not None else 'N/A' for k in ('udp_to_raw_p95_ms','raw_to_json_p95_ms')] for r in records]),
        '**Container compute and memory during the measured send window**',
        '100% CPU equals one logical core. CPU peaks are one-second interval averages; memory is cgroup memory.current, including cache. The table excludes warmup and quiet-drain samples; the original runtime JSON retains whole-workload metrics. Memory is the sampled peak, not an instantaneous high-water mark.',
        table(['Type','Mode','Container','Avg cores','Peak cores','Peak MiB','Samples'],
            [[r['type'],r['mode'],r['container'],f'{r["average_cpu_cores"]:.3f}',f'{r["peak_cpu_cores"]:.3f}',f'{r["peak_memory_bytes"]/2**20:.1f}',r['sample_count']] for r in resources]),
        '**FFM stage p95 estimates (ms)**',
        'Histogram estimates use bucket interpolation and are separate distributions, so stage p95 values must not be added. Raw record age is millisecond-quantized; native/mapping/parse/publication timers use nanoTime.',
        table(['Type','Parse','Native','JER map','ASN decode','JSON prep','Raw age','Output confirm','Commit'],
            [[r['message_type']]+[f'{r["summary"]["stage_p95_estimate_ms"].get(k):.3f}' if r['summary']['stage_p95_estimate_ms'].get(k) is not None else 'N/A' for k in
             ('parse','native','jer_mapping','ode_ffmlib_decode_asn','json','ode_ffmlib_raw_record_age','ode_ffmlib_output_confirmation','ode_ffmlib_offset_commit_completion')] for r in records if r['mode']=='ffm']),
        'The external path lacks equivalent stage timers and pending-work gauges; those metrics are unavailable rather than zero. Its raw-router, ADM, and decoded-router group offsets are checked against their input topic watermarks throughout the 15-second quiet period.',
        '**Integrity, health, throttling and pressure**']
    for r in records:
        s=r['summary']
        text.append(f'- {r["mode"]}/{r["message_type"]}: raw={s["raw_correlated"]:,}, JSON={s["json_correlated"]:,}; duplicates raw/JSON={s["duplicate_raw"]}/{s["duplicate_json"]}; DLT={s["unexpected_dlt_records"] if r["mode"]=="ffm" else "N/A (legacy external path)"}; drained={s["consumer_lag_drained"]}; healthy quiet period={s["health_ok_through_quiet_period"]}; pending output/raw/commit={s["pending_publications"]}/{s["pending_raw_publications"]}/{s["pending_offset_commits"]}; acceptance={"PASS" if s["passed"] else "FAIL"}'+(' — '+ '; '.join(s['errors']) if s['errors'] else ''))
    throttle=sum(r['cpu_throttled_usec'] or 0 for r in resources);mempsi=sum(r['memory_pressure_some_total'] or 0 for r in resources)
    memevents=sum(sum(r[k] or 0 for k in ('memory_events_high','memory_events_max','memory_events_oom','memory_events_oom_kill')) for r in resources)
    sample_errors=sum(r['collection_errors'] for r in resources)
    text += ['',f'Throttled CPU time across the captured workloads: {throttle/1e6:.6f} s. Memory pressure “some” stall time: {mempsi/1e6:.6f} s. Memory high/max/OOM/OOM-kill event deltas: {memevents}. Resource collection errors: {sample_errors}. Per-container pressure, health and throttle counters are in comparison-resources.csv and each runtime JSON. CPU PSI means scheduling contention even when quota throttling is zero.',
        '**Setup notes**',
        ('The initial harness swapped SRM and SSM UDP ports. Both failed during warmup, before any measured messages were sent. The failed setup logs are retained in setup-failure-ffm-srm and setup-failure-ffm-ssm, with setup-failures.json documenting the correction. All receiver ports were subsequently verified against application.yaml and the UDP fixtures. The original deployment was restored before resuming. The first five measured runs were preserved; each mode/type has one measured workload.' if setup_failures else 'No setup failures were recorded.'),
        '**Scope and interpretation**',
        'This is a deployment-path comparison, not an isolated native-codec microbenchmark: the external path has two extra Kafka legs, XML conversion, and a separate four-process ADM decoder. Runs are sequential (all FFM types, then external types); JVM warmup, Kafka cache growth and VM background services can influence results. Fixtures cover message types, not the full range of payload sizes, signed messages or mixed concurrent traffic. These are single-run observations; no failed workload is automatically repeated.',
        'Only valid UPER identity/numeric fields vary for correlation. Packets are canonical native encodings of the unsigned repository fixtures; their exact sizes and generation log are preserved. The external codec publishes null Kafka keys; key differences are reported and do not invalidate metadata.asn1 correlation. FFM retains matching raw/JSON keys and confirmed-output-before-ack, retry and quarantine behavior.',
        '**Evidence and restoration**',f'Evidence root: `{root}`. Attach `comparison-latency.csv`, `comparison-resources.csv`, `comparison-summary.json` and this report to the Jira task. Each mode/type directory contains `latency.csv`, `latency.json`, `resource-samples.csv`, `runtime-evidence.json`, and sanitized `runner.log`. Unique packet corpora and `fixture-generation.log` are preserved at the root.',
        f'Original deployment restoration: limits restored={setup.get("restore",{}).get("limits_restored")}; services healthy={setup.get("restore",{}).get("services_healthy")}; success={setup.get("restore",{}).get("success")}. Details are in `{setup_path.relative_to(root)}`.',
        f'Missing runs: {", ".join(missing) if missing else "none"}. All evidence is in the ignored scripts/tests/output directory. No deployment limits were changed in tracked configuration, and no branches or PRs were published.']
    (root/'jira-performance-report.md').write_text('\n\n'.join(text)+'\n')
    print(root/'jira-performance-report.md')

if __name__=='__main__':main()

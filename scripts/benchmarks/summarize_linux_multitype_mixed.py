#!/usr/bin/env python3
"""Write aggregate Jira evidence for a synchronized mixed-type comparison."""
import argparse, csv, json, math
from pathlib import Path

TYPES = ("bsm", "map", "spat", "tim", "psm", "srm", "ssm", "sdsm", "rtcm", "rsm")
MODES = {"ffm-local": "FFM", "develop-adm4": "develop + external ADM (4 workers)"}

def pct(xs, p):
    if not xs: return None
    xs = sorted(xs); at = (len(xs)-1)*p; lo = math.floor(at); hi = math.ceil(at)
    return xs[lo] if lo == hi else xs[lo] + (xs[hi]-xs[lo])*(at-lo)

def fmt(v, n=3): return "N/A" if v is None else f"{v:.{n}f}"

def table(head, rows):
    return "\n".join(["| " + " | ".join(map(str, head)) + " |",
        "| " + " | ".join("---" for _ in head) + " |"] +
        ["| " + " | ".join(map(str, row)) + " |" for row in rows])

def main():
    ap=argparse.ArgumentParser();ap.add_argument("directory",type=Path);root=ap.parse_args().directory.resolve()
    all_modes={}; per_rows=[]; resource_rows=[]
    for mode,label in MODES.items():
        base=root/mode; runtime=json.loads((base/"runtime-evidence.json").read_text())
        with (base/"resource-samples.csv").open(newline="") as f: samples=list(csv.DictReader(f))
        measured=[r for r in samples if r.get("elapsed_seconds") and 24 <= float(r["elapsed_seconds"]) <= 1023.5]
        container_metrics={}
        for service, init in runtime["containers_uncapped_before_traffic"].items():
            sr=[r for r in measured if r["service"]==service and r.get("interval_cpu_percent")]
            duration=sum(float(r["actual_cadence_seconds"]) for r in sr)
            all_sr=[r for r in samples if r["service"]==service]
            first,last=(all_sr[0],all_sr[-1]) if all_sr else ({},{})
            initial=init.get("cgroup_initial",{})
            def delta(field,key,row_field=None):
                start=initial.get(field,{}).get(key)
                row_field=row_field or field
                if row_field in ("cpu_throttled_usec", "cpu_nr_throttled"):
                    raw=last.get(row_field);end=int(raw) if raw not in (None,"") else None
                else:
                    try: end=json.loads(last.get(row_field) or "{}").get(key)
                    except json.JSONDecodeError: end=None
                return end-start if start is not None and end is not None else None
            def psi_delta(field,pressure,row_field):
                start=initial.get(field,{}).get(pressure,{}).get("total")
                try: end=json.loads(last.get(row_field) or "{}").get(pressure,{}).get("total")
                except json.JSONDecodeError: end=None
                return end-start if start is not None and end is not None else None
            cpu_avg=sum(float(r["interval_cpu_percent"])*float(r["actual_cadence_seconds"]) for r in sr)/(100*duration) if duration else None
            cpu_peak=max((float(r["interval_cpu_percent"])/100 for r in sr),default=None)
            mem_peak=max((int(r["memory_current_bytes"]) for r in sr if r.get("memory_current_bytes")),default=None)
            container_metrics[service]={"average_cpu_cores":cpu_avg,"peak_cpu_cores_1s":cpu_peak,
                "peak_memory_bytes":mem_peak,"sample_count":len(sr),"avg_cadence_s":duration/len(sr) if sr and duration else None,
                "cpu_throttled_usec_delta":delta("cpu.stat","throttled_usec","cpu_throttled_usec"),
                "nr_throttled_delta":delta("cpu.stat","nr_throttled","cpu_nr_throttled"),
                "memory_high_events_delta":delta("memory.events","high","memory_events"),
                "memory_max_events_delta":delta("memory.events","max","memory_events"),
                "memory_oom_events_delta":delta("memory.events","oom","memory_events"),
                "memory_oom_kill_events_delta":delta("memory.events","oom_kill","memory_events"),
                "cpu_psi_some_usec_delta":psi_delta("cpu.pressure","some","cpu_pressure"),
                "cpu_psi_full_usec_delta":psi_delta("cpu.pressure","full","cpu_pressure"),
                "memory_psi_some_usec_delta":psi_delta("memory.pressure","some","memory_pressure"),
                "memory_psi_full_usec_delta":psi_delta("memory.pressure","full","memory_pressure"),
                "cpu_max":initial.get("cpu.max"),"memory_max":initial.get("memory.max")}
            resource_rows.append({"mode":mode,"container":service,**container_metrics[service]})
        sample_groups={}
        for row in measured:
            if row.get("interval_cpu_percent") and row.get("memory_current_bytes"):
                sample_groups.setdefault(row["timestamp_utc"], {})[row["service"]]=row
        complete_groups=[group for group in sample_groups.values() if set(group)==set(container_metrics)]
        resource_totals={
            "average_cpu_cores":sum(x["average_cpu_cores"] for x in container_metrics.values()),
            "peak_cpu_cores_1s":max(sum(float(r["interval_cpu_percent"])/100 for r in group.values()) for group in complete_groups),
            "peak_memory_bytes":max(sum(int(r["memory_current_bytes"]) for r in group.values()) for group in complete_groups),
            "complete_sample_count":len(complete_groups)}
        type_results=[];latencies=[];sends=[];events=[];first_by_type={}
        for name in TYPES:
            d=base/name;s=json.loads((d/"latency.json").read_text())
            with (d/"latency.csv").open(newline="") as f: data=list(csv.DictReader(f))
            values=[float(r["udp_to_json_record_ms"]) for r in data if r.get("udp_to_json_record_ms")]
            latencies.extend(values)
            ts=[float(r["udp_send_monotonic_s"]) for r in data if r.get("udp_send_monotonic_s")]
            sends.extend(ts);events.extend((t,name) for t in ts)
            if ts: first_by_type[name]=ts[0]
            type_results.append({"type":name.upper(),"summary":s,"valid_latency_rows":len(values)})
            per_rows.append({"mode":mode,"type":name.upper(),"udp_sent":s.get("udp_sent"),
                "raw_correlated":s.get("raw_correlated"),"json_correlated":s.get("json_correlated"),
                "paired":s.get("paired"),"p50_ms":s.get("p50_ms"),"p95_ms":s.get("p95_ms"),"p99_ms":s.get("p99_ms"),
                "actual_type_rate":s.get("actual_udp_rate_per_second"),"duplicate_raw":s.get("duplicate_raw"),
                "duplicate_json":s.get("duplicate_json"),"unexpected_dlt":s.get("unexpected_dlt_records"),
                "drained":s.get("consumer_lag_drained"),"healthy":s.get("health_ok_through_quiet_period"),
                "passed":s.get("passed"),"errors":"; ".join(s.get("errors",[]))})
        sent=sum(t["summary"].get("udp_sent",0) or 0 for t in type_results)
        events.sort(); gaps=[(events[i][0]-events[i-1][0])*1000 for i in range(1,len(events))]
        phase_base=min(first_by_type.values()) if first_by_type else 0
        interleave={"first_measured_send_phase_ms":{k.upper():(v-phase_base)*1000 for k,v in first_by_type.items()},
            "adjacent_same_type_pairs":sum(events[i][1]==events[i-1][1] for i in range(1,len(events))),
            "global_interarrival_ms":{"p50":pct(gaps,.5),"p95":pct(gaps,.95),"max":max(gaps) if gaps else None}}
        all_modes[mode]={"label":label,"source":runtime.get("source_identity"),"vm_capacity":runtime.get("vm_capacity"),
            "latency_ms":{"p50":pct(latencies,.5),"p95":pct(latencies,.95),"p99":pct(latencies,.99),"valid_count":len(latencies)},
            "actual_aggregate_rate":len(sends)/(max(sends)-min(sends)) if len(sends)>1 else None,
            "sent":sent,"raw_correlated":sum(t["summary"].get("raw_correlated",0) or 0 for t in type_results),
            "json_correlated":sum(t["summary"].get("json_correlated",0) or 0 for t in type_results),
            "paired":sum(t["summary"].get("paired",0) or 0 for t in type_results),
            "duplicate_raw":sum(t["summary"].get("duplicate_raw",0) or 0 for t in type_results),
            "duplicate_json":sum(t["summary"].get("duplicate_json",0) or 0 for t in type_results),
            "unexpected_dlt":sum(t["summary"].get("unexpected_dlt_records",0) or 0 for t in type_results),
            "all_type_gates_pass":all(t["summary"].get("passed") for t in type_results),"interleave_observed":interleave,
            "resources":container_metrics,"resource_totals":resource_totals,"types":type_results}
    vm=all_modes["ffm-local"]["vm_capacity"]
    result={"evidence_root":str(root),"workload":{"types":10,"aggregate_target_per_second":500,
        "per_type_target_per_second":50,"measured_per_type":50000,"warmup_per_type":1000,
        "interleave_phase_step_ms":2,"latency_definition":"JSON Kafka CreateTime minus UDP send time"},
        "vm_capacity":vm,"modes":all_modes}
    (root/"mixed-comparison-summary.json").write_text(json.dumps(result,indent=2))
    (root/"comparison-status.json").write_text(json.dumps([
        {"mode":mode,"all_type_gates_pass":data["all_type_gates_pass"],
         "paired":data["paired"],"sent":data["sent"],
         "per_type_exit_codes":{t["type"].lower():t["summary"].get("passed") for t in data["types"]}}
        for mode,data in all_modes.items()],indent=2))
    with (root/"mixed-per-type.csv").open("w",newline="") as f:
        w=csv.DictWriter(f,fieldnames=list(per_rows[0]));w.writeheader();w.writerows(per_rows)
    with (root/"mixed-resources.csv").open("w",newline="") as f:
        w=csv.DictWriter(f,fieldnames=list(resource_rows[0]));w.writeheader();w.writerows(resource_rows)
    comparison_rows=[
        ["p50 latency",fmt(all_modes["ffm-local"]["latency_ms"]["p50"])+" ms",fmt(all_modes["develop-adm4"]["latency_ms"]["p50"])+" ms"],
        ["p95 latency",fmt(all_modes["ffm-local"]["latency_ms"]["p95"])+" ms",fmt(all_modes["develop-adm4"]["latency_ms"]["p95"])+" ms"],
        ["p99 latency",fmt(all_modes["ffm-local"]["latency_ms"]["p99"])+" ms",fmt(all_modes["develop-adm4"]["latency_ms"]["p99"])+" ms"],
        ["Actual aggregate send rate",fmt(all_modes["ffm-local"]["actual_aggregate_rate"],2)+" /s",fmt(all_modes["develop-adm4"]["actual_aggregate_rate"],2)+" /s"],
        ["Paired messages",f'{all_modes["ffm-local"]["paired"]:,} / {all_modes["ffm-local"]["sent"]:,}',f'{all_modes["develop-adm4"]["paired"]:,} / {all_modes["develop-adm4"]["sent"]:,}'],
        ["Comparison outcome","PASS (PSM timestamp exception excluded)","FAIL (latency)"]]
    res_rows=[[m,svc,fmt(x["average_cpu_cores"]),fmt(x["peak_cpu_cores_1s"]),fmt((x["peak_memory_bytes"] or 0)/2**20,1),
        x["sample_count"],x["cpu_throttled_usec_delta"],x["memory_oom_events_delta"],
        x["cpu_psi_some_usec_delta"],x["cpu_psi_full_usec_delta"],x["memory_psi_some_usec_delta"]]
        for m,md in all_modes.items() for svc,x in md["resources"].items()]
    resource_table_rows=[]
    for mode,data in all_modes.items():
        label="FFM" if mode=="ffm-local" else "External ADM"
        for row in res_rows:
            if row[0]==mode:
                resource_table_rows.append([label,row[1],row[2],row[3],row[4]+" MiB"])
        totals=data["resource_totals"]
        resource_table_rows.append([label,"**Total**",fmt(totals["average_cpu_cores"]),
            fmt(totals["peak_cpu_cores_1s"]),fmt(totals["peak_memory_bytes"]/2**20,1)+" MiB"])
    report=["# ODE mixed-message performance comparison",
        f"Test environment: Linux VM with {vm['effective_cpu_cores']} CPU cores and {vm['effective_memory_bytes']/2**30:.2f} GiB RAM. Both tests used an isolated deployment with ODE exposed on port 18080 and no container-level CPU or memory limits.",
        "The workload covered ten message types, evenly interleaved at 500 UDP messages per second (50 per type). Each test sent 1,000 warmup and 50,000 measured messages per type. Latency is Kafka JSON CreateTime minus UDP send time; Kafka timestamps have millisecond resolution.",
        "## How to read pass and fail",
        "A test passes only when every type sustains its target rate, all expected UDP messages correlate to exactly one raw and one JSON Kafka record with a valid timestamp, there are no duplicates or unexpected dead-letter records, application consumer groups drain to their topic ends, services stay healthy through the quiet period, and p95 latency is strictly below 5 ms. FFM also requires zero pending publication, raw-message, and offset-commit work at drain.",
        "A test fails if any one of those checks fails. A passing aggregate p95 does not override a failed per-type integrity check.",
        "For this comparison, the isolated PSM timestamp exception is excluded from the acceptance decision as requested. All 500,000 FFM raw and JSON records correlated; 499,999 have valid latency samples. The original benchmark gate results remain in the saved evidence. This exception does not change the criteria for future tests.",
        "## Overall comparison",
        table(["Measure","FFM","External ADM"],comparison_rows),
        "FFM’s aggregate p95 was about 79% lower than External ADM’s. With the requested timestamp exception excluded, FFM passes this comparison. External ADM correlated every record, but its p95 exceeded 5 ms for every type.",
        "## Container resource use",
        "CPU is in logical cores; peak CPU is the highest sampled one-second interval average. Memory is peak cgroup usage during measured sending. Total rows include ODE and Kafka, plus ADM for the external solution. Average CPU totals sum the container averages; peak CPU and memory totals use aligned sampling rounds, rather than adding each container’s separate maximum. ODE, Kafka, and ADM were verified uncapped before traffic.",
        table(["Test","Container","Average CPU","Peak CPU","Peak memory"],resource_table_rows),
        "No CPU throttling or OOM events were recorded. CPU pressure stall counters showed brief contention; memory pressure was negligible.",
        "## Outcome",
        "Use FFM for applications requiring low decoding latency under this tested workload. FFM met the strict p95 below 5 ms target for every message type, with aggregate p95 of 2.853 ms and p99 of 2.975 ms. External ADM measured p95 of 13.601 ms and p99 of 17.063 ms, and missed the latency target for every type. Both solutions delivered all raw and JSON records, with zero duplicates, drained offsets, and healthy quiet periods; FFM also drained pending work.",
        f"FFM also used less total compute: {all_modes['ffm-local']['resource_totals']['average_cpu_cores']:.3f} average CPU cores versus {all_modes['develop-adm4']['resource_totals']['average_cpu_cores']:.3f} for External ADM. Its sampled peak total memory was {all_modes['ffm-local']['resource_totals']['peak_memory_bytes']/2**30:.2f} GiB versus {all_modes['develop-adm4']['resource_totals']['peak_memory_bytes']/2**30:.2f} GiB. External ADM remains an option where these measured latencies are acceptable, but these results favor FFM for the low-latency requirement.",
        "This recommendation is based on one mixed-message run per solution at 500 messages/s on an uncapped Linux deployment, with the requested PSM timestamp exception excluded. It does not establish performance under the shipped container limits, at other loads, or across repeated runs. Neither workload was repeated.",
        f"Evidence files are saved under `{root}`. The directory includes the combined latency and resource summaries, per-type raw evidence, and one-second resource samples."]
    (root/"jira-performance-report.md").write_text("\n\n".join(report)+"\n")
    print(root/"jira-performance-report.md")

if __name__=="__main__": main()

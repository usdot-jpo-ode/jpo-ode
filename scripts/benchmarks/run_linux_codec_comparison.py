#!/usr/bin/env python3
"""Run one 50k/1kHz workload per UDP type and codec, restoring Compose afterward.

Packet corpora must contain 1,000 warmup + 50,000 distinct, valid hex datagrams.
Uses the existing one-second cgroup sampler; no measured workload is retried.
"""
import argparse
import json
import os
from pathlib import Path
import re
import signal
import shutil
import tempfile
import time
import concurrent.futures
from urllib.request import urlopen
from run_linux_ffmlib_latency import Runner, ROOT, scrub

TYPES = {'bsm':46800, 'map':44920, 'spat':44910, 'tim':47900, 'psm':44940,
         'srm':44930, 'ssm':44900, 'sdsm':44950, 'rtcm':44960, 'rsm':44970}
RAW_TOPIC_PARTITIONS = 4
EXTERNAL_CODEC_TOPICS = ('topic.Asn1DecoderInput', 'topic.Asn1DecoderOutput')

class ComparisonRunner(Runner):
    def __init__(self, directory, mode, message_type, override, output_mode=None,
                 count=50000, rate=1000):
        super().__init__(directory, count=count, warmup=1000, rate=rate)
        self.mode, self.message_type = mode, message_type
        self.services = ('ode','kafka','adm') if mode == 'external' else ('ode','kafka')
        self.override_path = override
        self.output_mode = output_mode or mode

    def workload_command(self, csv_path):
        corpus = self.output_dir.parent / (self.message_type + '.packets')
        command = self.compose('run', '--rm', '--no-deps', 'decode-benchmark',
            '--mode', self.mode, '--broker', 'kafka:9094', '--packet-file',
            '/output/' + str(corpus.relative_to(ROOT / 'scripts/tests/output')),
            '--message-type', self.message_type.upper(), '--udp-host', 'ode',
            '--udp-port', str(TYPES[self.message_type]), '--raw-topic',
            'topic.OdeRawEncoded'+self.message_type.upper()+'Json', '--json-topic',
            'topic.Ode'+self.message_type.title()+'Json', '--consumer-group',
            'RawEncoded'+self.message_type.upper()+'JsonRouter', '--metrics-url',
            'http://ode:8080/actuator/prometheus', '--health-url',
            'http://ode:8080/actuator/health', '--count', str(self.count), '--warmup', '1000',
            '--rate', str(self.rate), '--max-p95-ms', '5', '--quiet-period-seconds', '15',
            '--drain-timeout-seconds', '120', '--output',
            '/output/'+str(csv_path.relative_to(ROOT / 'scripts/tests/output')), override=True)
        if self.mode == 'external':
            command += ['--drain-group', 'AsnDecodeLatencyBenchmark:topic.Asn1DecoderInput',
                        '--drain-group', 'Asn1DecodedDataRouter:topic.Asn1DecoderOutput']
        return command


def main():
    parser=argparse.ArgumentParser()
    parser.add_argument('--output-dir',type=Path,required=True)
    parser.add_argument('--resume',action='store_true',help='skip every previously measured workload, including failures')
    parser.add_argument('--external-only',action='store_true')
    parser.add_argument('--adm-processes',type=int,default=1)
    parser.add_argument('--output-label')
    parser.add_argument('--setup-label')
    parser.add_argument('--message-type',choices=tuple(TYPES),action='append')
    parser.add_argument('--count',type=int,default=50000)
    parser.add_argument('--rate',type=int,default=1000)
    args=parser.parse_args()
    if args.external_only and args.adm_processes != 4:
        parser.error('--external-only requires --adm-processes 4 for the requested comparison')
    if args.external_only:
        # Compose resolves ADM_NUMBER_OF_PROCESSES from this host environment.
        os.environ['ADM_NUMBER_OF_PROCESSES']='4'
    root=args.output_dir.resolve();root.mkdir(parents=True,exist_ok=True)
    if args.count <= 0 or args.rate <= 0:
        parser.error('--count and --rate must be positive')
    selected_types=args.message_type or list(TYPES)
    for name in selected_types:
        port=TYPES[name]
        config=(ROOT/'jpo-ode-svcs/src/main/resources/application.yaml').read_text()
        import re
        match=re.search(r'    '+name+r':\n(?:      .*\n)*?      receiver-port: (\d+)',config)
        if match is None or int(match.group(1))!=port:parser.error('receiver port mismatch: '+name)
        if not (root/(name+'.packets')).exists():
            parser.error('missing unique packet corpus: '+name)
    control_dir=root/(args.setup_label or 'setup');control_dir.mkdir(exist_ok=True)
    control=Runner(control_dir,args.count,1000,args.rate)
    control.runtime['comparison_mode']='external4' if args.external_only else 'ffm-and-external'
    topic_evidence={}
    def verify_codec_partitions():
        for topic in EXTERNAL_CODEC_TOPICS:
            completed=control.command('verify-topic-partitions',[
                'docker','exec','usdot-jpo-ode-kafka-1',
                '/opt/bitnami/kafka/bin/kafka-topics.sh','--bootstrap-server','localhost:9092',
                '--describe','--topic',topic])
            match=re.search(r'PartitionCount:\s+(\d+)',completed.stdout)
            count=int(match.group(1)) if match else None
            topic_evidence[topic]=count
            if count != 4:
                raise RuntimeError(f'{topic} has {count} partitions; expected 4')
        control.runtime['external_codec_topic_partitions']=topic_evidence
        control.runtime['ADM_NUMBER_OF_PROCESSES']=4
        control._write_runtime()
    def wait_external_quiet(input_group,timeout=240,quiet_seconds=15):
        specifications=((input_group,'topic.Asn1DecoderInput'),
                        ('Asn1DecodedDataRouter','topic.Asn1DecoderOutput'))
        started=time.monotonic();quiet_started=None;previous=None;observations=[]
        def describe(group):
            return control.command('pipeline-offsets',[
                'docker','exec','usdot-jpo-ode-kafka-1',
                '/opt/bitnami/kafka/bin/kafka-consumer-groups.sh','--bootstrap-server',
                'localhost:9092','--describe','--group',group]).stdout
        with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
            while time.monotonic()-started < timeout:
                futures={group:pool.submit(describe,group) for group,_ in specifications}
                current=[];all_drained=True
                for group,topic in specifications:
                    output=futures[group].result()
                    for line in output.splitlines():
                        fields=line.split()
                        if len(fields)<6 or fields[0]!=group or fields[1]!=topic:
                            continue
                        try: partition,end,lag=int(fields[2]),int(fields[4]),int(fields[5])
                        except ValueError: continue
                        current.append((group,topic,partition,end,lag))
                        all_drained &= lag==0
                current=tuple(sorted(current));observations.append(current)
                if current and current==previous and all_drained:
                    if quiet_started is None:quiet_started=time.monotonic()
                    if time.monotonic()-quiet_started>=quiet_seconds:
                        control.runtime['external_pipeline_preflight']={
                            'groups':{group:{'partitions':sum(row[0]==group for row in current),
                                'lag':sum(row[4] for row in current if row[0]==group)}
                                for group,_ in specifications},
                            'quiet_seconds':quiet_seconds,'observations':len(observations),
                            'final_offsets':[list(row) for row in current]}
                        control._write_runtime();return
                else:quiet_started=None
                previous=current
                time.sleep(1)
        raise TimeoutError('external decoder input/output groups did not remain drained for '
                           f'{quiet_seconds}s: {observations[-1] if observations else "no offsets"}')
    results=[]
    def interrupted(signum, frame):
        raise KeyboardInterrupt('interrupted; restoring original deployment')
    signal.signal(signal.SIGTERM,interrupted)
    signal.signal(signal.SIGINT,interrupted)
    try:
        control.source_identity();control.vm_capacity();control.compose_config()
        control.check_original_stack()
        # ADM is absent on the reference FFM stack; preserve that state exactly.
        adm_ids=control.command('original-adm',control.compose('ps','-aq','adm')).stdout.strip()
        if adm_ids:
            raise RuntimeError('ADM container already exists; preserve its state before using this helper')
        with tempfile.TemporaryDirectory(prefix='ode-codec-comparison-') as temporary:
            control.make_override(temporary)
            uncapping=control.override_path.read_text()
            control.set_uncapped()
            modes=((args.output_label or ('external4' if args.adm_processes == 4 else 'external')),
                   ) if args.external_only else ('ffm','external')
            for mode in modes:
                output_mode=mode if isinstance(mode,str) else mode
                execution_mode='external' if output_mode.startswith('external') else mode
                override=Path(temporary)/(mode+'.yml')
                text=uncapping.replace('  ode:\n','  ode:\n    environment:\n      ODE_ASN1_CODEC_MODE: '+execution_mode+'\n',1)
                if execution_mode=='external':
                    config=Path(temporary)/'adm.properties'
                    content=(ROOT/'asn1_codec/config/adm.properties').read_text()
                    content=content.replace('group.id=AsnDecode','group.id=AsnDecodeLatencyBenchmark')
                    content=content.replace('auto.offset.reset=smallest','auto.offset.reset=latest')
                    content=content.replace('compression.type=zstd','compression.type=none')
                    config.write_text(content+'\nlinger.ms=0\nacks=all\nenable.idempotence=true\n')
                    supervisor_source=(ROOT/'asn1_codec/supervisord.conf').read_text()
                    expected='-b %(ENV_DOCKER_HOST_IP)s:9092'
                    if expected not in supervisor_source:
                        raise RuntimeError('expected ADM supervisor broker setting not found')
                    supervisor_path=Path(temporary)/'supervisord.conf'
                    supervisor_path.write_text(supervisor_source.replace(expected,'-b kafka:9094'))
                    text+='''  adm:
    cpus: !reset null
    mem_limit: !reset null
    memswap_limit: !reset null
    deploy:
      resources:
        limits: !reset null
    environment:
      ACM_NUMBER_OF_PROCESSES: "4"
      ACM_CONFIG_FILE: benchmark-adm.properties
    entrypoint: ["/asn1_codec/run_acm.sh"]
    command: []
    volumes:
      - '''+str(config)+''':/asn1_codec/config/benchmark-adm.properties:ro
      - '''+str(supervisor_path)+''':/etc/supervisord.conf:ro
'''
                override.write_text(text)
                control.override_path=override
                if execution_mode=='external':
                    verify_codec_partitions()
                    if control.command_stream('start-external-decoder',control.compose('up','-d','--no-deps','adm',override=True),timeout=300):
                        raise RuntimeError('external decoder failed to start')
                if mode != 'ffm' and control.command_stream('set-mode-'+mode,control.compose('up','-d','--no-deps','--force-recreate','ode',override=True),timeout=600):
                    raise RuntimeError('ODE failed to start in '+mode)
                control.wait_healthy('ode')
                if execution_mode=='external':
                    time.sleep(10)
                    state=control.inspect_container('adm')
                    if state['status']!='running':raise RuntimeError('external decoder not running')
                    processes=control.command('adm-supervisor-status',[
                        'docker','exec',state['id'],'supervisorctl','status'],log_output=True).stdout
                    running=sum(bool(re.search(r'\bRUNNING\b',line)) for line in processes.splitlines())
                    if running != 4:
                        raise RuntimeError(f'ADM started {running} worker processes; expected 4')
                    health=control.command('ode-readiness',[
                        'docker','exec','usdot-jpo-ode-ode-1','sh','-c',
                        'curl -sS http://localhost:8080/actuator/health'],log_output=False).stdout
                    if '"status":"UP"' not in health.replace(' ',''):
                        raise RuntimeError('ODE actuator health is not UP before workload')
                    wait_external_quiet('AsnDecodeLatencyBenchmark')
                    logs=control.command('adm-readiness',['docker','logs',state['id']],log_output=False).stdout
                    control.log_line(logs)
                    control.runtime['external_adm_workers_running']=running
                    control.runtime['external_adm_consumer_group']='AsnDecodeLatencyBenchmark'
                    control.runtime['external_adm_offset_reset']='latest (benchmark group starts at existing topic end)'
                    control._write_runtime()
                for name in selected_types:
                    directory=root/(output_mode+'-'+name)
                    if (directory/'latency.json').exists():
                        if not args.resume:raise RuntimeError('existing workload evidence; refusing to rerun '+mode+'/'+name)
                        results.append({'mode':mode,'type':name,'status':'preserved existing result'})
                        print(f'{mode}/{name}: preserving existing measured workload',flush=True)
                        continue
                    directory.mkdir(exist_ok=True)
                    runner=ComparisonRunner(directory,execution_mode,name,override,output_mode,
                                            args.count,args.rate)
                    try:
                        runner.runtime['source']=control.runtime['source']
                        runner.runtime['vm_capacity']=control.runtime['vm_capacity']
                        runner.runtime['comparison_settings']={'mode':execution_mode,'external_processes':4 if execution_mode=='external' else 0,
                            'target_rate_per_second':args.rate,'measured_count':args.count,'warmup':1000,
                            'ADM_NUMBER_OF_PROCESSES':4 if execution_mode=='external' else None,
                            'ffm_listeners_per_type':4 if execution_mode=='ffm' else None,
                            'external_codec_topic_partitions':topic_evidence.copy() if execution_mode=='external' else None,
                            'linger_ms':0,'compression':'none','acks':'all','idempotence':True}
                        for service in runner.services:
                            state=runner.inspect_container(service)
                            verified=runner.uncapped_state(service,state)
                            if not verified['uncapped']:raise RuntimeError('uncapping failed: '+service)
                        runner._write_runtime()
                        print(f'{mode}/{name}: uncapped verified; starting one 50,000-message workload',flush=True)
                        code=runner.collect_workload(directory/'latency.csv',directory/'resource-samples.csv')
                        results.append({'mode':output_mode,'type':name,'exit_code':code})
                        print(f'{output_mode}/{name}: finished exit={code}',flush=True)
                    finally:
                        runner.close()
                    (root/'comparison-status.json').write_text(json.dumps(results,indent=2))
    except BaseException as error:
        control.runtime['setup_or_execution_failure']=scrub(f'{type(error).__name__}: {error}')
        print(control.runtime['setup_or_execution_failure'],flush=True)
        raise
    finally:
        # Always remove the temporary external decoder and restore the FFM stack and caps.
        try:
            control.command('remove-temporary-adm',control.compose('rm','--stop','--force','adm'))
        finally:
            control.override_path=None
            control.restore();control._write_runtime();control.close()
        print('Original deployment restoration recorded in setup/runtime-evidence.json',flush=True)

if __name__=='__main__':main()

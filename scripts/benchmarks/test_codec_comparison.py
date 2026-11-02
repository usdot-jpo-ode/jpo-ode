"""Checks for the multi-type comparison port map and measured resource window."""
import ast
import csv
import datetime as dt
from pathlib import Path
import tempfile
import unittest
from run_linux_codec_comparison import TYPES
from summarize_codec_comparison import measured_resources

class CodecComparisonTest(unittest.TestCase):
    def test_ports_match_repository_udp_fixtures(self):
        root=Path(__file__).resolve().parents[1] / "tests"
        for name,port in TYPES.items():
            tree=ast.parse((root/f'udpsender_{name}.py').read_text())
            assignment=next(node for node in tree.body if isinstance(node,ast.Assign)
                and any(isinstance(target,ast.Name) and target.id=='UDP_PORT' for target in node.targets))
            values=[node.value for node in ast.walk(assignment.value)
                    if isinstance(node,ast.Constant) and isinstance(node.value,int)]
            self.assertIn(port,values,name)

    def test_resource_window_excludes_warmup_and_drain(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory)
            (root/'latency.csv').write_text('udp_send_time_ms\n100000\n104000\n')
            fields=['service','sample_error','actual_cadence_seconds','timestamp_utc',
                    'interval_cpu_percent','memory_current_bytes']
            with (root/'resource-samples.csv').open('w') as stream:
                writer=csv.DictWriter(stream,fieldnames=fields);writer.writeheader()
                for second,cpu,memory in [(100,900,999),(101,100,100),(102,200,200),(105,800,888)]:
                    writer.writerow(dict(zip(fields,['ode','',1,
                        dt.datetime.fromtimestamp(second,dt.timezone.utc).isoformat(),cpu,memory])))
            result=measured_resources(root)['ode']
            self.assertEqual(2,result['sample_count'])
            self.assertEqual(1.5,result['average_cpu_cores'])
            self.assertEqual(2,result['peak_cpu_cores'])
            self.assertEqual(200,result['peak_memory_bytes'])

if __name__=='__main__':unittest.main()

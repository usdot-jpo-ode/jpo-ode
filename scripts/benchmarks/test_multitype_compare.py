"""Regression checks for the mixed FFM/ADM comparison runner."""
import unittest

from run_linux_multitype_compare import running_adm_processes


class MultitypeCompareTest(unittest.TestCase):
    def test_reads_supervisor_group_prefix_and_requires_running_state(self):
        output = """asn1codec:asn1codec_0            RUNNING   pid 19, uptime 0:01:07
asn1codec:asn1codec_1            RUNNING   pid 20, uptime 0:01:07
asn1codec:asn1codec_2            RUNNING   pid 21, uptime 0:01:07
asn1codec:asn1codec_3            RUNNING   pid 11, uptime 0:01:08
asn1codec:asn1codec_4            STOPPED   Not started
"""
        self.assertEqual(["0", "1", "2", "3"], running_adm_processes(output))


if __name__ == "__main__":
    unittest.main()

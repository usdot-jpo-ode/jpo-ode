
"""Checks the comparison runner's UDP port map against the repository sender fixtures."""
import ast
from pathlib import Path
import unittest
from run_linux_codec_comparison import TYPES

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

if __name__=='__main__':unittest.main()

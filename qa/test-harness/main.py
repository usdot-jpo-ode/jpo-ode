import os
from argparse import ArgumentParser
from testharness import TestHarness

# main function using old functionality
def main():
    parser = ArgumentParser()
    parser.add_argument("--config-file", dest="config_file_path", help="Path to ini configuration file used for testing.", metavar="CONFIGFILEPATH", required=False, default="full-test-sample.ini")
    args = parser.parse_args()
    test_harness = TestHarness(args.config_file_path)
    test_harness.run()

if __name__ == '__main__':
    main()

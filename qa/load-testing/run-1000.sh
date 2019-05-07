#!/bin/bash
./create-testfiles.sh ../../data/bsmLogDuringEvent.gz testfiles 1000
python main.py --dir testfiles
rm -rf testfiles
echo "==== LOAD TEST COMPLETE! ===="

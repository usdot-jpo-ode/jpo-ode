# ODE Regression Test Harness

## Overview

This folder contains the automated regression test harness application built for the ODE. The test harness sends a file to the ODE, listens for messages to be published to Kafka, then validates the output.

## Usage

### Option 1: Using the Full Test Script (Recommended)

You may perform a complete test of all of the ODE input file types by running the `full-test.sh` script.

1. Set the [DOCKER_HOST_IP](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-management#obtaining-docker_host_ip) environment variable.
2. Run `./full-test.sh`


### Option 2: Using Custom Test Cases

Otherwise you may run individual, customized

Run using Python at the command line.

`python test-harness.py --config-file <CONFIGFILEPATH> --data-file <DATAFILEPATH> --kafka-topic <KAFKATOPIC> --output-file <LOGFILEPATH>`

```
usage: test-harness.py [-h] --data-file DATAFILEPATH --config-file
                       CONFIGFILEPATH --kafka-topic KAFKATOPIC
                       [--output-file LOGFILEPATH]

optional arguments:
  -h, --help            show this help message and exit
  --data-file DATAFILEPATH
                        Path to log data file that will be sent to the ODE for
                        validation.
  --config-file CONFIGFILEPATH
                        Path to ini configuration file used for testing.
  --kafka-topic KAFKATOPIC
                        Kafka topic to which to the test harness should listen
                        for output messages.
  --output-file LOGFILEPATH
                        Output file to which detailed validation results will
                        be printed.
```

## Test Case Configuration

See the [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) for more information on how to create your own test case configurations.

# ODE Regression Test Harness

## Overview

This folder contains the automated regression test harness application built for the ODE. The test harness sends a file to the ODE, listens for messages to be published to Kafka, then validates the output.

## Usage

**Important Note**: Before you begin either of the options below, you must set the [DOCKER_HOST_IP](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-management#obtaining-docker_host_ip) environment variable.

### Option 1:  (Recommended) Using the pre-configured test script

You may perform a complete test of all of the ODE input file types by running the pre-configured full-test script.

Run `./full-test.sh`

### Option 2: Using custom test cases

See the [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) for more information on how to create your own test case configurations.

You may run custom test cases by creating your own configuration file by setting the following command-line arguments:

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

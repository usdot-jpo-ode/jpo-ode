# ODE Regression Test Harness

## Overview

This folder contains the automated regression test harness application built for the ODE. The test harness uploads files to the ODE, listens for messages to be published to Kafka, then validates the output.

## Requirements

- Python 3.7

## Installation

#### Option 1: (Recommended) Using python3 with virtualenv

1. [Install python3](https://realpython.com/installing-python/)
2. [Install virtualenv](https://virtualenv.pypa.io/en/stable/installation/)
3. Create a virtualenv repository:
```
virtualenv -p python3 testharness
```
4. Activate virtualenv:
```
source ./testharness/bin/activate
```
5. Install the required python modules using pip:
```
pip install -r requirements.txt
```
5. _Run the test script using the **Usage** instructions below._
6. Deactivate virtualenv
```
deactivate
```

#### Option 2: Using system python

1. Install the required python modules using `pip install -r requirements.txt`.
2.  _Run the test script using the **Usage** instructions below._

## Usage

**Important Note**: Before you begin either of the options below, you must set the [DOCKER_HOST_IP](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-management#obtaining-docker_host_ip) environment variable.

### Option 1:  (Recommended) Using the default configuration

You may perform a complete test of all of the ODE input file types by running `python main.py`.

### Option 2: Using custom test cases

See the [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) for more information on how to create your own test case configurations.
[odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) is a submodule of `test-harness`. Even though, it can be
pull from GitHub, the `test-harness` will not be able to use the included config file.
it is recommended to update the submodule to allow the `test-harness` to use the latest config file, not to mention
the latest [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) code if local changes are present.

You may run custom test cases by creating your own configuration file by setting the following command-line arguments:

`python test-harness.py --config-file <CONFIGFILEPATH>`

```
usage: main.py [-h] [--config-file CONFIGFILEPATH]

optional arguments:
  -h, --help            show this help message and exit
  --config-file CONFIGFILEPATH
                        [Optional] Path to ini configuration file used for testing.
						If not specified, the test-harness will use the default config file full-test-sample.ini.
```

## Configuration

Test case configuration is done using a .ini file. Provided in this repository is the default configuration in full-test-sample.ini.

The `[_meta]` section contains one property: `PerformBundleIdCheck`. Set this to True if you want to perform bundle ID validations or False if you do not.

```
[_meta]
PerformBundleIdCheck = True
```

All other sections define test cases. You may provide as few or as many as you wish.

| Property     | Required | Description                                                                                            |
|--------------|----------|--------------------------------------------------------------------------------------------------------|
| DataFile     | Yes      | Log file or REST JSON file containing data to be uploaded to the ODE                                   |
| UploadUrl    | Yes      | REST endpoint to which data files are sent                                                             |
| UploadFormat | Yes      | FILE or BODY to specify if the data should be sent as an attachment or in the request body             |
| KafkaTopics  | Yes      | List of topics, separated by commas, to which the test harness should expect messages out from the ODE |
| BundleStream | Yes      | Used in conjunction with BundleID validation for grouping                                              |
| OutputFile   | No       | (Optional) Log file path to which detailed test results will be logged                                 |
| ConfigFile   | No       | (Optional) Path to ODE validator library custom configuration file                                     |

## Release History

### 2019-05-21
- Added bundleId validation
- Added formatted printing to make output log files easier to read
  - Added ResultPrinter.py
- Added support for multiple tests (and multiple files)
- Changed configuration from command-line arguments to .ini config file format
  - Changed example run script into example config file
  - Coalesced command-line arguments into one: a config file
  - Added _meta configuration section with PerformBundleIdCheck property
  - Added ConfigFile property for custom validator configuration
  - Added UploadFormat configuration property
  - Added BundleStream configuration property
- Removed necessary command-line arguments, now can run simply by using `python main.py`
- Refactored test harness into TestHarness and TestHarness iteration classes
- Updated ode-output-validator-library submodule
- Cleanup
- Documentation

### 2019-05-09
- Integrated [odevalidator v0.0.4](https://github.com/usdot-jpo-ode/ode-output-validator-library/releases/tag/odevalidator-0.0.4)

### 2019-04-15
- Added ode-upload-url command line argument so the test harness no longer
relies on DOCKER_HOST_IP environment variable to reach the ODE.
- Renamed `full-test.sh` to `full-test-sample.sh`. The users must create their own `full-test.sh` according to the provided sample script.

### 2019-04-09
- Integrated [odevalidator-0.0.3](https://github.com/usdot-jpo-ode/ode-output-validator-library/releases/tag/odevalidator-0.0.3)

### 2019-04-05
- Integrated [odevalidator-0.0.2](https://github.com/usdot-jpo-ode/ode-output-validator-library/releases/tag/odevalidator-0.0.2)

### 2019-04-01
- Integrated initial release of the validator library [odevalidator-v0.0.1](https://github.com/usdot-jpo-ode/ode-output-validator-library/releases/tag/odevalidator-0.0.1)

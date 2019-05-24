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

A typical test run takes about 2 minutes to complete.

### Option 2: Using custom test cases

See the [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) for more information on how to create your own test case configurations.
[odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) is a submodule of `test-harness`. Even though, it can be
pull from GitHub, the `test-harness` will not be able to use the included config file.
it is recommended to update the submodule to allow the `test-harness` to use the latest config file, not to mention
the latest [odevalidator library](https://github.com/usdot-jpo-ode/ode-output-validator-library) code if local changes are present.

You may run custom test cases by creating your own configuration file by setting the following command-line arguments:

`python main.py --config-file <CONFIGFILEPATH>`

```
usage: main.py [-h] [--config-file CONFIGFILEPATH]

optional arguments:
  -h, --help            show this help message and exit
  --config-file CONFIGFILEPATH
                        [Optional] Path to ini configuration file used for testing.
						If not specified, the test-harness will use the default config file full-test-sample.ini.
```

## Configuration

Test case configuration is done using a .ini file. Provided in this repository is the default configuration in full-test-sample.ini. The configuration file must start with a `[_meta]` section and then as many or as few test case sections as you wish.

**The _meta section**

This section is used to define high-level settings.

| Property             | Required | Description                                                                                                         |
| -------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| PerformBundleIdCheck | Yes      | Used to specify if the test harness should run bundleId validations across test cases                               |
| KafkaTimeout         | No       | (Optional) Used to override the default timeout, default value is 15000 miliseconds; set/increase this value if you are experiencing timeout exceptions |

Example:

```
[_meta]
PerformBundleIdCheck = True
KafkaTimeout = 7500
```

**Test case sections**

All other sections define test cases.

| Property         | Required | Description                                                                                                                                                                                        |
| ---------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [FilePath]       | Yes      | The name of your test case section (in [brackets]) defines the log file or REST JSON file path containing data to be uploaded to the ODE.                                                          |
| UploadUrl        | Yes      | REST endpoint to which data files are sent; note that DOCKER_HOST_IP is automatically interpolated                                                                                                 |
| UploadFormat     | Yes      | FILE or BODY to specify if the data should be sent as an attachment or in the request body; TIM REST API uploads must be set as BODY, log file uploads must be set as File                         |
| KafkaTopics      | Yes      | List of topics, separated by commas, to which the test harness should expect messages out from the ODE                                                                                             |
| OutputFile       | No       | (Optional) Log file path to which detailed test results will be logged                                                                                                                             |
| ConfigFile       | No       | (Optional) Path to ODE validator library custom configuration file                                                                                                                                 |
| ExpectedMessages | No       | (Optional) Used if you know how many messages to expect in the data file; if not used the test-harness will wait KafkaTimeout amount of time to receive all messages; useful for skipping excess timeout time|

_**Note:** When using the `ExpectedMessages` property, the test harness will stop listening for messages once that count has been reached. If the file contains more messages than that, they will be ignored._

Example:

```
[../../data/bsmLogDuringEvent.gz]
UploadUrl = http://%(DOCKER_HOST_IP)s:8080/upload/bsmlog
UploadFormat = FILE
KafkaTopics = topic.OdeBsmJson
OutputFile = bsmLogDuringEvent.log
ConfigFile = ./path/to/my/customconfig.ini
ExpectedMessages = 400
```

## Troubleshooting

- `testharness.TestHarnessException: [ERROR] Aborting test routine! Received no messages from the Kafka consumer.`
  1. Try increasing the Kafka consumer timeout using the `KafkaTimeout` property in the _meta section of the configuration file
  2. Verify that the ODE is publishing messages to the Kafka topics specified in the configuration file

## Release History

### 2019-05-21
- Added bundleId validation
- Added formatted printing to make output log files easier to read
  - Added resultprinter.py
- Added support for multiple tests (and multiple files)
- Changed configuration from command-line arguments to .ini config file format
  - Changed example run script into example config file
  - Replaced command-line arguments into config file
  - Added _meta configuration section with PerformBundleIdCheck property
  - Added ConfigFile property for custom validator configuration
  - Added UploadFormat configuration property
  - Added ExpectedMessages configuration property
- Removed required command-line arguments, now can run simply by using `python main.py`
- Refactored test harness into TestHarness and TestHarnessIteration classes
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

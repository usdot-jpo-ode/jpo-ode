# ODE Regression Test Harness

## Overview

This folder contains the automated regression test harness application built for the ODE. The test harness sends a file to the ODE, listens for messages to be published to Kafka, then validates the output.

## Usage

```
python test-harness.py -f <PATH_TO_CONFIG_FILE>
```

There are several _.ini_ configuration files provided for testing. For example to test the ODE's _bsmLogDuringEvent_ functionality, pass the configuration file as a runtime argument:

```
python test-harness.py -f bsmLogDuringEvent.ini
```

## Configuration

Configuration is managed through .ini files passed as parameters during runtime.

#### \_settings

At a high level, the `_settings` section outlines the following properties:

```
KafkaTopic = topic.OdeBsmJson                   # Kafka topic to listen to for output messages
InputFilePath = ../data/bsmLogDuringEvent.gz    # Source data file for input
OutputFilePath = bsmLogDuringEvent_results.log  # Output file path for validation results
ExpectedMessages = 222                          # Number of messages in the source data file
```

Then each field to be validated should be created.

- `Path`
  - Required: Yes
  - Summary: Identifies the field location within the message
  - Value: JSON path to the field, separated using periods
- `Type`
  - Required: Yes
  - Summary: Identifies the type of the field
  - Value: One of `enum`, `decimal`, `string`, or `timestamp`
    - enum
      - Specifies that the field must be one of a certain set of values
    - decimal
      - Specifies that the field is a number
    - string
      - Specifies that the field is a string
    - timestamp
      - Values of this type will be validated by testing for parsability
- `EqualsValue`
  - Required: Optional
  - Summary: Sets the expected value of this field, will fail if the value does not match this
  - Value: Expected value: `EqualsValue = us.dot.its.jpo.ode.model.OdeBsmPayload`
- `Values`
  - Required: Optional
  - Summary: Used with enum types to specify the list of possible values that this field must be
  - Value: JSON array with values in quotes: `Values = ["OptionA", "OptionB", ]`
- `LowerLimit`
  - Required: Optional
  - Summary: Used with decimal types to specify the lowest acceptable value for this field
  - Value: decimal number: `LowerLimit = 2`
- `UpperLimit`
  - Required: Optional
  - Summary: Used with decimal types to specify the highest acceptable value for this field
  - Value: decimal number: `UpperLimit = 150.43`

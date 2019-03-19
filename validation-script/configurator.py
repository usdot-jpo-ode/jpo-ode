import configparser
import dateutil.parser
import json
from decimal import Decimal

TYPE_DECIMAL = 'decimal'
TYPE_ENUM = 'enum'
TYPE_TIMESTAMP = 'timestamp'
TYPE_STRING = 'string'


class ValidationResult:
    def __init__(self, valid, error):
        self.valid = valid
        self.error = error

class Field:
    def __init__(self, field):
        # extract required settings
        self.path = field.get('Path')
        if self.path is None:
            raise ValueError("Missing required configuration property 'Path' for field '%s'" % field)
        self.type = field.get('Type')
        if self.type is None:
            raise ValueError("Missing required configuration property 'Type' for field '%s'" % field)

        # extract constraints
        upper_limit = field.get('UpperLimit')
        if upper_limit is not None:
            self.upper_limit = Decimal(upper_limit)
        lower_limit = field.get('LowerLimit')
        if lower_limit is not None:
            self.lower_limit = Decimal(lower_limit)
        values = field.get('Values')
        if values is not None:
            self.values = json.loads(values)
        increment = field.get('Increment')
        if increment is not None:
            self.increment = int(increment)
        equals_value = field.get('EqualsValue')
        if equals_value is not None:
            self.equals_value = str(equals_value)

    def _get_field_value(self, data):
        try:
            path_keys = self.path.split(".")
            value = data
            for key in path_keys:
                value = value.get(key)
            return value
        except AttributeError as e:
            print("[ERROR] Could not find field with path '%s' in message: '%s'" % (self.path, data))
            raise SystemExit

    def validate(self, data):
        field_value = self._get_field_value(data)
        if field_value is None:
            # field is missing
            return ValidationResult(False, "Field '%s' missing" % self.path)
        if field_value == "":
            # field is empty
            return ValidationResult(False, "Field '%s' empty" % self.path)
        if hasattr(self, 'upper_limit') and Decimal(field_value) > self.upper_limit:
            # field is greater than upper limit
            return ValidationResult(False, "Field '%s' value '%d' is greater than upper limit '%d'" % (self.path, Decimal(field_value), self.upper_limit))
        if hasattr(self, 'lower_limit') and Decimal(field_value) < self.lower_limit:
            # field is less than lower limit
            return ValidationResult(False, "Field '%s' value '%d' is less than lower limit '%d'" % (self.path, Decimal(field_value), self.lower_limit))
        if hasattr(self, 'values') and str(field_value) not in self.values:
            # field not in known enum list
            return ValidationResult(False, "Field '%s' value '%s' not in list of known values: [%s]" % (self.path, str(field_value), ', '.join(map(str, self.values))))
        if hasattr(self, 'equals_value') and str(field_value) != str(self.equals_value):
            return ValidationResult(False, "Field '%s' value '%s' did not equal expected value '%s'" % (self.path, field_value, self.equals_value))
        if hasattr(self, 'increment'):
            if not hasattr(self, 'previous_value'):
                self.previous_value = field_value
            else:
                if field_value != (self.previous_value + self.increment):
                    result = ValidationResult(False, "Field '%s' successor value '%d' did not match expected value '%d', increment '%d'" % (self.path, field_value, self.previous_value+self.increment, self.increment))
                    self.previous_value = field_value
                    return result
        if self.type == TYPE_TIMESTAMP:
            try:
                dateutil.parser.parse(field_value)
            except Exception as e:
                return ValidationResult(False, "Field '%s' value could not be parsed as a timestamp, error: %s" % (self.path, str(e)))
        return ValidationResult(True, "")

class Configurator:
    def __init__(self, filepath):
        config = configparser.ConfigParser()
        config.read(filepath)
        self.field_list = []
        for key in config.sections():
            if key == "_settings":
                self.kafka_topic = config[key]['KafkaTopic']
                self.input_file_path = config[key]['InputFilePath']
                self.expected_messages = int(config[key]['ExpectedMessages'])
            else:
                self.field_list.append(Field(config[key]))
    def validate(self, data):
        for field in self.field_list:
            result = field.validate(data)
            if not result.valid:
                return result.error
        return ""

def main():
    test_field = {"Path":"a.b.c", "Type":"decimal", "EqualsValue":5}
    field = Field(test_field)
    print("Validation result: %s" % field.validate(json.loads('{"a":{"b":{"c":5}}}')).error)

if __name__ == '__main__':
    main()

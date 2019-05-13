import json
import yaml

class ValidationResultPrinter:
    def __init__(self):
        pass

    def print(self, result, verbose):
        error_list = []
        for validation in result['Validations']:
            if not validation['Valid']:
                error_list.append("Field: %s, Details: %s" % (validation['Field']))
        printable_object = {
            "Record": result['RecordID'],
            "SerialID": json.dumps(result['Record']['metadata']['serialId']),
            "Errors": error_list,
        }
        if verbose:
            if len(printable_object['Errors']) > 0:
                print(yaml.dump(printable_object))

    def print_list(self, result_object):
        for result in result_object['Results']:
            self.print(result, True)

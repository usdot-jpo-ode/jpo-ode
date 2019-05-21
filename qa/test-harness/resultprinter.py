import json
import yaml

class ValidationResultPrinter:
    def __init__(self):
        pass

    def _create_summary_result(self, result):
        error_list = []
        for validation in result.field_validations:
            if not validation.valid:
                error_list.append("Field: %s, Details: %s" % (validation.field_path, validation.details))
        printable_object = {
            "SerialID": result.serial_id,
            "Errors": error_list,
        }
        return printable_object

    def print_list(self, result_object, errors_only, logger):
        results_list = []
        for result in result_object:
            result_summary = self._create_summary_result(result)
            if not errors_only or len(result_summary['Errors']) > 0:
                results_list.append(result_summary)
        logger.info(yaml.dump(results_list))

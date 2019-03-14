from decimal import Decimal
import dateutil.parser
import os

##################################
### Expected Fields
##################################
# bsmSource
# (not included) encodings
#   (not included) elementName
#   (not included) elementType
#   (not included) encodingRule
# logFileName
# odeReceivedAt
# payloadType
# receivedMessageDetails
#   locationData
#     latitude
#     longitude
#     elevation
#     speed
#     heading
#   rxSource
# recordGeneratedAt
# recordGeneratedBy
# recordType
# (not included) request
# sanitized
# schemaVersion
# securityResultCode
# serialId
#   streamId
#   bundleSize
#   bundleId
#   recordId
#   serialNumber
# (not included) validSignature
##################################

# Value constraints
LATITUDE_UPPER_LIMIT = Decimal(90.0)
LATITUDE_LOWER_LIMIT = Decimal(-90.0)
LONGITUDE_UPPER_LIMIT = Decimal(180.0)
LONGITUDE_LOWER_LIMIT = Decimal(-180.0)
ELEVATION_UPPER_LIMIT = Decimal(6143.9)
ELEVATION_LOWER_LIMIT = Decimal(-409.6)
SPEED_UPPER_LIMIT = Decimal(163.82)
SPEED_LOWER_LIMIT = Decimal(0)
HEADING_UPPER_LIMIT = Decimal(359.9875)
HEADING_LOWER_LIMIT = Decimal(0)
BSM_SOURCE_LIST = ['RV', 'EV', 'unknown']
# BSM_SOURCE_LIST = ['bob','alice']
RX_SOURCE_LIST = ['RSU', 'SAT', 'RV', 'SNMP', 'NA', 'unknown']
RECORD_GENERATED_BY_LIST = ['TMC', 'OBU', 'RSU', 'TMC_VIA_SAT', 'TMC_VIA_SNMP']
RECORD_TYPE_LIST = ['bsmLogDuringEvent', 'rxMsg', 'dnMsg', 'bsmTx', 'driverAlert', 'unsupported']
SANITIZED_LIST = [True, False]
SCHEMA_VERSION = 6
SECURITY_RESULT_CODE_LIST = ['success', 'unknown', 'inconsistentInputParameters', 'spduParsingInvalidInput', 'spduParsingUnsupportedCriticalInformationField', 'spduParsingCertificateNotFound', 'spduParsingGenerationTimeNotAvailable', 'spduParsingGenerationLocationNotAvailable', 'spduCertificateChainNotEnoughInformationToConstructChain', 'spduCertificateChainChainEndedAtUntrustedRoot', 'spduCertificateChainChainWasTooLongForImplementation', 'spduCertificateChainCertificateRevoked', 'spduCertificateChainOverdueCRL', 'spduCertificateChainInconsistentExpiryTimes', 'spduCertificateChainInconsistentStartTimes', 'spduCertificateChainInconsistentChainPermissions', 'spduCryptoVerificationFailure', 'spduConsistencyFutureCertificateAtGenerationTime', 'spduConsistencyExpiredCertificateAtGenerationTime', 'spduConsistencyExpiryDateTooEarly', 'spduConsistencyExpiryDateTooLate', 'spduConsistencyGenerationLocationOutsideValidityRegion', 'spduConsistencyNoGenerationLocation', 'spduConsistencyUnauthorizedPSID', 'spduInternalConsistencyExpiryTimeBeforeGenerationTime', 'spduInternalConsistencyextDataHashDoesntMatch', 'spduInternalConsistencynoExtDataHashProvided', 'spduInternalConsistencynoExtDataHashPresent', 'spduLocalConsistencyPSIDsDontMatch', 'spduLocalConsistencyChainWasTooLongForSDEE', 'spduRelevanceGenerationTimeTooFarInPast', 'spduRelevanceGenerationTimeTooFarInFuture', 'spduRelevanceExpiryTimeInPast', 'spduRelevanceGenerationLocationTooDistant', 'spduRelevanceReplayedSpdu', 'spduCertificateExpired']
BUNDLE_SIZE_UPPER_LIMIT = 2147483648
BUNDLE_SIZE_LOWER_LIMIT = 1
BUNDLE_ID_UPPER_LIMIT = 9223372036854775807
BUNDLE_ID_LOWER_LIMIT = 0
RECORD_ID_UPPER_LIMIT = 2147483647
RECORD_ID_LOWER_LIMIT = 0
SERIAL_NUMBER_UPPER_LIMIT = 9223372036854775807
SERIAL_NUMBER_LOWER_LIMIT = 0

# validates all fields of the metadata
def validate_metadata(metadata, errored):
    validate_field(errored, 'payloadType', metadata.get('payloadType'))
    if metadata.get('payloadType') == "us.dot.its.jpo.ode.model.OdeBsmPayload":
        validate_field(errored, 'bsmSource', metadata.get('bsmSource'), enum_list=BSM_SOURCE_LIST)
    validate_field(errored, 'logFileName', metadata.get('logFileName'))
    validate_field(errored, 'odeReceivedAt', metadata.get('odeReceivedAt'))
    validate_field(errored, 'receivedMessageDetails', metadata.get('receivedMessageDetails'))
    validate_field(errored, 'locationData', metadata['receivedMessageDetails'].get('locationData'))
    validate_field(errored, 'latitude', metadata['receivedMessageDetails']['locationData'].get('latitude'), upper_limit=LATITUDE_UPPER_LIMIT, lower_limit=LATITUDE_LOWER_LIMIT)
    validate_field(errored, 'longitude', metadata['receivedMessageDetails']['locationData'].get('longitude'), upper_limit=LONGITUDE_UPPER_LIMIT, lower_limit=LONGITUDE_LOWER_LIMIT)
    validate_field(errored, 'elevation', metadata['receivedMessageDetails']['locationData'].get('elevation'), upper_limit=ELEVATION_UPPER_LIMIT, lower_limit=ELEVATION_LOWER_LIMIT)
    validate_field(errored, 'speed', metadata['receivedMessageDetails']['locationData'].get('speed'), upper_limit=SPEED_UPPER_LIMIT, lower_limit=SPEED_LOWER_LIMIT)
    validate_field(errored, 'heading', metadata['receivedMessageDetails']['locationData'].get('heading'), upper_limit=HEADING_UPPER_LIMIT, lower_limit=HEADING_LOWER_LIMIT)
    validate_field(errored, 'rxSource', metadata['receivedMessageDetails'].get('rxSource'), enum_list=RX_SOURCE_LIST)
    validate_field(errored, 'recordGeneratedAt', metadata.get('recordGeneratedAt'), timestamp=True)
    validate_field(errored, 'recordGeneratedBy', metadata.get('recordGeneratedBy'), enum_list=RECORD_GENERATED_BY_LIST)
    validate_field(errored, 'recordType', metadata.get('recordType'), enum_list=RECORD_TYPE_LIST)
    validate_field(errored, 'sanitized', metadata.get('sanitized'), enum_list=SANITIZED_LIST)
    validate_field(errored, 'schemaVersion', metadata.get('schemaVersion'), equals_value=SCHEMA_VERSION)
    validate_field(errored, 'securityResultCode', metadata.get('securityResultCode'), enum_list=SECURITY_RESULT_CODE_LIST)
    validate_field(errored, 'serialId', metadata.get('serialId'))
    validate_field(errored, 'streamId', metadata['serialId'].get('streamId'))
    validate_field(errored, 'bundleSize', metadata['serialId'].get('bundleSize'), upper_limit=BUNDLE_SIZE_UPPER_LIMIT, lower_limit=BUNDLE_SIZE_LOWER_LIMIT)
    validate_field(errored, 'bundleId', metadata['serialId'].get('bundleId'), upper_limit=BUNDLE_ID_UPPER_LIMIT, lower_limit=BUNDLE_ID_LOWER_LIMIT)
    validate_field(errored, 'recordId', metadata['serialId'].get('recordId'), upper_limit=RECORD_ID_UPPER_LIMIT, lower_limit=RECORD_ID_LOWER_LIMIT)
    validate_field(errored, 'serialNumber', metadata['serialId'].get('serialNumber'), upper_limit=SERIAL_NUMBER_UPPER_LIMIT, lower_limit=SERIAL_NUMBER_LOWER_LIMIT)

# all-in-one validation checker
def validate_field(errored, field_name, field_value, upper_limit=None, lower_limit=None, enum_list=None, equals_value=None, timestamp=None):
    if field_value is None:
        error(errored, field_name, "field is missing")
    if field_value == "":
        error(errored, field_name, "field is empty")
    if upper_limit is not None and Decimal(field_value) > upper_limit:
        error(errored, field_name, "field value %s is greater than upper limit: %s" % (str(field_value), str(upper_limit)))
    if lower_limit is not None and Decimal(field_value) < lower_limit:
        error(errored, field_name, "field value %s is less than lower limit: %s" % (str(field_value), str(lower_limit)))
    if enum_list is not None and field_value not in enum_list:
        error(errored, field_name, "field value %s not in list of known values: [%s]" % (str(field_value), ', '.join(map(str, enum_list))))
    if equals_value is not None and Decimal(field_value) != equals_value:
        error(errored, field_name, "field value %s does not match expected value: %s" % (str(field_value), str(equals_value)))
    if timestamp is not None:
        validate_timestamp(errored, field_name, field_value)

# validates timestamp by parsability
def validate_timestamp(errored, field_name, string_time):
    try:
        dateutil.parser.parse(string_time)
    except Exception as e:
        error(errored, field_name, str(e))

# prints error and exits program
def error(errored, field_name, err_msg):
    print("======")
    print("[ERROR] Validation Failure!")
    print("[ERROR] Field '%s' failed validation with error: %s" % (field_name, err_msg))
    print("======")
    errored.put(True)
    raise SystemExit

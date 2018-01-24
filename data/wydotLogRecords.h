/*
 * Copyright (c) 2017 Lear Corporation. All rights reserved.
 * Proprietary and Confidential Material.
 *  
 */ 
#ifndef _LOG_CONFIGURATIONS_H_
#define _LOG_CONFIGURATIONS_H_

#define MAX_ISO_TIME_LEN 23
#define MAX_STRING_LEN 255
#define MAX_PAYLOAD_SIZE 2302 //as per 1609.3 std

/* securityResultCode contains below result codes */
typedef enum _securityResultCode {             /* from dot3 */
    success = 0,
    inconsistentInputParameters = 2,
    spduParsingInvalidInput = 3,
    spduParsingUnsupportedCriticalInformationField = 4,
    spduParsingCertificateNotFound = 5,
    spduParsingGenerationTimeNotAvailable = 6,
    spduParsingGenerationLocationNotAvailable = 7,
    spduCertificateChainNotEnoughInformationToConstructChain = 8,
    spduCertificateChainChainEndedAtUntrustedRoot = 9,
    spduCertificateChainChainWasTooLongForImplementation = 10,
    spduCertificateChainCertificateRevoked = 11,
    spduCertificateChainOverdueCRL = 12,
    spduCertificateChainInconsistentExpiryTimes = 13,
    spduCertificateChainInconsistentStartTimes = 14,
    spduCertificateChainInconsistentChainPermissions = 15,
    spduCryptoVerificationFailure = 16,
    spduConsistencyFutureCertificateAtGenerationTime = 17,
    spduConsistencyExpiredCertificateAtGenerationTime = 18,
    spduConsistencyExpiryDateTooEarly = 19,
    spduConsistencyExpiryDateTooLate = 20,
    spduConsistencyGenerationLocationOutsideValidityRegion = 21,
    spduConsistencyNoGenerationLocation = 22,
    spduConsistencyUnauthorizedPSID = 23,
    spduInternalConsistencyExpiryTimeBeforeGenerationTime = 24,
    spduInternalConsistencyextDataHashDoesntMatch = 25,
    spduInternalConsistencynoExtDataHashProvided = 26,
    spduInternalConsistencynoExtDataHashPresent = 27,
    spduLocalConsistencyPSIDsDontMatch = 28,
    spduLocalConsistencyChainWasTooLongForSDEE = 29,
    spduRelevanceGenerationTimeTooFarInPast = 30,
    spduRelevanceGenerationTimeTooFarInFuture = 31,
    spduRelevanceExpiryTimeInPast = 32,
    spduRelevanceGenerationLocationTooDistant = 33,
    spduRelevanceReplayedSpdu = 34,
    spduCertificateExpired = 35
} securityResultCode;

typedef enum _rxSource {
    RSU = 0,
    SAT, //XM satelite
    RV, /* for BSM rx */
    SNMP /* for SRM payload from backend/ODE*/
} rxSource;

/* below elements units are as per SAE-2735 */
typedef struct _location {
    uint32_t latitude;
    uint32_t longitude;
    uint32_t elevation;
    uint16_t speed;
    uint16_t heading;
} __attribute__((__packed__)) location;
/*
 * LEAR: 
 * Respective log files will have dump of below mentioned respective records.
 * "DriverAlert_msec_ipv6.csv.gzip" file will have dump of continues "driverAlertRecord" records.
 * 
 * Reading records from file:
 * fd = open("BSM30Sec_msec_ipv6.csv", O_RDONLY, 0666);
 * read(fd, &bsmTxRecord.timeInISO, MAX_ISO_TIME_LEN);
 * read(fd, &bsmTxRecord.length, 2(size of length:uint16_t));
 * read(fd, &bsmTxRecord.payload, bsmTxRecord.length);
 */
typedef struct _driverAlertRecord {
    location  curLocation;
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    uint16_t  length;
    char alert[MAX_STRING_LEN]; //LEAR: Alert will be a string.
} __attribute__((__packed__)) driverAlertRecord;

typedef struct _bsmTxRecord {
    uint8_t   direction; //0 for EV(Tx), 1 for RV(Rx)
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    uint16_t  length;
    uint8_t   payload[MAX_PAYLOAD_SIZE]; //LEAR: RAW 1609.2 format of Transmitted BSM
} __attribute__((__packed__)) bsmTxRecord;

typedef struct _receivedMsgRecord {
    location  curLocation;
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    rxSource  rxFrom;
    int8_t verificationStatus;
    uint16_t  length;
    uint8_t   payload[MAX_PAYLOAD_SIZE]; //LEAR: RAW 1609.2 format of TIM
} __attribute__((__packed__)) receivedMsgRecord;

typedef struct _bsmRxRecord {
    uint8_t   direction; //0 for EV(Tx), 1 for RV(Rx)
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    int8_t verificationStatus;
    uint16_t  length;
    uint8_t   payload[MAX_PAYLOAD_SIZE]; //LEAR: RAW 1609.2 format of Rx BSM
} __attribute__((__packed__)) bsmRxRecord;

typedef struct _dnmMsgRecord {
    location  curLocation;
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    int8_t verificationStatus;
    uint16_t  length;
    uint8_t   payload[MAX_PAYLOAD_SIZE]; //LEAR: RAW 1609.2 format of Tx & Rx DN TIM
} __attribute__((__packed__)) dnmMsgRecord;

/*
 * LEAR:
 * FW upgrade, System Logs will be logged to
 * respective log files in "syslog" format as mentioned
 * in below eaxmple:
 * May  2 21:30:36 wsarx: Consistency check failed [WS_ERR_NO_CA_CERTIFICATE].
 */
typedef struct _scmsRecord {
    /* TODO */
} __attribute__((__packed__)) scmsRecord;

/*
 * Same record format for both OBU upgrades & Sysmlog
 *
 */
typedef struct _updatesSystemLogRecord {
    //LEAR: Location & time are added in this record to know where & when exactly updates, boot, shutdown and any system events(application re-starts/crashes/errors) are happend. Since frequency of these messages are less not much over air over head by adding these.
    location  curLocation;
    uint32_t  utctimeInSec;
    uint16_t  mSec;
    uint16_t  length;
    char logString[MAX_STRING_LEN]; //LEAR: will be a string
} __attribute__((__packed__)) updatesSystemLogRecord;
#endif

/*
 * Copyright (c) 2017 Lear Corporation. All rights reserved.
 * Proprietary and Confidential Material.
 *  
 */ 
#ifndef _LOG_CONFIGURATIONS_H_
#define _LOG_CONFIGURATIONS_H_

typedef enum _rxSource {
    RSU = 0,
    SAT, //XM satelite
    RV, /* for BSM rx */
    SNMP /* for SRM payload from backend/ODE*/
} rxSource;

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

/* below elements units are as per SAE-2735 */
typedef struct _location {
    int32_t latitude;
    int32_t longitude;
    int32_t elevation;
    uint16_t speed;
    uint16_t heading;
} __attribute__((__packed__)) location;

typedef struct _intersection {
    int16_t intersectionId;
    int8_t intersectionStatus;
} __attribute__((__packed__)) intersection;

typedef struct _driverAlertRecord {
    location  curLocation;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) driverAlertRecord;

typedef struct _bsmLogRecHeader {
    uint8_t direction; //EV_BSM=0, RV_BSM=1
    location  curLocation;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    int8_t    signStatus;
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) bsmLogRecHeader;

typedef struct _receivedMsgRecord {
    uint8_t   rxFrom; /* refer rxSource for values */
    location  curLocation;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    int8_t    verificationStatus;
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) receivedMsgRecord;

typedef struct _dnmMsgRecord {
    location  curLocation;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    int8_t verificationStatus;
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) dnmMsgRecord;

typedef struct _SPaTMsgRecord {
    uint8_t   rxFrom; /* refer rxSource for values */
    intersection  curIntersection;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    int8_t    verificationStatus;
    int8_t    is_cert_present; /*ieee 1609 (acceptable values 0 = no,1 =yes)*/
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) SPaTMsgRecord;

/*
 * FW upgrade, SCMS, System Logs will be logged to
 * respective log files in "syslog" format as mentioned
 * in below eaxmple:
 * May  2 21:30:36 wsarx: Consistency check failed [WS_ERR_NO_CA_CERTIFICATE].
 * Do you prefer ISO time format here also??
 */
typedef struct _scmsRecord {
    /* TODO */
} __attribute__((__packed__)) scmsRecord;

/*
 * Same record format for both OBU upgrades & Sysmlog
 *
 */
typedef struct _updatesSystemLogRecord {
    location  curLocation;
    uint32_t  utcTimeInSec;
    uint16_t  msec;
    uint16_t  length;
    /* payload of length size*/
} __attribute__((__packed__)) updatesSystemLogRecord;

#endif

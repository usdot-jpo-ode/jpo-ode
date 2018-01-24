package us.dot.its.jpo.ode.model;

public class OdeLogMetadata extends OdeMsgMetadata {

   private static final long serialVersionUID = -8601265839394150140L;

   public enum RecordType {
      bsmLogDuringEvent, rxMsg, dnMsg, bsmTx, driverAlert, unsupported
   }

   public enum SecurityResultCode {
      success,
      unknown,
      inconsistentInputParameters,
      spduParsingInvalidInput,
      spduParsingUnsupportedCriticalInformationField,
      spduParsingCertificateNotFound,
      spduParsingGenerationTimeNotAvailable,
      spduParsingGenerationLocationNotAvailable,
      spduCertificateChainNotEnoughInformationToConstructChain,
      spduCertificateChainChainEndedAtUntrustedRoot,
      spduCertificateChainChainWasTooLongForImplementation,
      spduCertificateChainCertificateRevoked,
      spduCertificateChainOverdueCRL,
      spduCertificateChainInconsistentExpiryTimes,
      spduCertificateChainInconsistentStartTimes,
      spduCertificateChainInconsistentChainPermissions,
      spduCryptoVerificationFailure,
      spduConsistencyFutureCertificateAtGenerationTime,
      spduConsistencyExpiredCertificateAtGenerationTime,
      spduConsistencyExpiryDateTooEarly,
      spduConsistencyExpiryDateTooLate,
      spduConsistencyGenerationLocationOutsideValidityRegion,
      spduConsistencyNoGenerationLocation,
      spduConsistencyUnauthorizedPSID,
      spduInternalConsistencyExpiryTimeBeforeGenerationTime,
      spduInternalConsistencyextDataHashDoesntMatch,
      spduInternalConsistencynoExtDataHashProvided,
      spduInternalConsistencynoExtDataHashPresent,
      spduLocalConsistencyPSIDsDontMatch,
      spduLocalConsistencyChainWasTooLongForSDEE,
      spduRelevanceGenerationTimeTooFarInPast,
      spduRelevanceGenerationTimeTooFarInFuture,
      spduRelevanceExpiryTimeInPast,
      spduRelevanceGenerationLocationTooDistant,
      spduRelevanceReplayedSpdu,
      spduCertificateExpired
   }


   private String logFileName;
   private RecordType recordType;
   private SecurityResultCode securityResultCode;

   public OdeLogMetadata(OdeMsgPayload payload) {
      super(payload);
   }

   public OdeLogMetadata() {
      super();
   }

   public OdeLogMetadata(String payloadType, SerialId serialId, String receivedAt) {
      super(payloadType, serialId, receivedAt);
   }

   public String getLogFileName() {
      return logFileName;
   }

   public void setLogFileName(String logFileName) {
      this.logFileName = logFileName;
   }

   public RecordType getRecordType() {
      return recordType;
   }

   public void setRecordType(RecordType recordType) {
      this.recordType = recordType;
   }

   public SecurityResultCode getSecurityResultCode() {
      return securityResultCode;
   }

   public void setSecurityResultCode(SecurityResultCode securityResultCode) {
      this.securityResultCode = securityResultCode;
   }

}

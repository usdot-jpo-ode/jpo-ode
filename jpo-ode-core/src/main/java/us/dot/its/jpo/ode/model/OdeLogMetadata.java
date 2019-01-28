/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.model;

import java.util.ArrayList;
import java.util.List;

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
   private ReceivedMessageDetails receivedMessageDetails;
   private List<Asn1Encoding> encodings;

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

   public ReceivedMessageDetails getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }

   public List<Asn1Encoding> getEncodings() {
      return encodings;
   }

   public void setEncodings(List<Asn1Encoding> encodings) {
      this.encodings = encodings;
   }

   public OdeLogMetadata addEncoding(Asn1Encoding encoding) {
      if (encodings == null)
         encodings = new ArrayList<Asn1Encoding>();
      encodings.add(encoding);
      return this;
   }

}

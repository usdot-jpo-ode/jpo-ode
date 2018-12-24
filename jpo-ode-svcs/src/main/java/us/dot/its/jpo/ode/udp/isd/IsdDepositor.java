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
package us.dot.its.jpo.ode.udp.isd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;

public class IsdDepositor extends AbstractSubscriberDepositor {

   //TODO open-ode
//   private IntersectionSituationDataDeserializer deserializer;

   public IsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getIsdDepositorPort());
//TODO open-ode
//      this.deserializer = new IntersectionSituationDataDeserializer();
   }

   /**
    * TODO - utilize this method
    * 
    * @param encodedIsd
    */
   public void sendDataReceipt(byte[] encodedIsd) {

      /*
       * Send an ISDAcceptance message to confirm deposit
       */
      //TODO open-ode
//      IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
//      acceptance.dialogID = SemiDialogID.intersectionSitDataQuery;
//      acceptance.groupID = new GroupID(OdeProperties.getJpoOdeGroupId());
//      acceptance.requestID = getRequestId(encodedIsd);
//      acceptance.seqID = SemiSequenceID.accept;
//      acceptance.recordsSent = new INTEGER(trustManager.getSessionMessageCount(getRequestId(encodedIsd)));
//
//      byte[] encodedAccept = null;
//      try {
//         encodedAccept = coder.encode(acceptance).array();
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         logger.error("Error encoding ISD non-repudiation message", e);
//         return;
//      }
//
//      // Switching from socket.send to socket.receive in one thread is
//      // slower than non-repud round trip time so we must lead this by
//      // creating a socket.receive thread
//
//      try {
//         Future<AbstractData> f = Executors.newSingleThreadExecutor().submit(new DataReceiptReceiver(odeProperties, socket));
//         logger.debug("Submitted DataReceiptReceiver to listen on port {}", socket.getLocalPort());
//
//         String hexMsg = HexUtils.toHexString(encodedAccept);
//         logger.debug("Sending ISD non-repudiation message to SDC {} ", hexMsg);
//
//         socket.send(new DatagramPacket(encodedAccept, encodedAccept.length,
//               new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
//
//         DataReceipt receipt = (DataReceipt) f.get(odeProperties.getDataReceiptExpirationSeconds(), TimeUnit.SECONDS);
//
//         if (null != receipt) {
//            logger.debug("Successfully received data receipt from SDC {}", receipt);
//         } else {
//            throw new IOException("Received invalid packet.");
//         }
//
//      } catch (IOException | InterruptedException | ExecutionException e) {
//         logger.error("Error sending ISD Acceptance message to SDC", e);
//      } catch (TimeoutException e) {
//         logger.error("Did not receive ISD data receipt within alotted "
//               + +odeProperties.getDataReceiptExpirationSeconds() + " seconds " + e);
//      }
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

   //TODO open-ode
//   @Override
//   public SemiDialogID getDialogId() {
//      return SemiDialogID.intersectionSitDataDep;
//   }
//
//   @Override
//   public TemporaryID getRequestId(byte[] serializedMsg) {
//      return null;
//      IntersectionSituationData msg = deserializer.deserialize(null, serializedMsg);
//      return msg.getRequestID();
//   }
   
   @Override
   public byte[] encodeMessage(byte[] serializedMsg) {
      return serializedMsg;
//      IntersectionSituationData msg = deserializer.deserialize(null, serializedMsg);
//      
//      byte[] encodedMsg = null;
//      try {
//         encodedMsg = coder.encode(msg).array();
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         logger.error("Failed to encode serialized ISD for sending.", e);
//      }
//      
//      return encodedMsg;
   }

}

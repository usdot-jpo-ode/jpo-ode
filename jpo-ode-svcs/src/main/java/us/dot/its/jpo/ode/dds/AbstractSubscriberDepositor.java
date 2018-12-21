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
package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * @author 572682
 * This abstract class provides common and basic functionality for subscribing to
 * a messaging topic, processing the messages as implemented by a given MessageProcessor
 * and depositing the resultant data to SDC/SDW.  
 *
 */
public abstract class AbstractSubscriberDepositor extends AbstractSubscriberProcessor<String, byte[]> {

   protected OdeProperties odeProperties;
   protected DatagramSocket socket;;
// TODO open-ode
//   protected TrustManager trustManager;
//   protected Coder coder;
   private MessageConsumer<String, byte[]> consumer;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      super();
      this.odeProperties = odeProps;
      this.consumer = MessageConsumer.defaultByteArrayMessageConsumer(odeProps.getKafkaBrokers(),
         odeProps.getHostId() + this.getClass().getSimpleName(), this);
      this.consumer.setName(this.getClass().getSimpleName());

      //TODO open-ode
//      this.coder = J2735.getPERUnalignedCoder();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         this.socket = new DatagramSocket(port);
      // TODO open-ode
//         this.trustManager = new TrustManager(odeProps, socket);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   public void start(String... inputTopics) {
      super.start(consumer, inputTopics);
   }

   //TODO open-ode
   @Override
   public Object process(byte[] consumedData) {
      return consumedData;
//      if (null == consumedData || consumedData.length == 0) {
//         return null;
//      }
//
//      logger.info("Received data message for deposit");
//
//      TemporaryID requestID = getRequestId(consumedData);
//      SemiDialogID dialogID = getDialogId();
//
//      byte[] encodedMessage = null;
//      try {
//         if (trustManager.establishTrust(requestID, dialogID)) {
//            logger.debug("Sending message to SDC IP: {} Port: {}", odeProperties.getSdcIp(),
//                  odeProperties.getSdcPort());
//            encodedMessage = encodeMessage(consumedData);
//            sendToSdc(encodedMessage);
//            trustManager.incrementSessionTracker(requestID);
//         } else {
//            logger.error("Failed to establish trust, not sending message.");
//         }
//      } catch (UdpUtilException e) {
//         logger.error("Error Sending message to SDC", e);
//         return null;
//      }
//
//      String hexRequestID = HexUtils.toHexString(requestID.byteArrayValue());
//      logger.info("Messages sent since sessionID {} start: {}/{}", hexRequestID,
//            trustManager.getSessionMessageCount(requestID), odeProperties.getMessagesUntilTrustReestablished());
//
//      if (trustManager.getSessionMessageCount(requestID) >= odeProperties.getMessagesUntilTrustReestablished()) {
//         trustManager.endTrustSession(requestID);
//      }
//      return encodedMessage;
   }

   public void sendToSdc(byte[] msgBytes) throws UdpUtilException {
      UdpUtil.send(socket, msgBytes, odeProperties.getSdcIp(), odeProperties.getSdcPort());
   }

   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
   
   //TODO open-ode
//   public TemporaryID getRequestId(byte[] encodedMsg) {
//      return new TemporaryID(encodedMsg);
//   };
//
//   public abstract SemiDialogID getDialogId();
   public abstract byte[] encodeMessage(byte[] serializedMsg);
}

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
package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private static final String BSM_START_FLAG = "0014"; // these bytes indicate
                                                        // start of BSM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

   protected SerialId serialId;
   protected StringPublisher publisher;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.serialId = new SerialId();
      this.publisher = new StringPublisher(odeProperties);
   }

   @Override
   public void run() {

      logger.debug("UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP packets...");
            socket.receive(packet);
            if (packet.getLength() > 0) {
               senderIp = packet.getAddress().getHostAddress();
               senderPort = packet.getPort();
               logger.debug("Packet received from {}:{}", senderIp, senderPort);

               // extract the actualPacket from the buffer
               byte[] payload = removeHeader(packet.getData());
               String payloadHexString = HexUtils.toHexString(payload);
               logger.debug("Packet: {}", payloadHexString);

               publish(payload);
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   /**
    * Attempts to strip WSMP header bytes. If message starts with "0014",
    * message is raw BSM. Otherwise, headers are >= 20 bytes, so look past that
    * for start of payload BSM.
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);

      int startIndex = hexPacket.indexOf(BSM_START_FLAG);
      if (startIndex == 0) {
         logger.info("Message is raw BSM with no headers.");
      } else if (startIndex == -1) {
         logger.error("Message contains no BSM start flag.");
      } else {
         // We likely found a message with a header, look past the first 20
         // bytes for the start of the BSM
         int trueStartIndex = HEADER_MINIMUM_SIZE
               + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(BSM_START_FLAG);
         hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
      }

      return HexUtils.fromHexString(hexPacket);
   }
   
   public void publish(byte[] payloadBytes) throws XmlUtilsException {
     OdeAsn1Payload payload = new OdeAsn1Payload(payloadBytes);
     
     OdeLogMetadata msgMetadata = new OdeLogMetadata(payload);
     msgMetadata.setSerialId(serialId);

     Asn1Encoding msgEncoding = new Asn1Encoding("root", "MessageFrame", EncodingRule.UPER);
     msgMetadata.addEncoding(msgEncoding);
     OdeAsn1Data asn1Data = new OdeAsn1Data(msgMetadata, payload);

     publisher.publish(XmlUtils.toXmlStatic(asn1Data), publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
     serialId.increment();
  }

}

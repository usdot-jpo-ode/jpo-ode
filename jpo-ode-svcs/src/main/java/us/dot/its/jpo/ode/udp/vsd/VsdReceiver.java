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
package us.dot.its.jpo.ode.udp.vsd;

//TODO open-ode
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.util.Arrays;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.oss.asn1.AbstractData;
//
//import us.dot.its.jpo.ode.OdeProperties;
//import us.dot.its.jpo.ode.model.OdeBsmData;
//import us.dot.its.jpo.ode.model.SerialId;
//import us.dot.its.jpo.ode.udp.UdpUtil;
//import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
//import us.dot.its.jpo.ode.wrapper.MessageProducer;
//import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;
//
//public class VsdReceiver extends BsmReceiver {
//
//   private static final Logger logger = LoggerFactory.getLogger(VsdReceiver.class);
//   protected MessageProducer<String, OdeBsmData> odeBsmDataProducer;
//   private SerialId serialId = new SerialId();
//   
//   @Autowired
//   public VsdReceiver(OdeProperties odeProps) {
//      super(odeProps, odeProps.getVsdReceiverPort(), odeProps.getVsdBufferSize());
//      odeBsmDataProducer = new MessageProducer<String, OdeBsmData>(
//              odeProperties.getKafkaBrokers(),
//              odeProperties.getKafkaProducerType(), 
//              null, 
//              OdeBsmSerializer.class.getName());
//   }
//
//   @Override
//   public void run() {
//
//      logger.debug("Starting {}...", this.getClass().getSimpleName());
//
//      byte[] buffer = new byte[odeProperties.getVsdBufferSize()];
//
//      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//
//      while (!isStopped()) {
//         getPacket(packet);
//      }
//   }
//
//   public void getPacket(DatagramPacket packet) {
//      try {
//         logger.debug("Waiting for UDP packets...");
//         socket.receive(packet);
//         if (packet.getLength() > 0) {
//            senderIp = packet.getAddress().getHostAddress();
//            senderPort = packet.getPort();
//            logger.debug("Packet received from {}:{}", senderIp, senderPort);
//
//            // extract the actualPacket from the buffer
//            byte[] payload = Arrays.copyOf(packet.getData(), packet.getLength());
//            processPacket(payload);
//         }
//      } catch (IOException | UdpReceiverException e) {
//         logger.error("Error receiving packet", e);
//      }
//   }
//
//   public void processPacket(byte[] data) throws UdpReceiverException {
//      AbstractData decoded = super.decodeData(data);
//      try {
//         if (decoded instanceof ServiceRequest) {
//
//            if (null != ((ServiceRequest) decoded).getDestination()) {
//               ConnectionPoint cp = ((ServiceRequest) decoded).getDestination();
//
//               // Change return address, if specified
//               if (null != cp.getAddress()) {
//                  senderIp = ((ServiceRequest) decoded).getDestination().getAddress().toString();
//               }
//
//               // Change return port, if specified
//               if (null != cp.getPort()) {
//                  senderPort = ((ServiceRequest) decoded).getDestination().getPort().intValue();
//               }
//               logger.error("Service request response destination specified {}:{}", senderIp, senderPort);
//            }
//
//            UdpUtil.send(new DatagramSocket(odeProperties.getVsdTrustport()), decoded, senderIp, senderPort);
//         } else if (decoded instanceof VehSitDataMessage) {
//            logger.debug("Received VSD");
//            extractAndPublishBsms((VehSitDataMessage) decoded);
//         } else {
//            logger.error("Unknown message type received {}", decoded.getClass().getName());
//         }
//      } catch (Exception e) {
//         logger.error("Error processing message", e);
//      }
//   }
//
//   protected void extractAndPublishBsms(AbstractData data) {
//      VehSitDataMessage msg = (VehSitDataMessage) data;
//      List<BasicSafetyMessage> bsmList = null;
//      try {
//         bsmList = VsdToBsmConverter.convert(msg);
//      } catch (IllegalArgumentException e) {
//         logger.error("Unable to convert VehSitDataMessage bundle to BSM list", e);
//         return;
//      }
//
//      int i = 1;
//      for (BasicSafetyMessage entry : bsmList) {
//         logger.debug("Publishing BSM {}/{} to topic {}", 
//             i++, msg.getBundle().getSize(), odeProperties.getKafkaTopicOdeBsmPojo());
//         
//         J2735Bsm j2735Bsm = OssBsm.genericBsm(entry);
//         serialId.addBundleId(1).addRecordId(1);
//         OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(
//            (J2735Bsm) j2735Bsm, new IEEE1609p2Message(), null);
//        
//         odeBsmDataProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsmData);
//      }
//   }
//   
//}

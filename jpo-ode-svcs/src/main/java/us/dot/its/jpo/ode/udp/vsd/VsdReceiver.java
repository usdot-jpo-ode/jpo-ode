package us.dot.its.jpo.ode.udp.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.trust.ServiceMessageUtil;

public class VsdReceiver extends BsmReceiver {

   private static Logger logger = LoggerFactory.getLogger(VsdReceiver.class);

   @Autowired
   public VsdReceiver(OdeProperties odeProps) {
      super(odeProps, odeProps.getVsdReceiverPort(), odeProps.getVsdBufferSize());
   }

   @Override
   public void run() {

      logger.debug("Starting {}...", this.getClass().getSimpleName());

      byte[] buffer = new byte[odeProperties.getVsdBufferSize()];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      while (!isStopped()) {

         try {
            logger.debug("Waiting for UDP packets...");
            socket.receive(packet);
            if (packet.getLength() > 0) {
               senderIp = packet.getAddress().getHostAddress();
               senderPort = packet.getPort();
               logger.debug("Packet received from {}:{}", senderIp, senderPort);

               // extract the actualPacket from the buffer
               byte[] payload = Arrays.copyOf(packet.getData(), packet.getLength());
               processPacket(payload);
            }
         } catch (IOException e) {
            logger.error("Error receiving packet", e);
         } catch (UdpReceiverException e) {
            logger.error("Error decoding packet", e);
         }
      }
   }

   private void processPacket(byte[] data) throws UdpReceiverException {
      AbstractData decoded = super.decodeData(data);
      try {
         if (decoded instanceof ServiceRequest) {

            if (null != ((ServiceRequest) decoded).getDestination()) {
               ConnectionPoint cp = ((ServiceRequest) decoded).getDestination();

               // Change return address, if specified
               if (null != cp.getAddress()) {
                  senderIp = ((ServiceRequest) decoded).getDestination().getAddress().toString();
               }

               // Change return port, if specified
               if (null != cp.getPort()) {
                  senderPort = ((ServiceRequest) decoded).getDestination().getPort().intValue();
               }
               logger.error("Service request response destination specified {}:{}", senderIp, senderPort);
            }

            ServiceMessageUtil.encodeAndSend(new DatagramSocket(odeProperties.getVsdTrustport()), decoded, senderIp,
                  senderPort);
         } else if (decoded instanceof VehSitDataMessage) {
            logger.debug("Received VSD");

            /*
             * TODO ODE-314: We need to publish to a VSD topic publish(data,
             * odeProperties.getKafkaTopicVsd());
             * 
             * and then do the unpacking in a separate thread that consumes VSDs
             * from that VSD topic and publishes BSMs to BSM POJO topic
             */
            extractAndPublishBsms((VehSitDataMessage) decoded);
         } else {
            logger.error("Unknown message type received {}", decoded.getClass().getName());
         }
      } catch (Exception e) {
         logger.error("Error processing message", e);
      }
   }

   protected void extractAndPublishBsms(AbstractData data) throws OssBsmPart2Exception {
      VehSitDataMessage msg = (VehSitDataMessage) data;
      List<BasicSafetyMessage> bsmList = null;
      try {
         bsmList = VsdToBsmConverter.convert(msg);
      } catch (IllegalArgumentException e) {
         logger.error("Unable to convert VehSitDataMessage bundle to BSM list", e);
         return;
      }

      int i = 1;
      for (BasicSafetyMessage entry : bsmList) {
         logger.debug("Publishing BSM {}/{}", i++, msg.getBundle().getSize());
         J2735Bsm j2735Bsm = OssBsm.genericBsm(entry);
         publishBasicSafetyMessage(j2735Bsm);
      }
   }
}

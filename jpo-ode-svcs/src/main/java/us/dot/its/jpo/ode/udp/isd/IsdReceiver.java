package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.ByteArrayPublisher;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class IsdReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(IsdReceiver.class);
   protected ByteArrayPublisher publisher;
//TODO open-ode   private IntersectionSituationDataSerializer serializer;

   @Autowired
   public IsdReceiver(OdeProperties odeProps) {
      super(odeProps, odeProps.getIsdReceiverPort(), odeProps.getIsdBufferSize());
      this.publisher = new ByteArrayPublisher(odeProps);
    //TODO open-ode      this.serializer = new IntersectionSituationDataSerializer();
   }

   @Override
   public void run() {

      logger.debug("Starting {}...", this.getClass().getSimpleName());

      byte[] buffer = new byte[odeProperties.getIsdBufferSize()];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      while (!isStopped()) {
         getPacket(packet);
      }

   }

   public void getPacket(DatagramPacket packet) {
      try {
         logger.debug("Listening on port: {}", port);
         socket.receive(packet);
         if (packet.getLength() > 0) {
            senderIp = packet.getAddress().getHostAddress();
            senderPort = packet.getPort();
            logger.debug("Packet received from {}:{}", senderIp, senderPort);

            // extract the actual packet from the buffer
            processPacket(Arrays.copyOf(packet.getData(), packet.getLength()));
         }
      } catch (IOException | UdpReceiverException e) {
         logger.error("Error receiving packet", e);
      }
   }

   public void processPacket(byte[] data) throws UdpReceiverException {
      //TODO open-ode
//      AbstractData decoded = super.decodeData(data);
//      try {
//         if (decoded instanceof ServiceRequest) {
//
//            String hexMsg = HexUtils.toHexString(data);
//            logger.debug("Received ServiceRequest: {}", hexMsg);
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
//               logger.debug("ServiceResponse destination overriden: {}:{}", senderIp, senderPort);
//            }
//            UdpUtil.send(socket, decoded, senderIp, senderPort);
//         } else if (decoded instanceof IntersectionSituationData) {
//            String hexMsg = HexUtils.toHexString(data);
//            logger.debug("Received ISD: {}", hexMsg);
//          //TODO open-ode            publisher.publish(serializer.serialize(null, (IntersectionSituationData) decoded), odeProperties.getKafkaTopicIsdPojo());
//         } else {
//            String hexMsg = HexUtils.toHexString(data);
//            logger.error("Unknown message type received {}", hexMsg);
//         }
//      } catch (Exception e) {
//         logger.error("Error processing message", e);
//      }
   }

}

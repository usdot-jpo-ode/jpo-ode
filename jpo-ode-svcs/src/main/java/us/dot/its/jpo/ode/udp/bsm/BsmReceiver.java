package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.coder.stream.ByteDecoderPublisher;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private ByteDecoderPublisher byteDecoderPublisher;

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      MessagePublisher messagePub = new MessagePublisher(odeProperties);

      OssJ2735Coder jDec = null;
      logger.info("Loading ASN1 Coder: {}", odeProps.getJ2735CoderClassName());
      try {
         jDec = (OssJ2735Coder) PluginFactory.getPluginByName(odeProperties.getJ2735CoderClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         logger.error("Unable to load plugin: " + odeProperties.getJ2735CoderClassName(), e);
      }

      Oss1609dot2Coder ieee1609dotCoder = new Oss1609dot2Coder();
      DecoderHelper decoderHelper = new DecoderHelper(jDec, ieee1609dotCoder);
      byteDecoderPublisher = new ByteDecoderPublisher(messagePub, decoderHelper);
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
               logger.debug("Packet: {}", HexUtils.toHexString(payload));
               byteDecoderPublisher.decodeAndPublish(payload);
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   /**
    * Attempts to strip WSMP header bytes
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);
      int startIndex = hexPacket.indexOf("0014");
      logger.debug("BSM packet length: {}, start index: {}", hexPacket.length(), startIndex);
      if (startIndex >= 20) {
         hexPacket = hexPacket.substring(startIndex, hexPacket.length());
      }
      return HexUtils.fromHexString(hexPacket);
   }
}

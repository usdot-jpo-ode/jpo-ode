package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.coder.stream.ByteDecoderPublisher;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private static final String BSM_START_FLAG = "0014"; // these bytes indicate
                                                        // start of BSM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

   private ByteDecoderPublisher byteDecoderPublisher;

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      MessagePublisher messagePub = new MessagePublisher(odeProperties);

      byteDecoderPublisher = new ByteDecoderPublisher(messagePub);
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
    * Attempts to strip WSMP header bytes. Looks for BSM start sequence "0014"
    * occurring after 20 bytes. If not after 20 bytes, message probably does not
    * contain a header.
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);
      int startIndex = hexPacket.indexOf(BSM_START_FLAG);
      logger.debug("BSM packet length: {}, start index: {}", hexPacket.length(), startIndex);
      if (startIndex >= HEADER_MINIMUM_SIZE) {
         hexPacket = hexPacket.substring(startIndex, hexPacket.length());
      }
      return HexUtils.fromHexString(hexPacket);
   }
}

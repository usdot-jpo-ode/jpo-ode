package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private static final String BSM_START_FLAG = "0014"; // these bytes indicate
                                                        // start of BSM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

   private StringPublisher bsmPublisher;

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());

      this.bsmPublisher = new StringPublisher(odeProps);
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.bsmPublisher = new StringPublisher(odeProps);
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
               if (payload == null)
                  continue;
               String payloadHexString = HexUtils.toHexString(payload);
               logger.debug("Packet: {}", payloadHexString);
               
               // Add header data for the decoding process
               ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
               String timestamp = utc.format(DateTimeFormatter.ISO_INSTANT);

               JSONObject metadataObject = new JSONObject();
               metadataObject.put("utctimestamp", timestamp);
               metadataObject.put("originRsu", senderIp);

               JSONObject messageObject = new JSONObject();
               messageObject.put("metadata", metadataObject);
               messageObject.put("payload", payloadHexString);

               JSONArray messageList = new JSONArray();
               messageList.put(messageObject);

               JSONObject jsonObject = new JSONObject();
               jsonObject.put("BsmMessageContent", messageList);

               logger.debug("BSM JSON Object: {}", jsonObject.toString());

               // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
               this.bsmPublisher.publish(jsonObject.toString(), this.bsmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedBSMJson());
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

      // logger.debug("BSM packet length: {}, start index: {}",
      // hexPacket.length(), startIndex);

      int startIndex = hexPacket.indexOf(BSM_START_FLAG);
      if (startIndex == 0) {
         logger.debug("Message is raw BSM with no headers.");
      } else if (startIndex == -1) {
         logger.error("Message contains no BSM start flag.");
         return null;
      } else {
         // We likely found a message with a header, look past the first 20
         // bytes for the start of the BSM
         int trueStartIndex = HEADER_MINIMUM_SIZE
               + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(BSM_START_FLAG);
         hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
      }

      return HexUtils.fromHexString(hexPacket);
   }
}

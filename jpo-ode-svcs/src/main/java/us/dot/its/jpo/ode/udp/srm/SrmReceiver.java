package us.dot.its.jpo.ode.udp.srm;

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

public class SrmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(SrmReceiver.class);

   private static final String SRM_START_FLAG = "001d"; // these bytes indicate
                                                        // start of SRM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

   private StringPublisher srmPublisher;

   @Autowired
   public SrmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getSrmReceiverPort(), odeProps.getSrmBufferSize());

      this.srmPublisher = new StringPublisher(odeProps);
   }

   public SrmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.srmPublisher = new StringPublisher(odeProps);
   }

   @Override
   public void run() {

      logger.debug("SRM UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP SRM packets...");
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
               metadataObject.put("source", "RSU");

               JSONObject messageObject = new JSONObject();
               messageObject.put("metadata", metadataObject);
               messageObject.put("payload", payloadHexString);

               JSONArray messageList = new JSONArray();
               messageList.put(messageObject);

               JSONObject jsonObject = new JSONObject();
               jsonObject.put("SrmMessageContent", messageList);

               logger.debug("SRM JSON Object: {}", jsonObject.toString());

               // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
               this.srmPublisher.publish(jsonObject.toString(), this.srmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSRMJson());
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   /**
    * Attempts to strip WSMP header bytes. If message starts with "001d",
    * message is raw SRM. Otherwise, headers are >= 20 bytes, so look past that
    * for start of payload SRM.
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);

      int startIndex = hexPacket.indexOf(SRM_START_FLAG);
      if (startIndex == 0) {
         logger.debug("Message is raw SRM with no headers.");
      } else if (startIndex == -1) {
         logger.error("Message contains no SRM start flag.");
         return null;
      } else {
         // We likely found a message with a header, look past the first 20
         // bytes for the start of the SRM
         int trueStartIndex = HEADER_MINIMUM_SIZE
               + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(SRM_START_FLAG);
         hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
      }

      return HexUtils.fromHexString(hexPacket);
   }
}

package us.dot.its.jpo.ode.udp.tim;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class TimReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(TimReceiver.class);

   private static final String TIM_START_FLAG = "001f"; // these bytes indicate
                                                        // start of TIM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

   private StringPublisher timPublisher;

   @Autowired
   public TimReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getTimReceiverPort(), odeProps.getTimBufferSize());

      this.timPublisher = new StringPublisher(odeProps);
   }

   public TimReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.timPublisher = new StringPublisher(odeProps);
   }

   @Override
   public void run() {

      logger.debug("TIM UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP TIM packets...");
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
               
               logger.debug("Creating Decoded TIM JSON Object...");

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
               jsonObject.put("TimMessageContent", messageList);

               logger.debug("TIM JSON Object: {}", jsonObject.toString());

               // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
               logger.debug("Publishing JSON TIM...");

               this.timPublisher.publish(jsonObject.toString(), this.timPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedTIMJson());
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   /**
    * Attempts to strip WSMP header bytes. If message starts with "001F",
    * message is raw TIM. Otherwise, headers are >= 20 bytes, so look past that
    * for start of payload TIM.
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);

      //logger.debug("TIM packet: {}", hexPacket);

      int startIndex = hexPacket.indexOf(TIM_START_FLAG);
      if (startIndex == 0) {
         logger.info("Message is raw TIM with no headers.");
      } else if (startIndex == -1) {
         logger.error("Message contains no TIM start flag.");
         return null;
      } else {
         // We likely found a message with a header, look past the first 20
         // bytes for the start of the TIM
         int trueStartIndex = HEADER_MINIMUM_SIZE
               + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(TIM_START_FLAG);
         hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
      }

      return HexUtils.fromHexString(hexPacket);
   }
}

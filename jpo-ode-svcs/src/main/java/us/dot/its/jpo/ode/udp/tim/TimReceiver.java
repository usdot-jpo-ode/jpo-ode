package us.dot.its.jpo.ode.udp.tim;

import java.net.DatagramPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;

public class TimReceiver extends AbstractUdpReceiverPublisher {
   private static Logger logger = LoggerFactory.getLogger(TimReceiver.class);

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
               byte[] payload = packet.getData();
               if (payload == null)
                  continue;

               // convert bytes to hex string and verify identity
               String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
               if (payloadHexString.indexOf(odeProperties.getTimStartFlag()) == -1)
                  continue;
               logger.debug("Full TIM packet: {}", payloadHexString);
               payloadHexString = super.stripDot3Header(payloadHexString, odeProperties.getTimStartFlag());
               logger.debug("Stripped TIM packet: {}", payloadHexString);

               // Create OdeMsgPayload and OdeLogMetadata objects and populate them
               OdeAsn1Payload timPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
               OdeTimMetadata timMetadata = new OdeTimMetadata(timPayload);

               // Add header data for the decoding process
               ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
               String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
               timMetadata.setOdeReceivedAt(timestamp);

               timMetadata.setOriginIp(senderIp);
               timMetadata.setRecordType(RecordType.timMsg);
               timMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
               timMetadata.setSecurityResultCode(SecurityResultCode.success);

               // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
               timPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(timMetadata, timPayload), false),
                  timPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedTIMJson());
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }
}

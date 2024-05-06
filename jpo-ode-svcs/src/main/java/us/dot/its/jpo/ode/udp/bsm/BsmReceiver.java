package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

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

      logger.debug("BSM UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP BSM packets...");
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
               if (payloadHexString.indexOf(odeProperties.getBsmStartFlag()) == -1)
                  continue;
               logger.debug("Full BSM packet: {}", payloadHexString);
               payloadHexString = super.stripDot3Header(payloadHexString, odeProperties.getBsmStartFlag());
               logger.debug("Stripped BSM packet: {}", payloadHexString);

               // Create OdeMsgPayload and OdeLogMetadata objects and populate them
               OdeAsn1Payload bsmPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
               OdeBsmMetadata bsmMetadata = new OdeBsmMetadata(bsmPayload);

               // Set BSM Metadata values that can be assumed from the UDP endpoint
               ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
               String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
               bsmMetadata.setOdeReceivedAt(timestamp);

               ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
               OdeLogMsgMetadataLocation locationData = new OdeLogMsgMetadataLocation(
                  "unavailable", 
                  "unavailable", 
                  "unavailable", 
                  "unavailable", 
                  "unavailable");
					receivedMessageDetails.setRxSource(RxSource.RSU);
               receivedMessageDetails.setLocationData(locationData);
               bsmMetadata.setReceivedMessageDetails(receivedMessageDetails);

               bsmMetadata.setOriginIp(senderIp);
               bsmMetadata.setBsmSource(BsmSource.EV);
               bsmMetadata.setRecordType(RecordType.bsmTx);
               bsmMetadata.setRecordGeneratedBy(GeneratedBy.OBU);
               bsmMetadata.setSecurityResultCode(SecurityResultCode.success);

               // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
               bsmPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(bsmMetadata, bsmPayload), false),
                  bsmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedBSMJson());
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }
}

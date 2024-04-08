package us.dot.its.jpo.ode.udp.map;

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
import us.dot.its.jpo.ode.model.OdeMapMetadata.MapSource;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;

public class MapReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(MapReceiver.class);

    private StringPublisher mapPublisher;

    @Autowired
    public MapReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getMapReceiverPort(), odeProps.getMapBufferSize());

        this.mapPublisher = new StringPublisher(odeProps);
    }

    public MapReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.mapPublisher = new StringPublisher(odeProps);
    }

    @Override
    public void run() {

        logger.debug("Map UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP Map packets...");
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
                    if (payloadHexString.indexOf(odeProperties.getMapStartFlag()) == -1)
                        continue;
                    logger.debug("Full Map packet: {}", payloadHexString);
                    payloadHexString = super.stripDot3Header(payloadHexString, odeProperties.getMapStartFlag());
                    logger.debug("Stripped Map packet: {}", payloadHexString);

                    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
                    OdeAsn1Payload mapPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
                    OdeMapMetadata mapMetadata = new OdeMapMetadata(mapPayload);

                    // Add header data for the decoding process
                    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                    String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    mapMetadata.setOdeReceivedAt(timestamp);

                    mapMetadata.setOriginIp(senderIp);
                    mapMetadata.setMapSource(MapSource.RSU);
                    mapMetadata.setRecordType(RecordType.mapTx);
                    mapMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
                    mapMetadata.setSecurityResultCode(SecurityResultCode.success);

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    mapPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(mapMetadata, mapPayload), false),
                        mapPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedMAPJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

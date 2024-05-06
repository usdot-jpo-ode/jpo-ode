package us.dot.its.jpo.ode.udp.spat;

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
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.OdeSpatMetadata.SpatSource;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;

public class SpatReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SpatReceiver.class);

    private StringPublisher spatPublisher;

    @Autowired
    public SpatReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getSpatReceiverPort(), odeProps.getSpatBufferSize());

        this.spatPublisher = new StringPublisher(odeProps);
    }

    public SpatReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.spatPublisher = new StringPublisher(odeProps);
    }

    @Override
    public void run() {

        logger.debug("SPaT UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP SPaT packets...");
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
                    if (payloadHexString.indexOf(odeProperties.getSpatStartFlag()) == -1)
                        continue;
                    logger.debug("Full SPaT packet: {}", payloadHexString);
                    payloadHexString = super.stripDot3Header(payloadHexString, odeProperties.getSpatStartFlag());
                    logger.debug("Stripped SPaT packet: {}", payloadHexString);

                    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
                    OdeAsn1Payload spatPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
                    OdeSpatMetadata spatMetadata = new OdeSpatMetadata(spatPayload);

                    // Add header data for the decoding process
                    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                    String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    spatMetadata.setOdeReceivedAt(timestamp);

                    spatMetadata.setOriginIp(senderIp);
                    spatMetadata.setSpatSource(SpatSource.RSU);
                    spatMetadata.setRecordType(RecordType.spatTx);
                    spatMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
                    spatMetadata.setSecurityResultCode(SecurityResultCode.success);

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    spatPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(spatMetadata, spatPayload), false),
                        spatPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSPATJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

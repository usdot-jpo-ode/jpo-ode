package us.dot.its.jpo.ode.udp.ssm;

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
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.model.OdeSsmMetadata.SsmSource;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;

public class SsmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SsmReceiver.class);

    private StringPublisher ssmPublisher;

    @Autowired
    public SsmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getSsmReceiverPort(), odeProps.getSsmBufferSize());

        this.ssmPublisher = new StringPublisher(odeProps);
    }

    public SsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.ssmPublisher = new StringPublisher(odeProps);
    }

    @Override
    public void run() {

        logger.debug("SSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP SSM packets...");
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
                    if (payloadHexString.indexOf(odeProperties.getSsmStartFlag()) == -1)
                        continue;
                    logger.debug("Full SSM packet: {}", payloadHexString);
                    payloadHexString = super.stripDot3Header(payloadHexString, odeProperties.getSsmStartFlag());
                    logger.debug("Stripped SSM packet: {}", payloadHexString);

                    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
                    OdeAsn1Payload ssmPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
                    OdeSsmMetadata ssmMetadata = new OdeSsmMetadata(ssmPayload);

                    // Add header data for the decoding process
                    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                    String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    ssmMetadata.setOdeReceivedAt(timestamp);

                    ssmMetadata.setOriginIp(senderIp);
                    ssmMetadata.setSsmSource(SsmSource.RSU);
                    ssmMetadata.setRecordType(RecordType.ssmTx);
                    ssmMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
                    ssmMetadata.setSecurityResultCode(SecurityResultCode.success);

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    ssmPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(ssmMetadata, ssmPayload), false),
                        ssmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSSMJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

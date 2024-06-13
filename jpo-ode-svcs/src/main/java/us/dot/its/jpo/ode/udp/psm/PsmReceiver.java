package us.dot.its.jpo.ode.udp.psm;

import java.net.DatagramPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdePsmMetadata.PsmSource;
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

public class PsmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(PsmReceiver.class);

    private StringPublisher psmPublisher;

    @Autowired
    public PsmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getPsmReceiverPort(), odeProps.getPsmBufferSize());

        this.psmPublisher = new StringPublisher(odeProps);
    }

    public PsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.psmPublisher = new StringPublisher(odeProps);
    }

    @Override
    public void run() {

        logger.debug("PSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP PSM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    senderIp = packet.getAddress().getHostAddress();
                    senderPort = packet.getPort();
                    logger.debug("Packet received from {}:{}", senderIp, senderPort);

                    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
                    OdeAsn1Payload psmPayload = super.getPayloadHexString(packet, UperUtil.SupportedMessageTypes.PSM);
                    if (psmPayload == null)
                        continue;
                    OdePsmMetadata psmMetadata = new OdePsmMetadata(psmPayload);

                    // Add header data for the decoding process
                    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                    String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    psmMetadata.setOdeReceivedAt(timestamp);

                    psmMetadata.setOriginIp(senderIp);
                    psmMetadata.setPsmSource(PsmSource.RSU);
                    psmMetadata.setRecordType(RecordType.psmTx);
                    psmMetadata.setRecordGeneratedBy(GeneratedBy.UNKNOWN);
                    psmMetadata.setSecurityResultCode(SecurityResultCode.success);

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    psmPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(psmMetadata, psmPayload), false),
                        psmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedPSMJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

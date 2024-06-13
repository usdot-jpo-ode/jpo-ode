package us.dot.its.jpo.ode.udp.srm;

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
import us.dot.its.jpo.ode.model.OdeSrmMetadata;
import us.dot.its.jpo.ode.model.OdeSrmMetadata.SrmSource;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

public class SrmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SrmReceiver.class);

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

                    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
                    OdeAsn1Payload srmPayload = super.getPayloadHexString(packet, UperUtil.SupportedMessageTypes.SRM);
                    if (srmPayload == null)
                        continue;
                    OdeSrmMetadata srmMetadata = new OdeSrmMetadata(srmPayload);

                    // Add header data for the decoding process
                    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                    String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    srmMetadata.setOdeReceivedAt(timestamp);

                    srmMetadata.setOriginIp(senderIp);
                    srmMetadata.setSrmSource(SrmSource.RSU);
                    srmMetadata.setRecordType(RecordType.srmTx);
                    srmMetadata.setRecordGeneratedBy(GeneratedBy.OBU);
                    srmMetadata.setSecurityResultCode(SecurityResultCode.success);

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    srmPublisher.publish(JsonUtils.toJson(new OdeAsn1Data(srmMetadata, srmPayload), false),
                        srmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSRMJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

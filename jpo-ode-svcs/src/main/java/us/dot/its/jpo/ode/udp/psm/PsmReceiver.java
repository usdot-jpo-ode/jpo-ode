package us.dot.its.jpo.ode.udp.psm;

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

public class PsmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(PsmReceiver.class);

    private static final String PSM_START_FLAG = "0020"; // these bytes indicate
                                                          // start of PSM payload
    private static final int HEADER_MINIMUM_SIZE = 6; // WSMP headers are at
                                                       // least 20 bytes long

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
                    jsonObject.put("PsmMessageContent", messageList);

                    logger.debug("PSM JSON Object: {}", jsonObject.toString());

                    // Submit JSON to the OdeRawEncodedMessageJson Kafka Topic
                    this.psmPublisher.publish(jsonObject.toString(),
                            this.psmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedPSMJson());
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

    /**
     * Attempts to strip WSMP header bytes. If message starts with "0013", message
     * is raw SPAT. Otherwise, headers are >= 20 bytes, so look past that for start
     * of payload SPAT.
     * 
     * @param packet
     */
    public byte[] removeHeader(byte[] packet) {
        String hexPacket = HexUtils.toHexString(packet);
        logger.debug("PSM packet: {}", hexPacket);

        int startIndex = hexPacket.indexOf(PSM_START_FLAG);
        if (startIndex == 0) {
            logger.debug("Message is raw PSM with no headers.");
        } else if (startIndex == -1) {
            logger.error("Message contains no PSM start flag.");
            return null;
        } else {
            // We likely found a message with a header, look past the first 20
            // bytes for the start of the PSM
            int trueStartIndex = HEADER_MINIMUM_SIZE
                    + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(PSM_START_FLAG);
            hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
        }
        logger.debug("PSM packet substring: {}", hexPacket);

        return HexUtils.fromHexString(hexPacket);
    }
}

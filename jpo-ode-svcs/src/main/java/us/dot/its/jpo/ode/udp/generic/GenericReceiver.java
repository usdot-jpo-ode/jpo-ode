package us.dot.its.jpo.ode.udp.generic;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.uper.UperUtil;

import java.net.DatagramPacket;

@Slf4j
public class GenericReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher publisher;
    private final RawEncodedJsonTopics rawEncodedJsonTopics;

    public GenericReceiver(UDPReceiverProperties.ReceiverProperties props, OdeKafkaProperties odeKafkaProperties, RawEncodedJsonTopics rawEncodedJsonTopics) {
        super(props.getReceiverPort(), props.getBufferSize());

        this.publisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getKafkaType(), odeKafkaProperties.getDisabledTopics());
        this.rawEncodedJsonTopics = rawEncodedJsonTopics;
    }

    @Override
    public void run() {
        log.debug("Generic UDP Receiver Service started.");

        byte[] buffer;
        do {
            buffer = new byte[bufferSize];
            // packet should be recreated on each loop to prevent latent data in buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                log.debug("Waiting for Generic UDP packets...");
                socket.receive(packet);
                byte[] payload = packet.getData();
                if ((packet.getLength() <= 0) || (payload == null)) {
                    log.debug("Skipping empty payload");
                    continue;
                }

                senderIp = packet.getAddress().getHostAddress();
                senderPort = packet.getPort();
                log.debug("Packet received from {}:{}", senderIp, senderPort);

                String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
                log.debug("Raw Payload {}", payloadHexString);

                String messageType = UperUtil.determineHexPacketType(payloadHexString);

                log.debug("Detected Message Type {}", messageType);

                switch (messageType) {
                    case "MAP" -> {
                        String mapJson = UdpHexDecoder.buildJsonMapFromPacket(packet);
                        log.debug("Sending Data to Topic {}", mapJson);
                        if (mapJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getMap(), mapJson);
                        }
                    }
                    case "SPAT" -> {
                        String spatJson = UdpHexDecoder.buildJsonSpatFromPacket(packet);
                        if (spatJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getSpat(), spatJson);
                        }
                    }
                    case "TIM" -> {
                        String timJson = UdpHexDecoder.buildJsonTimFromPacket(packet);
                        if (timJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getTim(), timJson);
                        }
                    }
                    case "BSM" -> {
                        String bsmJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);
                        if (bsmJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getBsm(), bsmJson);
                        }
                    }
                    case "SSM" -> {
                        String ssmJson = UdpHexDecoder.buildJsonSsmFromPacket(packet);
                        if (ssmJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getSsm(), ssmJson);
                        }
                    }
                    case "SRM" -> {
                        String srmJson = UdpHexDecoder.buildJsonSrmFromPacket(packet);
                        if (srmJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getSrm(), srmJson);
                        }
                    }
                    case "PSM" -> {
                        String psmJson = UdpHexDecoder.buildJsonPsmFromPacket(packet);
                        if (psmJson != null) {
                            publisher.publish(rawEncodedJsonTopics.getPsm(), psmJson);
                        }
                    }
                    default -> log.debug("Unknown Message Type");
                }
            } catch (InvalidPayloadException e) {
                log.error("Error decoding packet", e);
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

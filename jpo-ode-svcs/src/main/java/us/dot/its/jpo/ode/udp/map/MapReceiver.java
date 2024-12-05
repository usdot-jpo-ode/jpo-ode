package us.dot.its.jpo.ode.udp.map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class MapReceiver extends AbstractUdpReceiverPublisher {

    KafkaTemplate<String, String> kafkaTemplate;
    private final String publishTopic;

    public MapReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
        this.kafkaTemplate = kafkaTemplate;
        this.publishTopic = publishTopic;
    }

    @Override
    public void run() {
        log.debug("Map UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP Map packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String mapJson = UdpHexDecoder.buildJsonMapFromPacket(packet);
                    if (mapJson != null) {
                        kafkaTemplate.send(publishTopic, mapJson);
                    }
                } else {
                    log.debug("Ignoring empty packet from {}", packet.getSocketAddress());
                }
            } catch (InvalidPayloadException e) {
                log.error("Error decoding packet", e);
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

}

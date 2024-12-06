package us.dot.its.jpo.ode.udp.psm;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class PsmReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher psmPublisher;
    private final String publishTopic;

    public PsmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.psmPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getKafkaType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("PSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP PSM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String psmJson = UdpHexDecoder.buildJsonPsmFromPacket(packet);
                    if (psmJson != null) {
                        psmPublisher.publish(this.publishTopic, psmJson);
                    }
                }
            } catch (InvalidPayloadException e) {
                log.error("Error decoding packet", e);
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

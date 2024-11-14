package us.dot.its.jpo.ode.udp.bsm;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class BsmReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher bsmPublisher;
    private final String publishTopic;

    public BsmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.bsmPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducer().getType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("BSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP BSM packets...");
                this.socket.receive(packet);
                if (packet.getLength() > 0) {
                    String bsmJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);
                    if (bsmJson != null) {
                        bsmPublisher.publish(publishTopic, bsmJson);
                    }
                }
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

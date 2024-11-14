package us.dot.its.jpo.ode.udp.spat;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class SpatReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher spatPublisher;
    private final String publishTopic;

    public SpatReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.spatPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducer().getType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("SPaT UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP SPaT packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String spatJson = UdpHexDecoder.buildJsonSpatFromPacket(packet);
                    if (spatJson != null) {
                        spatPublisher.publish(this.publishTopic, spatJson);
                    }
                }
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
}

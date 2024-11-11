package us.dot.its.jpo.ode.udp.map;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class MapReceiver extends AbstractUdpReceiverPublisher {
    private final StringPublisher mapPublisher;
    private final String publishTopic;

    public MapReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.mapPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducerType(), odeKafkaProperties.getDisabledTopics());
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
                        mapPublisher.publish(this.publishTopic, mapJson);
                    }

                }
            } catch (Exception e) {
                log.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }


}

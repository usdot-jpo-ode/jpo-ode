package us.dot.its.jpo.ode.udp.ssm;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class SsmReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher ssmPublisher;
    private final String publishTopic;

    public SsmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.ssmPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducer().getType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("SSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP SSM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String ssmJson = UdpHexDecoder.buildJsonSsmFromPacket(packet);
                    if (ssmJson != null) {
                        ssmPublisher.publish(this.publishTopic, ssmJson);
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

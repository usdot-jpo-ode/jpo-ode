package us.dot.its.jpo.ode.udp.srm;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class SrmReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher srmPublisher;
    private final String publishTopic;

    public SrmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.srmPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducer().getType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("SRM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                log.debug("Waiting for UDP SRM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String srmJson = UdpHexDecoder.buildJsonSrmFromPacket(packet);
                    if (srmJson != null) {
                        srmPublisher.publish(publishTopic, srmJson);
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

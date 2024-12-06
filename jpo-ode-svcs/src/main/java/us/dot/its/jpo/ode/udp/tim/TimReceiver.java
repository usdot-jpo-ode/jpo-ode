package us.dot.its.jpo.ode.udp.tim;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

import java.net.DatagramPacket;

@Slf4j
public class TimReceiver extends AbstractUdpReceiverPublisher {

    private final StringPublisher timPublisher;
    private final String publishTopic;

    public TimReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.timPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getKafkaType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {
        log.debug("TIM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                log.debug("Waiting for UDP TIM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {

                    String timJson = UdpHexDecoder.buildJsonTimFromPacket(packet);
                    if (timJson != null) {
                        timPublisher.publish(publishTopic, timJson);
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

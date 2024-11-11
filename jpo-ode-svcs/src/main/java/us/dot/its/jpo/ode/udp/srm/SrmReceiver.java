package us.dot.its.jpo.ode.udp.srm;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

public class SrmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SrmReceiver.class);

    private final StringPublisher srmPublisher;
    private final String publishTopic;

    public SrmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties, OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

        this.publishTopic = publishTopic;
        this.srmPublisher = new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducerType(), odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public void run() {

        logger.debug("SRM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP SRM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    
                    String srmJson = UdpHexDecoder.buildJsonSrmFromPacket(packet);
                    if(srmJson != null){
                        srmPublisher.publish(publishTopic, srmJson);
                    }
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

    
}

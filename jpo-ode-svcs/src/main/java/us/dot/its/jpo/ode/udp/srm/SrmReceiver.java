package us.dot.its.jpo.ode.udp.srm;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class SrmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SrmReceiver.class);

    private final StringPublisher srmPublisher;

    @Autowired
    public SrmReceiver(@Qualifier("ode-us.dot.its.jpo.ode.OdeProperties") OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
        this(odeProps, odeKafkaProperties, odeProps.getSrmReceiverPort(), odeProps.getSrmBufferSize());
    }

    public SrmReceiver(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.srmPublisher = new StringPublisher(odeProperties, odeKafkaProperties);
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
                        srmPublisher.publish(srmJson, srmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSRMJson());
                    }
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

    
}

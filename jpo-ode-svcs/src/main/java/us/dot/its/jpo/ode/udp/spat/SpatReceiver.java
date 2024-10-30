package us.dot.its.jpo.ode.udp.spat;

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

public class SpatReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SpatReceiver.class);

    private final StringPublisher spatPublisher;

    @Autowired
    public SpatReceiver(@Qualifier("ode-us.dot.its.jpo.ode.OdeProperties") OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
        this(odeProps, odeKafkaProperties, odeProps.getSpatReceiverPort(), odeProps.getSpatBufferSize());
    }

    public SpatReceiver(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.spatPublisher = new StringPublisher(odeProperties, odeKafkaProperties);
    }

    @Override
    public void run() {

        logger.debug("SPaT UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP SPaT packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    String spatJson = UdpHexDecoder.buildJsonSpatFromPacket(packet);
                    if(spatJson != null){
                        spatPublisher.publish(spatJson,spatPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSPATJson());
                    }
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }


    
}

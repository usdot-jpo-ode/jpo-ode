package us.dot.its.jpo.ode.udp.ssm;

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

public class SsmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SsmReceiver.class);

    private final StringPublisher ssmPublisher;

    @Autowired
    public SsmReceiver(@Qualifier("ode-us.dot.its.jpo.ode.OdeProperties") OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
        this(odeProps, odeKafkaProperties, odeProps.getSsmReceiverPort(), odeProps.getSsmBufferSize());
    }

    public SsmReceiver(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.ssmPublisher = new StringPublisher(odeProperties, odeKafkaProperties);
    }

    @Override
    public void run() {

        logger.debug("SSM UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP SSM packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    
                    String ssmJson = UdpHexDecoder.buildJsonSsmFromPacket(packet);

                    if(ssmJson!=null){
                        ssmPublisher.publish(ssmJson, ssmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedSSMJson());
                    }
                    
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

    
}

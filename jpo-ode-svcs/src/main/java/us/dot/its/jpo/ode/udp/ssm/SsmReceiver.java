package us.dot.its.jpo.ode.udp.ssm;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class SsmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SsmReceiver.class);

    private StringPublisher ssmPublisher;

    @Autowired
    public SsmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getSsmReceiverPort(), odeProps.getSsmBufferSize());

        this.ssmPublisher = new StringPublisher(odeProps);
    }

    public SsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.ssmPublisher = new StringPublisher(odeProps);
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

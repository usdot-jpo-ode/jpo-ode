package us.dot.its.jpo.ode.udp.srm;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class SrmReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SrmReceiver.class);

    private StringPublisher srmPublisher;

    @Autowired
    public SrmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getSrmReceiverPort(), odeProps.getSrmBufferSize());

        this.srmPublisher = new StringPublisher(odeProps);
    }

    public SrmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.srmPublisher = new StringPublisher(odeProps);
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

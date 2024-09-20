package us.dot.its.jpo.ode.udp.spat;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class SpatReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(SpatReceiver.class);

    private StringPublisher spatPublisher;

    @Autowired
    public SpatReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getSpatReceiverPort(), odeProps.getSpatBufferSize());

        this.spatPublisher = new StringPublisher(odeProps);
    }

    public SpatReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.spatPublisher = new StringPublisher(odeProps);
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

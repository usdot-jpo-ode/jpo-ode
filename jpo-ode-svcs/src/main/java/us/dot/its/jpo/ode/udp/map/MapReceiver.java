package us.dot.its.jpo.ode.udp.map;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class MapReceiver extends AbstractUdpReceiverPublisher {
    private static Logger logger = LoggerFactory.getLogger(MapReceiver.class);

    private StringPublisher mapPublisher;

    @Autowired
    public MapReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getMapReceiverPort(), odeProps.getMapBufferSize());

        this.mapPublisher = new StringPublisher(odeProps);
    }

    public MapReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.mapPublisher = new StringPublisher(odeProps);
    }

    @Override
    public void run() {

        logger.debug("Map UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP Map packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    
                    String mapJson = UdpHexDecoder.buildJsonMapFromPacket(packet);
                    if(mapJson != null){
                        mapPublisher.publish(mapJson, mapPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedMAPJson());
                    }
                    
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }


    
}

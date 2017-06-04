package us.dot.its.jpo.ode.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/**
 * Created by anthonychen on 6/1/17.
 */


public class IsdReceiver implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(IsdReceiver.class);
    private static Coder coder = J2735.getPERUnalignedCoder();

    private DatagramSocket socket;

    private OdeProperties odeProperties;

    private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
    private MessageProducer<String, String> isdProducer;
    private ExecutorService execService;

    @Autowired
    public IsdReceiver(OdeProperties odeProps) {

        this.odeProperties = odeProps;

        try {
            socket = new DatagramSocket(odeProperties.getIsdReceiverPort());
            logger.info("Created UDP socket bound to port {}", odeProperties.getIsdReceiverPort());
        } catch (SocketException e) {
            logger.error("Error creating socket with port " + odeProperties.getIsdReceiverPort(), e);
        }


        this.execService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    }

    @Override
    public void run() {

        logger.debug("Isdm Receiver Service started.");

        byte[] buffer = new byte[odeProperties.getIsdBufferSize()];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        Boolean stopped = false;
        while (!stopped) {

            try {
                logger.debug("Waiting for UDP packets...");
                socket.receive(packet);
                logger.debug("Packet received.");
                String obuIp = packet.getAddress().getHostAddress();
                int obuPort = packet.getPort();


                if (packet.getLength() > 0) {
                    IsdDepositor depositor = new IsdDepositor(odeProperties, packet.getData());
                    execService.submit(depositor);
                    publishIsd(packet.getData().toString());
                }



            } catch (IOException e) {
                logger.error("Error receiving packet", e);
//				stopped = true;
            }
        }
    }


    public void publishIsd(String msg) {
        isdProducer.send(odeProperties.getKafkaTopicEncodedIsd(), null, msg);
    }



}

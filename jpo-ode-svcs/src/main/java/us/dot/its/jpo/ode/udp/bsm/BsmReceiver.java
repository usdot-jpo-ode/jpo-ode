package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

    private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

    private OssJ2735Coder asn1Coder;

    @Autowired
    public BsmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
    }

    public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);
        asn1Coder = new OssJ2735Coder();
    }

    @Override
    public void run() {

        logger.debug("UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        do {
            try {
                logger.debug("Waiting for UDP packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    senderIp = packet.getAddress().getHostAddress();
                    senderPort = packet.getPort();
                    logger.debug("Packet received from {}:{}", senderIp, senderPort);

                    // extract the actualPacket from the buffer
                    byte[] payload = Arrays.copyOf(packet.getData(), packet.getLength());
                    publishBsm(payload);
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }

    protected void publishBsm(byte[] data) throws UdpReceiverException {
        try {
            Asn1Object decoded = asn1Coder.decodeUPERBsmBytes(data);

            if (decoded instanceof J2735Bsm) {
                logger.debug("Received J2735Bsm");
                publishBasicSafetyMessage((J2735Bsm) decoded);
            } else {
                logger.error("Unknown message type received {}", data.getClass().getName());
            }
        } catch (OssBsmPart2Exception e) {
            logger.error("Unable to convert BSM", e);
        }
    }

    protected void publishBasicSafetyMessage(J2735Bsm genericBsm) throws OssBsmPart2Exception {
        logger.debug("Publishing BSM to topics {} and {}", odeProperties.getKafkaTopicRawBsmPojo(),
                odeProperties.getKafkaTopicBsmRawJson());

        byteArrayProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null,
                new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) genericBsm));
    }
}

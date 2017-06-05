package us.dot.its.jpo.ode.bsm;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiver2 extends AbstractUdpReceiverPublisher {

	private static Logger logger = LoggerFactory.getLogger(BsmReceiver2.class);

    private MessageProducer<String, String> stringProducer;

	@Autowired
	public BsmReceiver2 (OdeProperties odeProps) {
	    this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
		this.odeProperties = odeProps;

        // Create a String producer for JSON BSMs
        stringProducer = MessageProducer.defaultStringMessageProducer(
                odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());
	}

    public BsmReceiver2 (OdeProperties odeProps, 
                                        int port, 
                                        int bufferSize) {
        super(odeProps, port, bufferSize);
    }
    
    
    @Override
    public void run() {

        logger.debug("UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!isStopped()) {
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
        }
    }

    protected void publishBsm(byte[] data) throws UdpReceiverException {
        try {
            AbstractData decoded = super.decodeData(data);
            
            if (decoded instanceof BasicSafetyMessage) {
                logger.debug("Received BSM");
                publishBasicSafetyMessage((BasicSafetyMessage) decoded);
            } else {
                logger.error("Unknown message type received {}", data.getClass().getName());
            }
        } catch (OssBsmPart2Exception e) {
            logger.error("Unable to convert BSM", e);
        }
    }

    protected void publishBasicSafetyMessage(BasicSafetyMessage decoded) throws OssBsmPart2Exception {
        J2735Bsm genericBsm = OssBsm.genericBsm(decoded);
        
        logger.debug("Publishing BSM to topics {} and {}",
                odeProperties.getKafkaTopicBsmSerializedPojo(), 
                odeProperties.getKafkaTopicBsmRawJson());

        byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
                new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) genericBsm));

        /*
         * TODO ODE-314: This needs to be done in a separate thread consuming from the BSM POJO topic
         * and publishing to BSM JSON topic
         */
        String bsmJson = JsonUtils.toJson(genericBsm, odeProperties.getVerboseJson());
        stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, bsmJson);
    }
}

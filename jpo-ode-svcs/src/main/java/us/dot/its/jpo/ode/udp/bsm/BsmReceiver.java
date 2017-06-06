package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

	private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

    private MessageProducer<String, String> stringProducer;
    private OssAsn1Coder asn1Coder;

	@Autowired
	public BsmReceiver (OdeProperties odeProps) {
	    this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
	}

    public BsmReceiver (OdeProperties odeProps, 
                                        int port, 
                                        int bufferSize) {
        super(odeProps, port, bufferSize);

        // Create a String producer for JSON BSMs
        stringProducer = MessageProducer.defaultStringMessageProducer(
                odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());
        
        asn1Coder = new OssAsn1Coder();
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
                    if(extractBsmMessageFrame(payload) != null)
                    	publishBsm(extractBsmMessageFrame(payload));
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
    
    protected static byte[] extractBsmMessageFrame(byte[] packet){
    	String hexPacket = Hex.encodeHexString(packet);
    	int startIndex = hexPacket.indexOf("0014");
    	String bsmMsgFrameHex = hexPacket.substring(startIndex, hexPacket.length());
    	try {
			return Hex.decodeHex(bsmMsgFrameHex.toCharArray());
		} catch (Exception e) {
			logger.error("Failed to decode bsmMsgFrame", e);
			return null;
		}
    }

    protected void publishBsm(byte[] data) throws UdpReceiverException {
        try {
            Asn1Object decoded = asn1Coder.decodeUPERMessageFrameBytes(data);
            if (decoded instanceof J2735MessageFrame) {
                logger.debug("Received BSM");
                publishBasicSafetyMessage(((J2735MessageFrame) decoded).getValue());
            } else {
                logger.error("Unknown message type received {}", data.getClass().getName());
            }
        } catch (OssBsmPart2Exception e) {
            logger.error("Unable to convert BSM", e);
        }
    }

    protected void publishBasicSafetyMessage(J2735Bsm genericBsm) throws OssBsmPart2Exception {
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

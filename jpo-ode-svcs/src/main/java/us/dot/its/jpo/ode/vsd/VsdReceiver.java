package us.dot.its.jpo.ode.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class VsdReceiver extends AbstractUdpReceiverPublisher {

	private static Logger logger = LoggerFactory.getLogger(VsdReceiver.class);

	private OdeProperties odeProperties;

    private MessageProducer<String, byte[]> byteArrayProducer;
    private MessageProducer<String, String> stringProducer;

	@Autowired
	public VsdReceiver(OdeProperties odeProps) {
        super(odeProps.getVsdReceiverPort(), odeProps.getVsdBufferSize());

		this.odeProperties = odeProps;

		// Create a String producer for hex VSDs
		stringProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
				odeProperties.getKafkaProducerType());

        // Create a ByteArray producer for UPER VSDs
        byteArrayProducer = MessageProducer.defaultByteArrayMessageProducer(odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());
	}

    @Override
    public void run() {

        logger.debug("Starting {}...", this.getClass().getSimpleName());

        byte[] buffer = new byte[odeProperties.getVsdBufferSize()];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        Boolean stopped = false;
        while (!stopped) {

            try {
                logger.debug("Waiting for UDP packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    senderIp = packet.getAddress().getHostAddress();
                    senderPort = packet.getPort();
                    logger.debug("Packet received from {}:{}", senderIp, senderPort);
                    
                    //extract the actualPacket from the buffer
                    byte[] payload = Arrays.copyOf(packet.getData(), packet.getLength());
                    publish(payload);
                }
            } catch (IOException e) {
                logger.error("Error receiving packet", e);
            } catch (UdpReceiverException e) {
                logger.error("Error decoding packet", e);
            }
        }
    }

    @Override
    protected void publish(byte[] data) throws UdpReceiverException {
        AbstractData decoded = super.decodeData(data);
        try {
            if (decoded instanceof ServiceRequest) {
                logger.debug("Received ServiceRequest:\n{} \n", decoded.toString());
                ServiceRequest request = (ServiceRequest) decoded;
                TrustManager tm = new TrustManager(odeProperties, socket);
                tm.sendServiceResponse(tm.createServiceResponse(request), senderIp, senderPort);
            } else if (decoded instanceof VehSitDataMessage) {
                logger.debug("Received VSD");
                publishVsd(data);
                extractAndPublishBsms((VehSitDataMessage) decoded);
            } else {
                logger.error("Unknown message type received {}", decoded.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error decoding message", e);
        }
    }

    protected void extractAndPublishBsms(AbstractData data) {
        VehSitDataMessage msg = (VehSitDataMessage) data;
        List<BasicSafetyMessage> bsmList = null;
        try {
            bsmList = VsdToBsmConverter.convert(msg);
        } catch (IllegalArgumentException e) {
            logger.error("Unable to convert VehSitDataMessage bundle to BSM list", e);
            return;
        }

        int i = 1;
        for (BasicSafetyMessage entry : bsmList) {
            try {
                J2735Bsm convertedBsm = OssBsm.genericBsm(entry);
                publishBsm(convertedBsm);
                
                String bsmJson = JsonUtils.toJson(convertedBsm, odeProperties.getVerboseJson());
                publishBsm(bsmJson);
                logger.debug("Published bsm {} to the topics {} and {}", 
                        i++, 
                        odeProperties.getKafkaTopicBsmSerializedPojo(),
                        odeProperties.getKafkaTopicBsmRawJson());
            } catch (OssBsmPart2Exception e) {
                logger.error("Unable to convert BSM", e);
            }
        }
    }

    
	public void publishBsm(String msg) {
		stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);
	}

	public void publishBsm(Asn1Object msg) {
        logger.debug("Publishing BSM to topic {}", odeProperties.getKafkaTopicBsmSerializedPojo());
		byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
				new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) msg));
	}

	private void publishVsd(byte[] data) {
        logger.debug("Publishing VSD to topic {}", odeProperties.getKafkaTopicVsd());
	    byteArrayProducer.send(odeProperties.getKafkaTopicVsd(), null, data);
	}

}

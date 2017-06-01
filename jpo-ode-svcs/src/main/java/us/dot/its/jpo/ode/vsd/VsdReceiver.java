package us.dot.its.jpo.ode.vsd;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private ExecutorService execService;

	@Autowired
	public VsdReceiver(OdeProperties odeProps) {
        super(odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());

		this.odeProperties = odeProps;

		// Create a String producer for hex BSMs
		stringProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
				odeProperties.getKafkaProducerType());

        // Create a ByteArray producer for UPER BSMs and VSDs
        byteArrayProducer = MessageProducer.defaultByteArrayMessageProducer(odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());

		this.execService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	}

    @Override
    protected void publish(AbstractData data) {
        try {
            extractAndPublishBsms((VehSitDataMessage) data);
            
            if (data instanceof BasicSafetyMessage) {
                logger.debug("Received BSM");
                J2735Bsm genericBsm = OssBsm.genericBsm((BasicSafetyMessage) data);
                
                logger.debug("Publishing BSM to topic {}", 
                        odeProperties.getKafkaTopicBsmSerializedPojo());
                byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
                        new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) genericBsm));

                String bsmJson = JsonUtils.toJson(genericBsm, odeProperties.getVerboseJson());
                stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, bsmJson);
                logger.debug("Published bsm to the topics {} and {}",
                        odeProperties.getKafkaTopicBsmSerializedPojo(), 
                        odeProperties.getKafkaTopicBsmRawJson());
            } else {
                logger.error("Unknown message type received {}", data.getClass().getName());
            }
        } catch (OssBsmPart2Exception e) {
            logger.error("Unable to convert BSM", e);
        }
    }

    
    @Override
    protected AbstractData decodeData(byte[] msg, String obuIp, int obuPort) 
            throws UdpReceiverException {
        AbstractData decoded = super.decodeData(msg, obuIp, obuPort);
		try {
			if (decoded instanceof ServiceRequest) {
				logger.debug("Received ServiceRequest:\n{} \n", decoded.toString());
				ServiceRequest request = (ServiceRequest) decoded;
				ReqResForwarder forwarder = new ReqResForwarder(odeProperties, request, obuIp, obuPort);
				execService.submit(forwarder);
			} else if (decoded instanceof VehSitDataMessage) {
				logger.debug("Received VSD");
	            publishVsd(msg);
			} else {
				logger.error("Unknown message type received {}", decoded.getClass().getName());
			}
		} catch (Exception e) {
			logger.error("Error decoding message", e);
		}
        return decoded;
	}

	private void extractAndPublishBsms(VehSitDataMessage msg) {
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

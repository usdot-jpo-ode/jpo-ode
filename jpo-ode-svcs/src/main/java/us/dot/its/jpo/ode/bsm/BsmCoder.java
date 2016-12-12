package us.dot.its.jpo.ode.bsm;

import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.OdeSvcsApplication;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@Service
public class BsmCoder {
	
	public static final String BSM_ASN1_ENCODED_MESSAGES = "BSM_ASN1_ENCODED_MESSAGES";

	public static final String BSM_OBJECTS = "BSM_OBJECTS";

	private static Logger logger = LoggerFactory.getLogger(BsmCoder.class);

	private OdeProperties odeProperties;
	private Asn1Plugin asn1Coder;

	
	public BsmCoder(OdeProperties properties) {
		super();
		this.odeProperties = properties;
		if (this.asn1Coder == null) {
			logger.info("Loading ASN1 Coder: {}", this.odeProperties.getAsn1CoderClassName());
			try {
				this.asn1Coder = (Asn1Plugin) PluginFactory.getPluginByName(properties.getAsn1CoderClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.error("Unable to load plugin: " + properties.getAsn1CoderClassName(), e);
			}
		}

	}

	public String encodefromJson(InputStream is) {
		String line = null;
		try (Scanner scanner = new Scanner(is)){
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				Asn1Object bsm;
				String encoded;
				try {
					bsm = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);
					encoded = asn1Coder.UPER_EncodeHex(bsm);
					logger.debug("Encoded: {}", encoded);
				} catch (Exception e) {
					logger.warn("Message is not JSON: " + line, e);
				}

			}
		} catch (Exception e) {
			logger.error("Error encoding data: " + line, e);
			throw e;
		}
		return line;
	}

	public J2735Bsm decodeFromHex(InputStream is)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String line = null;
		J2735Bsm decoded = null;
		try (Scanner scanner = new Scanner(is)){
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();

				decoded  = (J2735Bsm) asn1Coder.UPER_DecodeHex(line);
				logger.debug("Decoded: {}", decoded);
			}
		} catch (Exception e) {
			logger.error("Error decoding data: " + line, e);;
		}
		return decoded;

	}

	public void publish(String topic, J2735Bsm msg) {
		publish(topic, SerializationUtils.serialize(msg));
	}

	public void publish(String topic, String msg) {
		publish(topic, SerializationUtils.serialize(msg));
	}

	public void publish(String topic, byte[] msg) {
		MessageProducer<String, byte[]> producer = 
				(MessageProducer<String, byte[]>) OdeSvcsApplication.getMessageProducerPool().checkOut();
		producer.send(topic, null, msg);
		logger.debug("Published: {}", msg.toString());
		OdeSvcsApplication.getMessageProducerPool().checkIn(producer);
	}

	public void subscribe() {
		MessageConsumer<String, byte[]> consumer = OdeSvcsApplication.getMessageConsumerPool().checkOut();
		consumer.subscribe(BSM_OBJECTS); // Subscribe to the topic name
		OdeSvcsApplication.getMessageConsumerPool().checkIn(consumer);
	}

}

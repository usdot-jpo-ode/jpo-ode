package us.dot.its.jpo.ode.services.asn1.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;

public abstract class AbstractAsn1DecodeMessageJSON extends AbstractSubscriberProcessor<String, String> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected StringPublisher codecPublisher;

	public AbstractAsn1DecodeMessageJSON() {
		super();
	}

	public AbstractAsn1DecodeMessageJSON(StringPublisher codecPublisher) {
		super();
		this.codecPublisher = codecPublisher;
	}

	protected void publishEncodedMessageToAsn1Decoder(OdeData odeData) {
		XmlUtils xmlUtils = new XmlUtils();
		try {
			logger.debug("Sending encoded message payload XML to ASN1 codec {}", xmlUtils.toXml(odeData));
			codecPublisher.publish(xmlUtils.toXml(odeData),
					codecPublisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
		} catch (JsonProcessingException e) {
			logger.error("Error sending encoded message payload XML to ASN1 codec {}", e.getMessage());
			e.printStackTrace();
		}

	}
}

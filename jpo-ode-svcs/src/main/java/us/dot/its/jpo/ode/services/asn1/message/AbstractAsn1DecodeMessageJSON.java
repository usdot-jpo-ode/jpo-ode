package us.dot.its.jpo.ode.services.asn1.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;

@Slf4j
public abstract class AbstractAsn1DecodeMessageJSON extends AbstractSubscriberProcessor<String, String> {

	protected final String publishTopic;
	protected StringPublisher codecPublisher;
    protected String payloadStartFlag;

	protected AbstractAsn1DecodeMessageJSON(String publishTopic, StringPublisher codecPublisher, String payloadStartFlag) {
		super();
		this.publishTopic = publishTopic;
		this.payloadStartFlag = payloadStartFlag;
		this.codecPublisher = codecPublisher;
	}

	protected void publishEncodedMessageToAsn1Decoder(OdeData odeData) {
		XmlUtils xmlUtils = new XmlUtils();
		try {
			log.debug("Sending encoded message payload XML to ASN1 codec {}", xmlUtils.toXml(odeData));
			codecPublisher.publish(publishTopic, xmlUtils.toXml(odeData)
            );
		} catch (JsonProcessingException e) {
			log.error("Error sending encoded message payload XML to ASN1 codec {}", e.getMessage(), e);
		}

	}
}

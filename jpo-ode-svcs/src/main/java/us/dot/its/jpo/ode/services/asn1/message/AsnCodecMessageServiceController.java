package us.dot.its.jpo.ode.services.asn1.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/***
 * Launch sending encoded message service
 */
@Controller
@Slf4j
public class AsnCodecMessageServiceController {

	@Autowired
	public AsnCodecMessageServiceController(OdeKafkaProperties odeKafkaProperties, RawEncodedJsonTopics rawEncodedJsonTopics, Asn1CoderTopics asn1CoderTopics) {
		super();
		log.info("Starting {} ", this.getClass().getSimpleName());

		// asn1_codec Decoder Routing
		log.info("Send encoded BSM to ASN.1 Decoder");
		Asn1DecodeBSMJSON asn1DecodeBSMJSON = new Asn1DecodeBSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

		MessageConsumer<String, String> asn1RawBSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeBSMJSON);
		asn1RawBSMJSONConsumer.setName("asn1DecodeBSMJSON");
		asn1DecodeBSMJSON.start(asn1RawBSMJSONConsumer, rawEncodedJsonTopics.getBsm());

		log.info("Send encoded SPAT to ASN.1 Decoder");
		Asn1DecodeSPATJSON asn1DecodeSPATJSON = new Asn1DecodeSPATJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

		MessageConsumer<String, String> asn1RawSPATJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSPATJSON);
		asn1RawSPATJSONConsumer.setName("asn1DecodeSPATJSON");
		asn1DecodeSPATJSON.start(asn1RawSPATJSONConsumer, rawEncodedJsonTopics.getSpat());

		log.info("Send encoded SSM to ASN.1 Decoder");
		Asn1DecodeSSMJSON asn1DecodeSSMJSON = new Asn1DecodeSSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

		MessageConsumer<String, String> asn1RawSSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSSMJSON);
		asn1RawSSMJSONConsumer.setName("asn1DecodeSSMJSON");
		asn1DecodeSSMJSON.start(asn1RawSSMJSONConsumer, rawEncodedJsonTopics.getSsm());

		log.info("Send encoded SRM to ASN.1 Decoder");
		Asn1DecodeSRMJSON asn1DecodeSRMJSON = new Asn1DecodeSRMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

		MessageConsumer<String, String> asn1RawSRMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSRMJSON);
		asn1RawSRMJSONConsumer.setName("asn1DecodeSRMJSON");
		asn1DecodeSRMJSON.start(asn1RawSRMJSONConsumer, rawEncodedJsonTopics.getSrm());

		log.info("Send encoded TIM to ASN.1 Decoder");
		Asn1DecodeTIMJSON asn1DecodeTIMJSON = new Asn1DecodeTIMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

		MessageConsumer<String, String> asn1RawTIMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeTIMJSON);
		asn1RawTIMJSONConsumer.setName("asn1DecodeTIMJSON");
		asn1DecodeTIMJSON.start(asn1RawTIMJSONConsumer, rawEncodedJsonTopics.getTim());

		log.info("Send encoded PSM to ASN.1 Decoder");
		Asn1DecodePSMJSON asn1DecodePSMSON = new Asn1DecodePSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());
		MessageConsumer<String, String> asn1RawPSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodePSMSON);
		asn1RawPSMJSONConsumer.setName("asn1DecodePSMJSON");				      
		asn1DecodePSMSON.start(asn1RawPSMJSONConsumer, rawEncodedJsonTopics.getPsm());
	}
}

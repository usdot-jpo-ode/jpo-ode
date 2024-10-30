package us.dot.its.jpo.ode.services.asn1.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/***
 * Launch sending encoded message service
 */
@Controller
public class AsnCodecMessageServiceController {

	private static final Logger logger = LoggerFactory.getLogger(AsnCodecMessageServiceController.class);

	@Autowired
	public AsnCodecMessageServiceController(@Qualifier("ode-us.dot.its.jpo.ode.OdeProperties") OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
		super();
		logger.info("Starting {} ", this.getClass().getSimpleName());

		// asn1_codec Decoder Routing
		// BSM
		logger.info("Send encoded BSM to ASN.1 Decoder");
		Asn1DecodeBSMJSON asn1DecodeBSMJSON = new Asn1DecodeBSMJSON(odeProps, odeKafkaProperties);

		MessageConsumer<String, String> asn1RawBSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeBSMJSON);
		asn1RawBSMJSONConsumer.setName("asn1DecodeBSMJSON");
		asn1DecodeBSMJSON.start(asn1RawBSMJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedBSMJson());

		// SPAT
		logger.info("Send encoded SPAT to ASN.1 Decoder");
		Asn1DecodeSPATJSON asn1DecodeSPATJSON = new Asn1DecodeSPATJSON(odeProps, odeKafkaProperties);

		MessageConsumer<String, String> asn1RawSPATJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSPATJSON);
		asn1RawSPATJSONConsumer.setName("asn1DecodeSPATJSON");
		asn1DecodeSPATJSON.start(asn1RawSPATJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedSPATJson());

		// SSM
		logger.info("Send encoded SSM to ASN.1 Decoder");
		Asn1DecodeSSMJSON asn1DecodeSSMJSON = new Asn1DecodeSSMJSON(odeProps, odeKafkaProperties);

		MessageConsumer<String, String> asn1RawSSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSSMJSON);
		asn1RawSSMJSONConsumer.setName("asn1DecodeSSMJSON");
		asn1DecodeSSMJSON.start(asn1RawSSMJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedSSMJson());

		// SRM
		logger.info("Send encoded SRM to ASN.1 Decoder");
		Asn1DecodeSRMJSON asn1DecodeSRMJSON = new Asn1DecodeSRMJSON(odeProps, odeKafkaProperties);

		MessageConsumer<String, String> asn1RawSRMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeSRMJSON);
		asn1RawSRMJSONConsumer.setName("asn1DecodeSRMJSON");
		asn1DecodeSRMJSON.start(asn1RawSRMJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedSRMJson());

		// TIM
		logger.info("Send encoded TIM to ASN.1 Decoder");
		Asn1DecodeTIMJSON asn1DecodeTIMJSON = new Asn1DecodeTIMJSON(odeProps, odeKafkaProperties);

		MessageConsumer<String, String> asn1RawTIMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeTIMJSON);
		asn1RawTIMJSONConsumer.setName("asn1DecodeTIMJSON");
		asn1DecodeTIMJSON.start(asn1RawTIMJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedTIMJson());

		//MAP
		logger.info("Send encoded MAP to ASN.1 Decoder");
		Asn1DecodeMAPJSON asn1DecodeMAPSON = new Asn1DecodeMAPJSON(odeProps, odeKafkaProperties);
		MessageConsumer<String, String> asn1RawMAPJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodeMAPSON);
		asn1RawMAPJSONConsumer.setName("asn1DecodeMAPJSON");				      
		asn1DecodeMAPSON.start(asn1RawMAPJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedMAPJson());

		//PSM
		logger.info("Send encoded PSM to ASN.1 Decoder");
		Asn1DecodePSMJSON asn1DecodePSMSON = new Asn1DecodePSMJSON(odeProps, odeKafkaProperties);
		MessageConsumer<String, String> asn1RawPSMJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
				odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), asn1DecodePSMSON);
		asn1RawPSMJSONConsumer.setName("asn1DecodePSMJSON");				      
		asn1DecodePSMSON.start(asn1RawPSMJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedPSMJson());
	}
}

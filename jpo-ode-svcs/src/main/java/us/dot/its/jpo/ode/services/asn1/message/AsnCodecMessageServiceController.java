package us.dot.its.jpo.ode.services.asn1.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/***
 * Launch sending encoded message service
 * */
@Controller
public class AsnCodecMessageServiceController {

	   private static final Logger logger = LoggerFactory.getLogger(AsnCodecMessageServiceController.class);
	   
	   @Autowired
	   public AsnCodecMessageServiceController(OdeProperties odeProps)
	   {
		   super();
		   logger.info("Starting {} ",this.getClass().getSimpleName());

		      // asn1_codec Decoder Routing
		      logger.info("Send encoded message to ASN.1 Decoder");

		      Asn1DecodeMessageJSON asn1DecodeMessageJSON = new Asn1DecodeMessageJSON(odeProps);

		      MessageConsumer<String, String> asn1RawMessageJSONConsumer = MessageConsumer.defaultStringMessageConsumer(
		         odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), asn1DecodeMessageJSON);
		      asn1RawMessageJSONConsumer.setName("asn1DecodeMessageJSON");
		      
		      asn1DecodeMessageJSON.start(asn1RawMessageJSONConsumer, odeProps.getKafkaTopicOdeRawEncodedMessageJson());

	   }
}

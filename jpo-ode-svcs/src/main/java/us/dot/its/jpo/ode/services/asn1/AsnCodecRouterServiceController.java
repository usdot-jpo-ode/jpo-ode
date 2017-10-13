package us.dot.its.jpo.ode.services.asn1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class AsnCodecRouterServiceController {

   private static final Logger logger = LoggerFactory.getLogger(AsnCodecRouterServiceController.class);
   org.apache.kafka.common.serialization.Serdes bas;

   @Autowired
   public AsnCodecRouterServiceController(OdeProperties odeProps) {
      super();

      logger.info("Starting {}", this.getClass().getSimpleName());

      // asn1_codec Routing
      logger.info("Routing DECODED data received ASN.1 Decoder");

      Asn1CodecRouter asn1codecRouter = new Asn1CodecRouter(odeProps);

      MessageConsumer<String, String> asn1DecoderConsumer = MessageConsumer.defaultStringMessageConsumer(
         odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), asn1codecRouter);

      asn1DecoderConsumer.setName("Asn1DecoderConsumer");
      asn1codecRouter.start(asn1DecoderConsumer, odeProps.getKafkaTopicAsn1DecoderOutput());

   }
}

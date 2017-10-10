package us.dot.its.jpo.ode.services.asn1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class AsnCodecToOdeBsmServiceController {

   private static final Logger logger = LoggerFactory.getLogger(AsnCodecToOdeBsmServiceController.class);
   org.apache.kafka.common.serialization.Serdes bas;

   @Autowired
   public AsnCodecToOdeBsmServiceController(OdeProperties odeProps) {
      super();

      logger.info("Starting {}", this.getClass().getSimpleName());

      // asn1_codec BSM --> OdeBsmData convert
      logger.info("Converting {} records from topic {} and publishing to topic {} ", OdeBsmData.class.getSimpleName(),
            odeProps.getKafkaTopicOdeBsmPojo(), odeProps.getKafkaTopicOdeBsmJson());

      Asn1CodecBsmToOdeBsmTransformer asn1codecBsmConverter = new Asn1CodecBsmToOdeBsmTransformer(
         new MessageProducer<String, OdeBsmData>(
               odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType(), null, OdeBsmSerializer.class.getName()),
         odeProps.getKafkaTopicOdeBsmPojo());

      MessageConsumer<String, String> asn1codecBsmConsumer = MessageConsumer.defaultStringMessageConsumer(
         odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), asn1codecBsmConverter);

      asn1codecBsmConsumer.setName("asn1codecBsmConsumer");
      asn1codecBsmConverter.start(asn1codecBsmConsumer, odeProps.getKafkaTopicAsn1DecodedBsm());

   }
}

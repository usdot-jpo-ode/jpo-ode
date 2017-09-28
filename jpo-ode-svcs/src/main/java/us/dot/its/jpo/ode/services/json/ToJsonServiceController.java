package us.dot.its.jpo.ode.services.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeTimData;
// TODO Deprecate per ODE-436
// vvvvvvvvvvvvvvvvvvvvvvvvvv
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
// ^^^^^^^^^^^^^^^^^^^^^^^^^^
// TODO Deprecate per ODE-436
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.serdes.J2735BsmDeserializer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmDeserializer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimDeserializer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class ToJsonServiceController {

   private static final Logger logger = LoggerFactory.getLogger(ToJsonServiceController.class);
   org.apache.kafka.common.serialization.Serdes bas;

   @Autowired
   public ToJsonServiceController(OdeProperties odeProps) {
      super();

      logger.info("Starting {}", this.getClass().getSimpleName());

      // TODO Deprecate per ODE-436
      // vvvvvvvvvvvvvvvvvvvvvvvvvv
      logger.info("Converting {} records from topic {} and publishing to topic {} ", J2735Bsm.class.getSimpleName(),
            odeProps.getKafkaTopicRawBsmPojo(), odeProps.getKafkaTopicRawBsmJson());

      ToJsonConverter<J2735Bsm> j2735BsmConverter = new ToJsonConverter<>(odeProps, false,
            odeProps.getKafkaTopicRawBsmJson());

      MessageConsumer<String, J2735Bsm> j2735BsmConsumer = new MessageConsumer<>(
            odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), j2735BsmConverter,
            J2735BsmDeserializer.class.getName());

      j2735BsmConsumer.setName("j2735BsmConsumer");
      j2735BsmConverter.start(j2735BsmConsumer, odeProps.getKafkaTopicRawBsmPojo());
      // ^^^^^^^^^^^^^^^^^^^^^^^^^^
      // TODO Deprecate per ODE-436

      // BSM POJO --> JSON converter
      logger.info("Converting {} records from topic {} and publishing to topic {} ", OdeBsmData.class.getSimpleName(),
            odeProps.getKafkaTopicOdeBsmPojo(), odeProps.getKafkaTopicOdeBsmJson());

      ToJsonConverter<OdeBsmData> odeBsmConverter = new ToJsonConverter<>(odeProps, false,
            odeProps.getKafkaTopicOdeBsmJson());

      MessageConsumer<String, OdeBsmData> odeBsmConsumer = new MessageConsumer<>(
            odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), odeBsmConverter,
            OdeBsmDeserializer.class.getName());

      odeBsmConsumer.setName("odeBsmConsumer");
      odeBsmConverter.start(odeBsmConsumer, odeProps.getKafkaTopicOdeBsmPojo());

      // TIM POJO --> JSON converter
      logger.info("Converting {} records from topic {} and publishing to topic {} ",
            OdeTimData.class.getSimpleName(), odeProps.getKafkaTopicOdeTimPojo(),
            odeProps.getKafkaTopicOdeTimJson());
      
      ToJsonConverter<OdeTimData> odeTimConverter = new ToJsonConverter<>(odeProps, false,
            odeProps.getKafkaTopicOdeTimJson());
      
      MessageConsumer<String, OdeTimData> odeTimConsumer = new MessageConsumer<>(
            odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), odeTimConverter,
            OdeTimDeserializer.class.getName());
      
      odeTimConsumer.setName("odeTimConsumer");
      odeTimConverter.start(odeTimConsumer, odeProps.getKafkaTopicOdeTimPojo());

   }
}

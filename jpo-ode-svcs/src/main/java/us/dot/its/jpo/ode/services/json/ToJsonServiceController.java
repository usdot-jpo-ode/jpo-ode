package us.dot.its.jpo.ode.services.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
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

      // BSM POJO --> JSON converter
      logger.info("Converting {} records from topic {} and publishing to topic {} ", OdeBsmData.class.getSimpleName(),
            odeProps.getKafkaTopicOdeBsmPojo(), odeProps.getKafkaTopicOdeBsmJson());

      ToJsonConverter<OdeBsmData> odeBsmConverter = new ToJsonConverter<>(odeProps, false,
            odeProps.getKafkaTopicOdeBsmJson());

      MessageConsumer<String, OdeBsmData> odeBsmConsumer = new MessageConsumer<>(
            odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), odeBsmConverter,
            OdeBsmDeserializer.class.getName());

      odeBsmConsumer.setName(this.getClass().getName() + "#odeBsmConsumer");
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

      // Distress POJO --> JSON converter
      logger.info("Converting {} records from topic {} and publishing to topic {} ",
              OdeTimData.class.getSimpleName(), odeProps.getKafkaTopicOdeDNMsgPojo(),
              odeProps.getKafkaTopicOdeDNMsgJson());

      ToJsonConverter<OdeTimData> odeDNMsgConverter = new ToJsonConverter<>(odeProps, false,
              odeProps.getKafkaTopicOdeDNMsgJson());

      MessageConsumer<String, OdeTimData> odeDNMsgConsumer = new MessageConsumer<>(
              odeProps.getKafkaBrokers(), this.getClass().getSimpleName(), odeDNMsgConverter,
              OdeTimDeserializer.class.getName());

      odeDNMsgConsumer.setName("odeDNMsgConsumer");
      odeDNMsgConverter.start(odeDNMsgConsumer, odeProps.getKafkaTopicOdeDNMsgPojo());

   }
}

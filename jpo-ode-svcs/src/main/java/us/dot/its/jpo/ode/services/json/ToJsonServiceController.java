package us.dot.its.jpo.ode.services.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmDeserializer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimDeserializer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class ToJsonServiceController {

   private static final Logger logger = LoggerFactory.getLogger(ToJsonServiceController.class);
   
   private OdeProperties odeProperties;

   @Autowired
   public ToJsonServiceController(OdeProperties odeProps) {
      super();
      
      this.odeProperties = odeProps;

      logger.info("Starting {}", this.getClass().getSimpleName());

      // BSM POJO --> JSON converter
      launchConverter(odeProps.getKafkaTopicOdeBsmPojo(), OdeBsmDeserializer.class.getName(),
         new ToJsonConverter<>(odeProps, false, odeProps.getKafkaTopicOdeBsmJson()));

      // TIM POJO --> JSON converter
      launchConverter(odeProps.getKafkaTopicOdeTimPojo(), OdeTimDeserializer.class.getName(),
         new ToJsonConverter<>(odeProps, false, odeProps.getKafkaTopicOdeTimJson()));

      // Broadcast TIM POJO --> Broadcast TIM JSON converter
      launchConverter(odeProps.getKafkaTopicOdeTimBroadcastPojo(), OdeTimDeserializer.class.getName(),
         new ToJsonConverter<>(odeProps, false, odeProps.getKafkaTopicOdeTimBroadcastJson()));
   }

   private <V> void launchConverter(String fromTopic, String serializerFQN, 
      ToJsonConverter<V> jsonConverter) {
      logger.info("Converting records from topic {} and publishing to topic {} ", 
         fromTopic, jsonConverter.getOutputTopic());

      MessageConsumer<String, V> consumer = new MessageConsumer<String, V>(
            odeProperties.getKafkaBrokers(), this.getClass().getSimpleName(), 
            jsonConverter, serializerFQN);

      consumer.setName(this.getClass().getName() + fromTopic + "Consumer");
      jsonConverter.start(consumer, fromTopic);
   }
}

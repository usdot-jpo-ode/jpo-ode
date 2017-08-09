package us.dot.its.jpo.ode.services.vsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@Controller
public class BsmToVsdPackagerController {

   private static final Logger logger = LoggerFactory.getLogger(BsmToVsdPackagerController.class);

   @Autowired
   protected BsmToVsdPackagerController(OdeProperties odeProps) {
      super();

      // TODO use filtered topic
      // String inputTopic = odeProps.getKafkaTopicFilteredOdeBsmJson();
      
      String inputTopic = odeProps.getKafkaTopicOdeBsmPojo();
      String outputTopic = odeProps.getKafkaTopicEncodedVsd();

      if (odeProps.isEnabledVsdKafkaTopic()) {
         logger.info("Converting {} records from topic {} and publishing to topic {} ", J2735Bsm.class.getSimpleName(),
               inputTopic, outputTopic);

         BsmToVsdPackager converter = new BsmToVsdPackager(MessageProducer.defaultByteArrayMessageProducer(
               odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType()), outputTopic);

         MessageConsumer<String, String> consumer = new MessageConsumer<String, String>(odeProps.getKafkaBrokers(),
               this.getClass().getSimpleName(), converter, MessageConsumer.SERIALIZATION_STRING_DESERIALIZER);

         consumer.setName(BsmToVsdPackager.class.getSimpleName());
         converter.start(consumer, inputTopic);
      } else {
         logger.warn("WARNING - VSD Kafka topic disabled, BSM-to-VSD packager not started.");
      }
   }
}

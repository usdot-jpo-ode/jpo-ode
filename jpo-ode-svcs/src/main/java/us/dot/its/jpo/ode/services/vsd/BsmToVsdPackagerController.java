package us.dot.its.jpo.ode.services.vsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.OdeBsmDeserializer;

@Controller
public class BsmToVsdPackagerController {

   private static final Logger logger = LoggerFactory.getLogger(BsmToVsdPackagerController.class);

   @Autowired
   protected BsmToVsdPackagerController(OdeProperties odeProps) {
      super();

      // TODO use filtered topic
      // String inputTopic = odeProps.getKafkaTopicFilteredOdeBsmJson();
      String inputTopic = odeProps.getKafkaTopicOdeBsmJson();

      String outputTopic = odeProps.getKafkaTopicEncodedVsd();

      if (odeProps.isEnabledVsdKafkaTopic()) {
         logger.info("Converting {} records from topic {} and publishing to topic {} ",
               OdeBsmData.class.getSimpleName(), inputTopic, outputTopic);

         BsmToVsdPackager converter = new BsmToVsdPackager(MessageProducer.defaultByteArrayMessageProducer(
               odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType()), outputTopic);

         MessageConsumer<String, String> consumer = new MessageConsumer<String, String>(odeProps.getKafkaBrokers(),
               this.getClass().getSimpleName(), converter, OdeBsmDeserializer.class.getName());

         consumer.setName(BsmToVsdPackager.class.getSimpleName());
         converter.start(consumer, inputTopic);
      } else {
         logger.warn("WARNING - VSD Kafka topic disabled, BSM-to-VSD packager not started.");
      }
   }
}

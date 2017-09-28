package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
   protected MessageProducer<String, OdeObject> objectProducer;
   private String kafkaTopic;

   public MessagePublisher(OdeProperties odeProps, String kafkaTopic, Class<?> msgSerializerClass) {
      this.kafkaTopic = kafkaTopic;
      this.objectProducer = new MessageProducer<>(odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType(), null,
            msgSerializerClass.getName());
   }

   public void publish(OdeData msg) {
      logger.debug("Publishing to {}: {}", kafkaTopic, msg);
      objectProducer.send(kafkaTopic, null, msg);
   }
}

package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class OdeStringPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(OdeDataPublisher.class);
   protected MessageProducer<String, String> stringProducer;

   public OdeStringPublisher(OdeProperties odeProps) {
      super(odeProps);
      this.stringProducer = MessageProducer.defaultStringMessageProducer(
         odeProperties.getKafkaBrokers(),
         odeProperties.getKafkaProducerType(), 
         odeProperties.getKafkaTopicsDisabledSet());

   }

   public void publish(OdeData msg, String topic) {
      logger.debug("Publishing to {}: {}", topic, msg);
      stringProducer.send(topic, null, msg.toJson());
   }
}

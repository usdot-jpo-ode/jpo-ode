package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class StringPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(StringPublisher.class);
   protected MessageProducer<String, String> stringProducer;

   public StringPublisher(OdeProperties odeProps) {
      super(odeProps);
      this.stringProducer = MessageProducer.defaultStringMessageProducer(
         odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(), 
         odeProperties.getKafkaTopicsDisabledSet());

   }

   public void publish(String msg, String topic) {
    logger.debug("Publishing String data to {}", topic);
    stringProducer.send(topic, null, msg);
   }

}

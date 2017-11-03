package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;

public abstract class MessagePublisher {

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   protected OdeProperties odeProperties;

   public MessagePublisher(OdeProperties odeProps) {
      this.odeProperties = odeProps;
      logger.info("Using Brokers: {} of Type: ", odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType());
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }   
}

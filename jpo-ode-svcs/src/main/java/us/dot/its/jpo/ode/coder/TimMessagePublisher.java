package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTravelerInformationMessageSerializer;

public class TimMessagePublisher {
   
   private static final Logger logger = LoggerFactory.getLogger(TimMessagePublisher.class);
   private OdeProperties odeProperties;
   protected MessageProducer<String, OdeObject> objectProducer;

   public TimMessagePublisher(OdeProperties odeProps) {
      this.odeProperties = odeProps;
      this.objectProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, OdeTravelerInformationMessageSerializer.class.getName());

   }

   public void publish(OdeData msg) {
      OdeTimData odeTim = (OdeTimData) msg;
      
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeTimPojo(), odeTim);
      objectProducer.send(odeProperties.getKafkaTopicOdeTimPojo(), null, odeTim);
   }

}

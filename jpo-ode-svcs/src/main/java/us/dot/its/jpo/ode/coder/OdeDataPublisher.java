package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class OdeDataPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(OdeDataPublisher.class);
   protected MessageProducer<String, OdeObject> objectProducer;

   public OdeDataPublisher(OdeProperties odeProps, String serializer) {
      super(odeProps);
      this.objectProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, serializer);
   }

   public void publish(OdeData msg, String topic) {
//      if (msg.getMetadata() != null && msg.getMetadata().getReceivedAt() != null)
//         try {
//            long latency = DateTimeUtils.difference(DateTimeUtils.isoDateTime(msg.getMetadata().getReceivedAt()),
//                  ZonedDateTime.now());
//            odeBsm.getMetadata().setLatency(latency);
//         } catch (ParseException e) {
//            logger.error("Error converting ISO timestamp", e);
//         }
      
      logger.debug("Publishing to {}: {}", topic, msg);
      objectProducer.send(topic, null, msg);
   }

}

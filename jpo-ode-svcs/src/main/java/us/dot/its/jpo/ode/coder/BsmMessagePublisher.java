package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;

public class BsmMessagePublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(BsmMessagePublisher.class);
   private OdeProperties odeProperties;

   public BsmMessagePublisher(OdeProperties odeProps, String kafkaTopic, Class<?> msgSerializerClass) {
      super(odeProps, kafkaTopic, msgSerializerClass);
      this.odeProperties = odeProps;
   }

   @Override
   public void publish(OdeData msg) {
      OdeBsmData odeBsm = (OdeBsmData) msg;

//      if (msg.getMetadata() != null && msg.getMetadata().getReceivedAt() != null)
//         try {
//            long latency = DateTimeUtils.difference(DateTimeUtils.isoDateTime(msg.getMetadata().getReceivedAt()),
//                  ZonedDateTime.now());
//            odeBsm.getMetadata().setLatency(latency);
//         } catch (ParseException e) {
//            logger.error("Error converting ISO timestamp", e);
//         }
      
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicRawBsmPojo(), odeBsm.getPayload().getData());
      objectProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null, odeBsm.getPayload().getData());

      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeBsmPojo(), odeBsm);
      objectProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsm);
   }

}

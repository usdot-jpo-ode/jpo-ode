package us.dot.its.jpo.ode.coder;

import java.text.ParseException;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

public class MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
   private OdeProperties odeProperties;
   protected MessageProducer<String, OdeObject> objectProducer;

   public MessagePublisher(OdeProperties odeProps) {
      this.odeProperties = odeProps;
      this.objectProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, OdeBsmSerializer.class.getName());

   }

   public void publish(OdeData msg) {
      OdeBsmData odeBsm = (OdeBsmData) msg;

      if (msg.getMetadata() != null && msg.getMetadata().getReceivedAt() != null)
         try {
            long latency = DateTimeUtils.difference(DateTimeUtils.isoDateTime(msg.getMetadata().getReceivedAt()),
                  ZonedDateTime.now());
            odeBsm.getMetadata().setLatency(latency);
         } catch (ParseException e) {
            logger.error("Error converting ISO timestamp", e);
         }
      
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicRawBsmPojo(), odeBsm.getPayload().getData());
      objectProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null, odeBsm.getPayload().getData());

      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeBsmPojo(), odeBsm);
      objectProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsm);
   }

}

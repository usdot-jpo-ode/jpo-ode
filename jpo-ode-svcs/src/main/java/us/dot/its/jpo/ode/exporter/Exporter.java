package us.dot.its.jpo.ode.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class Exporter implements Runnable {

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private OdeProperties odeProperties;
   private SimpMessagingTemplate template;
   private MessageConsumer<String, String> stringConsumer;
   private MessageConsumer<String, byte[]> byteArrayConsumer;
   private String topic;

   public Exporter(OdeProperties odeProps, SimpMessagingTemplate template, String topic)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      this.odeProperties = odeProps;
      this.template = template;
      this.topic = topic;

   }

   @Override
   public void run() {
      logger.info("Subscribing to {}", OdeProperties.KAFKA_TOPIC_J2735_BSM);
      if (OdeProperties.KAFKA_TOPIC_J2735_BSM.endsWith("json")) {
         stringConsumer = MessageConsumer.defaultStringMessageConsumer(
               odeProperties.getKafkaBrokers(),
               odeProperties.getHostId() + this.getClass().getSimpleName(),
               new StompStringMessageDistributor(template, topic));
         stringConsumer.subscribe(OdeProperties.KAFKA_TOPIC_J2735_BSM);
      } else {
         byteArrayConsumer = MessageConsumer.defaultByteArrayMessageConsumer(
               odeProperties.getKafkaBrokers(),
               odeProperties.getHostId() + this.getClass().getSimpleName(),
               new StompByteArrayMessageDistributor(template, topic));
         byteArrayConsumer.subscribe(OdeProperties.KAFKA_TOPIC_J2735_BSM);
      }
      shutDown();
   }

   public void shutDown() {
      logger.info("Shutting down Exporter to topic {}", topic);
      if (stringConsumer != null)
         stringConsumer.close();
      
      if (byteArrayConsumer != null)
         byteArrayConsumer.close();
   }
}

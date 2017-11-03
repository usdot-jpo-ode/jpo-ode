package us.dot.its.jpo.ode.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.stomp.StompContent;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;

public class StompStringMessageDistributor extends AbstractSubscriberProcessor<String, String> {
   private static Logger logger = LoggerFactory.getLogger(StompStringMessageDistributor.class);

   private SimpMessagingTemplate template;
   private String topic;

   public StompStringMessageDistributor(SimpMessagingTemplate template, String topic) {
      this.template = template;
      this.topic = topic;
      logger.info("Distributing messages to API layer topic {}", topic);
   }

   @Override
   protected Object process(String consumedData) {
      template.convertAndSend(topic, new StompContent(consumedData));
      return null;
   }

}

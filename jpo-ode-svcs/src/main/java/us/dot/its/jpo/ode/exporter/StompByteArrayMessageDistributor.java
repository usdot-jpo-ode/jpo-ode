package us.dot.its.jpo.ode.exporter;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.subscriber.Subscriber;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class StompByteArrayMessageDistributor extends MessageProcessor<String, byte[]> {

   private SimpMessagingTemplate template;
   private String topic;

   public StompByteArrayMessageDistributor(SimpMessagingTemplate template, String topic) {
      this.template = template;
      this.topic = topic;
   }

   @Override
   public Object call() throws Exception {
      SerializationUtils<J2735Bsm> serializer = new SerializationUtils<J2735Bsm>();
      Object bsm = serializer.deserialize(record.value());
      template.convertAndSend(topic, new Subscriber(bsm.toString()));
      return bsm;
   }

}

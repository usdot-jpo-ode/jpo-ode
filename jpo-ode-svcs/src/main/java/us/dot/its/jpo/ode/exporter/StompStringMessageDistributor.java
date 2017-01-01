package us.dot.its.jpo.ode.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.subscriber.Subscriber;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class StompStringMessageDistributor extends MessageProcessor<String, String> {
   private static Logger logger = LoggerFactory.getLogger(StompStringMessageDistributor.class);

	private SimpMessagingTemplate template;
   private String topic;

	public StompStringMessageDistributor(SimpMessagingTemplate template, String topic) {
		this.template = template;
      this.topic = topic;
      logger.info("Distributing messages to API layer topic {}", topic);
	}

	@Override
	public Object call() throws Exception {
	   logger.debug("Distributing to topic {}: {}", topic, record.value());
		template.convertAndSend(topic, new Subscriber(record.value()));
		return null;
	}

}

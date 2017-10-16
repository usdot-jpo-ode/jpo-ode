package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ByteArrayPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(ByteArrayPublisher.class);
   protected MessageProducer<String, byte[]> bytesProducer;

   public ByteArrayPublisher(OdeProperties odeProps) {
      super(odeProps);
      this.bytesProducer = MessageProducer.defaultByteArrayMessageProducer(
         odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType());

   }

   public void publish(byte[] msg, String topic) {
    logger.debug("Publishing binary data to {}", topic);
    bytesProducer.send(topic, null, msg);
   }

}

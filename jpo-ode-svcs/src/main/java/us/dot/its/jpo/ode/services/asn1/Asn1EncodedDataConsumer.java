package us.dot.its.jpo.ode.services.asn1;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class Asn1EncodedDataConsumer<K, V> extends MessageConsumer<K, V> {

   private static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

   public Asn1EncodedDataConsumer(String brokers, String groupId, MessageProcessor<K, V> processor,
         String valueDeserializer) {
      super(brokers, groupId, processor, valueDeserializer);
      setName(this.getClass().getSimpleName());
   }

   
   @Override
   public void subscribe(String... topics) {
      List<String> listTopics = Arrays.asList(topics);
      logger.info("subscribing to {}", listTopics);

      consumer.subscribe(listTopics);
   }


   /**
    * Consumes messages until the process method returns a non-null value
    * 
    * @param topics
    *           topics to consume
    * @return the result of processing the consumed messages
    */
   public Object consume() {
      isRunning = true;
      boolean gotMessages = false;
      Object result = null;
      while (isRunning) {
         try {
            logger.debug("Polling for records to process ");
            ConsumerRecords<K, V> records = consumer.poll(CONSUMER_POLL_TIMEOUT_MS);
            if (records != null && !records.isEmpty()) {
               gotMessages = true;
               logger.debug("Records received. {} examining {} message(s)", name, records.count());
               result = processor.process(records);
               if (result != null)
                  isRunning = false;
            } else {
               if (gotMessages) {
                  logger.debug("{} no messages consumed in {} seconds.", name, CONSUMER_POLL_TIMEOUT_MS / 1000);
                  gotMessages = false;
               }
            }
         } catch (Exception e) {
            isRunning = false;
            logger.error(" {} error processing consumed messages", name, e);
         }
      }

      logger.debug("Closing message consumer.");
      consumer.close();
      return result;
   }

}

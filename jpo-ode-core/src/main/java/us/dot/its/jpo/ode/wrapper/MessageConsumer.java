package us.dot.its.jpo.ode.wrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConsumer<K, V> {
   
   private String name = "DefaultMessageConsumer";

   private static final int CONSUMER_POLL_TIMEOUT_MS = 60000;
   public static final String SERIALIZATION_STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
   public static final String SERIALIZATION_BYTE_ARRAY_DESERIALIZER = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
   public static final int DEFAULT_CONSUMER_SESSION_TIMEOUT_MS = 30000;
   public static final int DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS = 1000;
   public static final String DEFAULT_CONSUMER_ENABLE_AUTO_COMMIT = "true";

   private static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

   private MessageProcessor<K, V> processor;

   private KafkaConsumer<K, V> consumer;

   private boolean isRunning = false;

    public static MessageConsumer<String, byte[]> defaultByteArrayMessageConsumer(
        String brokers, String groupId, MessageProcessor<String, byte[]> processor) {

        MessageConsumer<String, byte[]> msgConsumer = new MessageConsumer<String, byte[]>(
                brokers, groupId, processor,
                SERIALIZATION_BYTE_ARRAY_DESERIALIZER);

        logger.info("Default String Message Consumer Created");

        return msgConsumer;
    }

    public static MessageConsumer<String, String> defaultStringMessageConsumer(
         String brokers, String groupId, MessageProcessor<String, String> processor) {

      MessageConsumer<String, String> msgConsumer = new MessageConsumer<String, String>(
              brokers, groupId, processor,
              SERIALIZATION_STRING_DESERIALIZER);

      logger.info("Default String Message Consumer Created");

      return msgConsumer;
   }

   public MessageConsumer(String brokers, String groupId, MessageProcessor<K, V> processor, String valueDeserializer) {
       Properties props = new Properties();

       props.put("enable.auto.commit", DEFAULT_CONSUMER_ENABLE_AUTO_COMMIT);
       props.put("auto.commit.interval.ms", DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS);
       props.put("session.timeout.ms", DEFAULT_CONSUMER_SESSION_TIMEOUT_MS);
       props.put("key.deserializer", SERIALIZATION_STRING_DESERIALIZER);
       props.put("value.deserializer", valueDeserializer);

       this.processor = processor;
       props.put("bootstrap.servers", brokers);
       props.put("group.id", groupId);
       this.consumer = new KafkaConsumer<K, V>(props);

       logger.info("Consumer Created for groupId {}", groupId);
   }

    public MessageConsumer(String brokers, String groupId, MessageProcessor<K, V> processor, Properties props) {
      this.processor = processor;
      props.put("bootstrap.servers", brokers);
      props.put("group.id", groupId);
      this.consumer = new KafkaConsumer<K, V>(props);

      logger.info("Consumer Created for groupId {}", groupId);

    }

    public void subscribe(String... topics) {

        List<String> listTopics = Arrays.asList(topics);
        logger.info("Subscribing to {}", listTopics);
        consumer.subscribe(listTopics);

        isRunning = true;
        boolean gotMessages = false;
        while (isRunning) {
            try {
                ConsumerRecords<K, V> records = consumer.poll(CONSUMER_POLL_TIMEOUT_MS);
                if (records != null && !records.isEmpty()) {
                    gotMessages = true;
                    logger.debug("{} consuming {} message(s)", name, records.count());
                    processor.process(records);
                } else {
                    if (gotMessages) {
                        logger.debug("{} no messages consumed in {} seconds.", name, CONSUMER_POLL_TIMEOUT_MS / 1000);
                        gotMessages = false;
                    }
                }
            } catch (Exception e) {
                logger.error(" {} error processing consumed messages", name, e);
            }
        }

		logger.debug("Closing message consumer.");
        consumer.close();
    }

   public void close() {
      isRunning = false;
   }

   public MessageProcessor<K, V> getProcessor() {
      return processor;
   }

   public void setProcessor(MessageProcessor<K, V> processor) {
      this.processor = processor;
   }

   public KafkaConsumer<K, V> getConsumer() {
      return consumer;
   }

   public void setConsumer(KafkaConsumer<K, V> consumer) {
      this.consumer = consumer;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}

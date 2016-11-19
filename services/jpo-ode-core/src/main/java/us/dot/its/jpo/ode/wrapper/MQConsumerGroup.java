package us.dot.its.jpo.ode.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;

public class MQConsumerGroup<K, V, R> {
   private static Logger logger = LoggerFactory
         .getLogger(MQConsumerGroup.class);

   private ConsumerConnector consumerConnector;
   private MQTopic topic;
   private ExecutorService executorService;
   private DataProcessor<V, R> processor;
   private Decoder<K> keyDecoder;
   private Decoder<V> valueDecoder;

   public MQConsumerGroup(String zookeepers, String a_groupId, 
         MQTopic topic, Decoder<K> keyDecoder,
         Decoder<V> valueDecoder,
         DataProcessor<V, R> a_processor) {
      
      consumerConnector = Consumer.createJavaConsumerConnector(
            createConsumerConfig(zookeepers, a_groupId));
      
      this.topic = topic;
      this.keyDecoder = keyDecoder;
      this.valueDecoder = valueDecoder;
      this.processor = a_processor;
   }

   public void shutDown() {
      if (consumerConnector != null)
         consumerConnector.shutdown();
      if (executorService != null)
         executorService.shutdown();
      try {
         if (!executorService.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
            logger.warn("Timed out waiting for consumer threads to shut down, exiting uncleanly");
         }
      } catch (InterruptedException e) {
         logger.warn("Interrupted during shutdown, exiting uncleanly");
      }
   }

   public void consume() throws InterruptedException, ExecutionException {
      Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
      if (topic != null) {
         topicCountMap.put(topic.getName(), topic.getPartitions());
         Map<String, List<KafkaStream<K, V>>> consumerMap = consumerConnector
               .createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
         List<KafkaStream<K, V>> streams = consumerMap.get(topic.getName());

         // now launch all the threads
         executorService = Executors.newFixedThreadPool(streams.size());

         // now create an object to consume the messages
         int threadNumber = 0;
         for (final KafkaStream<K, V> stream : streams) {
            @SuppressWarnings("unused")
            Future<Object> future = executorService.submit(new MQConsumer<K, V, R>(
                  stream, threadNumber, processor));
            threadNumber++;
         }
      }
   }

   private static ConsumerConfig createConsumerConfig(String zookeepers,
         String a_groupId) {
      Properties props = new Properties();
      props.put("zookeeper.connect", zookeepers);
      props.put("group.id", a_groupId);
      props.put("zookeeper.session.timeout.ms", "4000");
      props.put("zookeeper.sync.time.ms", "2000");
      props.put("auto.commit.interval.ms", "1000");

      return new ConsumerConfig(props);
   }

   public MQTopic getTopic() {
      return topic;
   }

   public void setTopic(MQTopic topic) {
      this.topic = topic;
   }

}
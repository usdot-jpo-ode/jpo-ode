package us.dot.its.jpo.ode.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

/**
 * @author 572682
 * This abstract class provides the common and basic functionality for processinf messages
 * received from a Kafka topic subscription.
 *  
 * @param <K> Message Key type
 * @param <V> Message Value type
 */
public abstract class MessageProcessor<K, V> implements Callable<Object> {

   protected ConsumerRecord<K, V> record;

   public Map<TopicPartition, Long> process(ConsumerRecords<K, V> consumerRecords) throws Exception {

      Map<TopicPartition, Long> processedOffsets = new HashMap<TopicPartition, Long>();
      for (ConsumerRecord<K, V> recordMetadata : consumerRecords) {
         record = recordMetadata;

         TopicPartition topicPartition = new TopicPartition(recordMetadata.topic(), recordMetadata.partition());
         try {
            call();
            processedOffsets.put(topicPartition, recordMetadata.offset());
         } catch (Exception e) {
            throw new Exception("Error processing message", e);
         }
      }
      return processedOffsets;
   }
   
   public void setRecord(ConsumerRecord<K, V> newRecord) {
       this.record = newRecord;
   }

}

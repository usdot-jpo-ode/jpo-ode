/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.wrapper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author 572682
 * This abstract class provides the common and basic functionality for processinf messages
 * received from a Kafka topic subscription.
 *  
 * @param <K> Message Key type
 * @param <V> Message Value type
 */
public abstract class MessageProcessor<K, V> implements Callable<Object> {

   private ConsumerRecord<K, V> record;

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
   
   public ConsumerRecord<K, V> getRecord() {
      return record;
   }

   public MessageProcessor<K, V> setRecord(ConsumerRecord<K, V> newRecord) {
       this.record = newRecord;
       return this;
   }

}

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
package us.dot.its.jpo.ode;

import java.util.Properties;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.util.SerializableObjectPool;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class SerializableMessageProducerPool<K, V> extends SerializableObjectPool<MessageProducer<K, V>> {

   private static final long serialVersionUID = -2293786403623236678L;

   transient OdeProperties odeProperties;
   transient OdeKafkaProperties odeKafkaProperties;

   private String brokers;
   private String type;
   private String partitionerClass;

   private Properties props;

   public SerializableMessageProducerPool(OdeProperties odeProperties, OdeKafkaProperties odeKafkaProperties) {
      super();
      this.odeProperties = odeProperties;
      this.odeKafkaProperties = odeKafkaProperties;
      this.brokers = odeKafkaProperties.getBrokers();
      this.type = odeKafkaProperties.getProducerType();
      this.partitionerClass = odeProperties.getProperty("kafka.partitionerClass");
      init();
   }

   public SerializableMessageProducerPool<K, V> init() {
      props = new Properties();
      props.put("acks", odeProperties.getProperty("kafka.producer.ack", MessageProducer.DEFAULT_PRODUCER_ACKS)); // Set
                                                                                                                 // acknowledgments
                                                                                                                 // for
                                                                                                                 // producer
                                                                                                                 // requests.
      props.put("retries",
            odeProperties.getProperty("kafka.producer.retries", MessageProducer.DEFAULT_PRODUCER_RETRIES)); // If
                                                                                                            // the
                                                                                                            // request
                                                                                                            // fails,
                                                                                                            // the
                                                                                                            // producer
                                                                                                            // can
      // automatically retry
      props.put("batch.size",
            odeProperties.getProperty("kafka.producer.batch.size", MessageProducer.DEFAULT_PRODUCER_BATCH_SIZE_BYTES));
      props.put("linger.ms",
            odeProperties.getProperty("kafka.producer.linger.ms", MessageProducer.DEFAULT_PRODUCER_LINGER_MS));
      props.put("buffer.memory", odeProperties.getProperty("kafka.producer.buffer.memory",
            MessageProducer.DEFAULT_PRODUCER_BUFFER_MEMORY_BYTES));
      props.put("key.serializer",
            odeProperties.getProperty("kafka.key.serializer", MessageProducer.SERIALIZATION_STRING_SERIALIZER));
      props.put("value.serializer",
            odeProperties.getProperty("kafka.value.serializer", MessageProducer.SERIALIZATION_BYTE_ARRAY_SERIALIZER));

      return this;
   }

   @Override
   protected MessageProducer<K, V> create() {
      return new MessageProducer<>(brokers, type, partitionerClass, props,
            odeKafkaProperties.getDisabledTopics());
   }

   @Override
   public boolean validate(MessageProducer<K, V> o) {
      return o.getProducer() != null;
   }

   @Override
   public void expire(MessageProducer<K, V> o) {
      o.close();
   }

   public String getBrokers() {
      return brokers;
   }

   public void setBrokers(String brokers) {
      this.brokers = brokers;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Properties getProps() {
      return props;
   }

   public void setProps(Properties props) {
      this.props = props;
   }

   public String getPartitionerClass() {
      return partitionerClass;
   }

   public void setPartitionerClass(String partitionerClass) {
      this.partitionerClass = partitionerClass;
   }

}

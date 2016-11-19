package us.dot.its.jpo.ode.wrapper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class MQProducer<K, V> {
   private static Logger logger = LoggerFactory.getLogger(MQProducer.class);

   Producer<K, V> producer;

   public MQProducer(String brokers, String type) {
      // TODO
      /*
       * Using default encoder for now. We should define a specific encoder
       * based on the data type.
       */
      // props.put("serializer.class", "kafka.serializer.StringEncoder");
      // TODO
      /*
       * Using default partitioned for now. We should define a specific
       * partitioner based on the data type.
       */
      // props.put("partitioner.class", "com.bah.ode.wrapper.MQPartitioner");
      this(brokers, type, "kafka.serializer.StringEncoder", null);

   }

   public MQProducer(String brokers, String type,
         String encoderClass, String partitionerClass) {

      Properties props = new Properties();
      props.put("metadata.broker.list", brokers);
      if (encoderClass != null)
         props.put("serializer.class", encoderClass);

      if (partitionerClass != null)
         props.put("partitioner.class", partitionerClass);

      props.put("request.required.acks", "1");
      props.put("producer.type", type);

      ProducerConfig config = new ProducerConfig(props);

      producer = new Producer<K, V>(config);

      logger.info("Producer Created");
   }

   public void send(String topic, K key, V value) {
      KeyedMessage<K, V> data;
      if (key == null)
         data = new KeyedMessage<K, V>(topic, value);
      else
         data = new KeyedMessage<K, V>(topic, key, value);

      producer.send(data);
   }

   public void shutDown() {
      producer.close();
      logger.info("Producer Closed");
   }

   public Producer<K, V> getProducer() {
      return producer;
   }

   public MQProducer<K, V> setProducer(Producer<K, V> producer) {
      this.producer = producer;
      return this;
   }

}

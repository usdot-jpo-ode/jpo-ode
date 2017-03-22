package us.dot.its.jpo.ode;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serializer;

import us.dot.its.jpo.ode.util.SerializableObjectPool;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class SerializableMessageProducerPool<K, V> extends SerializableObjectPool<MessageProducer<K, V>> {

   private static final long serialVersionUID = -2293786403623236678L;

   transient OdeProperties odeProperties;

   private String brokers;
   private String type;
   private String partitionerClass;

   private Properties props;
   
   public SerializableMessageProducerPool(OdeProperties odeProperties) {
       super();
       this.odeProperties = odeProperties;
       this.brokers = odeProperties.getKafkaBrokers();
       this.type = odeProperties.getKafkaProducerType();
       this.partitionerClass = odeProperties.getProperty("kafka.partitionerClass");
       init();
    }

    public SerializableMessageProducerPool(String brokers, String type, Serializer<K> keySerializer,
          Serializer<V> valueSerializer, Properties props) {
       this(brokers, type, null, keySerializer, valueSerializer, props);
    }

    public SerializableMessageProducerPool(String brokers, String type, String partitionerClass,
          Serializer<K> keySerializer, Serializer<V> valueSerializer, Properties props) {
       this.brokers = brokers;
       this.type = type;
       this.partitionerClass = partitionerClass;
       this.props = props;
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
      return new MessageProducer<>(brokers, type, partitionerClass, props);
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

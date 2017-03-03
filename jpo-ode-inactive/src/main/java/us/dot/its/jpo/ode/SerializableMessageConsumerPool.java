package us.dot.its.jpo.ode;

import java.util.Properties;

import us.dot.its.jpo.ode.util.SerializableObjectPool;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class SerializableMessageConsumerPool<K, V> extends SerializableObjectPool<MessageConsumer<K, V>> {

   private static final long serialVersionUID = -2293786403623236678L;

   OdeProperties odeProperties;

   private String brokers;
   private String groupId;

   private Properties props;
   
   public SerializableMessageConsumerPool(String groupId, OdeProperties odeProperties) {
       this.odeProperties = odeProperties;
       this.brokers = odeProperties.getKafkaBrokers();
       this.groupId = groupId;
       init();
    }

    public SerializableMessageConsumerPool(String brokers, String groupId, Properties consumerProps) {
       this.brokers = brokers;
       this.groupId = groupId;
       this.props = consumerProps;
       init();
    }

   public SerializableMessageConsumerPool<K, V> init() {
      props = new Properties();

      props.put("enable.auto.commit", odeProperties.getProperty("kafka.consumer.enable.auto.commit",
            MessageConsumer.DEFAULT_CONSUMER_ENABLE_AUTO_COMMIT));
      props.put("auto.commit.interval.ms", odeProperties.getProperty("kafka.auto.commit.interval.ms",
            MessageConsumer.DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS));
      props.put("session.timeout.ms", odeProperties.getProperty("kafka.consumer.session.timeout.ms",
            MessageConsumer.DEFAULT_CONSUMER_SESSION_TIMEOUT_MS));
      props.put("key.deserializer",
            odeProperties.getProperty("kafka.key.deserializer", MessageConsumer.SERIALIZATION_STRING_DESERIALIZER));
      props.put("value.deserializer", odeProperties.getProperty("kafka.value.deserializer",
            MessageConsumer.SERIALIZATION_BYTE_ARRAY_DESERIALIZER));

      return this;
   }

   @Override
   protected MessageConsumer<K, V> create() {
      return new MessageConsumer<K, V>(brokers, groupId, props);
   }

   @Override
   public boolean validate(MessageConsumer<K, V> o) {
      return o.getProcessor() != null && o.getConsumer() != null;
   }

   @Override
   public void expire(MessageConsumer<K, V> o) {
      o.close();
   }

   public String getBrokers() {
      return brokers;
   }

   public void setBrokers(String brokers) {
      this.brokers = brokers;
   }

   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }

   public void setOdeProperties(OdeProperties odeProperties) {
      this.odeProperties = odeProperties;
   }

   public Properties getProps() {
      return props;
   }

   public void setProps(Properties props) {
      this.props = props;
   }

}

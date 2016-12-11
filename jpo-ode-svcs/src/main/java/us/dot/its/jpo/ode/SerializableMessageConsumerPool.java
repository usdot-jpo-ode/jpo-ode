package us.dot.its.jpo.ode;

import java.util.Properties;

import us.dot.its.jpo.ode.util.SerializableObjectPool;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class SerializableMessageConsumerPool<K, V> extends 
	SerializableObjectPool<MessageConsumer<K, V>> {

	private static final long serialVersionUID = -2293786403623236678L;

	OdeProperties odeProperties;

	private String brokers;
	private String groupId;
	private MessageProcessor<K, V> processor;

	private Properties props;

	public SerializableMessageConsumerPool<K, V> init() {
		props = new Properties();

		props.put("enable.auto.commit", 
				OdeProperties.getProperty("kafka.consumer.enable.auto.commit", "true"));
		props.put("auto.commit.interval.ms", OdeProperties.getProperty("kafka.auto.commit.interval.ms", 1000));
		props.put("session.timeout.ms", OdeProperties.getProperty("kafka.consumer.session.timeout.ms", 30000));
		props.put("key.deserializer", 
				OdeProperties.getProperty("kafka.key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"));
		props.put("value.deserializer", 
				OdeProperties.getProperty("kafka.value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer"));

		return this;
	}
	
	public SerializableMessageConsumerPool(String groupId, 
			MessageProcessor<K, V> processor, OdeProperties odeProperties) {
		init();
		this.odeProperties = odeProperties;
		this.brokers = odeProperties.getKafkaBrokers();
		this.groupId = groupId;
		this.processor = processor;
	}

	public SerializableMessageConsumerPool(
			String brokers, String groupId, MessageProcessor<K, V> processor,
			Properties consumerProps) {
		init();
		this.brokers = brokers;
		this.groupId = groupId;
		this.processor = processor;
		this.props = consumerProps;
	}

	@Override
	protected MessageConsumer<K, V> create() {
		return new MessageConsumer<K, V>(brokers, groupId, processor, props);
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

	public MessageProcessor<K, V> getProcessor() {
		return processor;
	}

	public void setProcessor(MessageProcessor<K, V> processor) {
		this.processor = processor;
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

package us.dot.its.jpo.ode;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serializer;

import us.dot.its.jpo.ode.util.SerializableObjectPool;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class SerializableMessageProducerPool<K, V> extends 
	SerializableObjectPool<MessageProducer<K, V>> {

	private static final long serialVersionUID = -2293786403623236678L;

	OdeProperties odeProperties;
	
	private String brokers;
	private String type;
	private String partitionerClass;
	
	private Properties props;
	
	public SerializableMessageProducerPool<K, V> init() {
		props  = new Properties();
		props.put("ack", OdeProperties.getProperty("kafka.producer.ack", "all")); // Set acknowledgments for producer requests.
		props.put("retries", OdeProperties.getProperty("kafka.producer.retries", 0)); // If the request fails, the producer can
		                                 // automatically retry
		props.put("batch.size", OdeProperties.getProperty("kafka.producer.batch.size", 16384));
		props.put("linger.ms", OdeProperties.getProperty("kafka.producer.batch.size", 1));
		props.put("buffer.memory", 
				OdeProperties.getProperty("kafka.producer.buffer.memory", 33554432));// The buffer.memory controls the
		                                             // total amount of memory
                                                     // available to the producer for
		                                             // buffering.
		props.put("key.serializer", 
				OdeProperties.getProperty("kafka.key.serializer", 
						"org.apache.kafka.common.serialization.StringSerializer"));
		props.put("value.serializer", 
				OdeProperties.getProperty("kafka.value.serializer",
				"org.apache.kafka.common.serialization.ByteArraySerializer"));
		
		return this;
	}

	public SerializableMessageProducerPool(OdeProperties odeProperties) {
		super();
		init();
		this.odeProperties = odeProperties;
		this.brokers = odeProperties.getKafkaBrokers();
		this.type = odeProperties.getKafkaProducerType();
		this.partitionerClass = OdeProperties.getProperty("kafka.partitionerClass");
	}

	public SerializableMessageProducerPool(String brokers, String type, 
			Serializer<K> keySerializer, Serializer<V> valueSerializer, Properties props) {
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

	@Override
	protected MessageProducer<K, V> create() {
		return new MessageProducer<K, V>(brokers, type, partitionerClass, props);
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

package us.dot.its.jpo.ode.wrapper;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProducer<K, V> {
	private static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	Producer<K, V> producer;

//	public MessageProducer(String brokers, String type, Properties props) {
//		// TODO
//		/*
//		 * Using default partitioned for now. We should define a specific
//		 * partitioner based on the data type.
//		 */
//		// props.put("partitioner.class", Partitioner.class.canonicalNamer());
//		this(brokers, type, null, new StringSerializer(), new ByteArraySerializer(), props);
//
//	}

	public MessageProducer(String brokers, String type, String partitionerClass, Properties props) {
		props.put("bootstrap.servers", brokers);
		if (partitionerClass != null)
			props.put("partitioner.class", partitionerClass);

		producer = new KafkaProducer<K, V>(props);

		logger.info("Producer Created");
	}

	public void send(String topic, K key, V value) {
		ProducerRecord<K, V> data;
		if (key == null)
			data = new ProducerRecord<K, V>(topic, value);
		else
			data = new ProducerRecord<K, V>(topic, key, value);

		producer.send(data);
	}

	public void close() {
		producer.close();
		logger.info("Producer Closed");
	}

	public Producer<K, V> getProducer() {
		return producer;
	}

	public MessageProducer<K, V> setProducer(Producer<K, V> producer) {
		this.producer = producer;
		return this;
	}

	public void send(ProducerRecord<K, V> producerRecord) {
		producer.send(producerRecord);
		
	}

}

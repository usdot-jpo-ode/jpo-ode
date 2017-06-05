package us.dot.its.jpo.ode.vsdm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.common.Topic;
import us.dot.its.jpo.ode.OdeProperties;

public class VsdKafkaConsumer implements Runnable {

	private static final int CONSUMER_POLL_TIMEOUT_MS = 60000;
	public static final String SERIALIZATION_STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
	public static final String SERIALIZATION_BYTE_ARRAY_DESERIALIZER = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
	public static final int DEFAULT_CONSUMER_SESSION_TIMEOUT_MS = 30000;
	public static final int DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS = 1000;
	public static final String DEFAULT_CONSUMER_ENABLE_AUTO_COMMIT = "true";

	private static final Logger logger = LoggerFactory.getLogger(VsdKafkaConsumer.class);

	private final KafkaConsumer<String, String> consumer;
	private final List<String> topics;
	private final int id;

	public VsdKafkaConsumer(int id, String groupId, List<String> topics, OdeProperties odeProps) {
		logger.info("VSD KAFKA CONSUMER - Topics: {}", topics);
		this.id = id;
		this.topics = topics;
		Properties props = new Properties();
		props.put("enable.auto.commit", DEFAULT_CONSUMER_ENABLE_AUTO_COMMIT);
		props.put("auto.commit.interval.ms", DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS);
		props.put("session.timeout.ms", DEFAULT_CONSUMER_SESSION_TIMEOUT_MS);
		props.put("key.deserializer", SERIALIZATION_STRING_DESERIALIZER);
		props.put("value.deserializer", SERIALIZATION_STRING_DESERIALIZER);
		props.put("bootstrap.servers", odeProps.getKafkaBrokers());
		props.put("group.id", groupId);
		this.consumer = new KafkaConsumer<>(props);
	}

	@Override
	public void run() {
		try {
			logger.info("VSD KAFKA CONSUMER - Topics: {}", topics);
			consumer.subscribe(topics);
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(1000);
				for (ConsumerRecord<String, String> record : records) {
					Map<String, Object> data = new HashMap<>();
					data.put("partition", record.partition());
					data.put("offset", record.offset());
					data.put("value", record.value());
					System.out.println(this.id + ": " + data);
					logger.info("VSD KAFKA CONSUMER - Received record: {}", data);
				}
			}
		} catch (WakeupException e) {
			// ignore for shutdown
		} finally {
			consumer.close();
		}
	}

	public void shutdown() {
		consumer.wakeup();
	}

}

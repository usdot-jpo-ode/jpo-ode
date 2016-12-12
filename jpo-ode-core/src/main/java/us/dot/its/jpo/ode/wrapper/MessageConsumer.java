package us.dot.its.jpo.ode.wrapper;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.metrics.OdeMetrics;
import us.dot.its.jpo.ode.metrics.OdeMetrics.Meter;

public class MessageConsumer<K, V> {

	private static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

	private MessageProcessor<K, V> processor;

	protected Meter meter;

	private KafkaConsumer<K, V> consumer;

	private boolean isRunning = false;

	public MessageConsumer(String brokers, String groupId, MessageProcessor<K, V> processor, Properties props) {
		this.processor = processor;
		
		consumer = new KafkaConsumer<K, V>(props);
		
		logger.info("Consumer Created");

		this.meter = OdeMetrics.getInstance().meter(MessageConsumer.class.getSimpleName(),
				processor.getClass().getSimpleName(), "meter");
	}


	public void subscribe(String... topics) {
		consumer.subscribe(topics);
		isRunning = true;
		while (isRunning) {
			Map<String, ConsumerRecords<K, V>> records = consumer.poll(100);
			try {
				processor.process(records);
			} catch (Exception e) {
				logger.error("Error processing consumed messages", e);
			}
		}
		consumer.close();
	}

	public void close() {
		isRunning = false;
	}
	
	public MessageProcessor<K, V> getProcessor() {
		return processor;
	}

	public void setProcessor(MessageProcessor<K, V> processor) {
		this.processor = processor;
	}

	public KafkaConsumer<K, V> getConsumer() {
		return consumer;
	}

	public void setConsumer(KafkaConsumer<K, V> consumer) {
		this.consumer = consumer;
	}
	
	
}

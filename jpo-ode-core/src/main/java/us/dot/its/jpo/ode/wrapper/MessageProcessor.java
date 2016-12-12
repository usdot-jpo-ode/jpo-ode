package us.dot.its.jpo.ode.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

public abstract class MessageProcessor<K, V> implements Callable<Object> {

	protected ConsumerRecord<K, V> record;

	public Map<TopicPartition, Long> process(ConsumerRecords<K, V> consumerRecords) 
			throws Exception {

		Map<TopicPartition, Long> processedOffsets = new HashMap<TopicPartition, Long>();
		List<ConsumerRecord<K, V>> recordsPerPartition = 
				consumerRecords.records(
						new TopicPartition(record.topic(), record.partition()));
		for (ConsumerRecord<K, V> recordMetadata : recordsPerPartition) {
			record = recordMetadata;
			try {
				call();
//				processedOffsets.put(record., record.offset());
			} catch (Exception e) {
				throw new Exception("Error processing message", e);
			}
		}
		return processedOffsets;
	}

	public void process(Map<String, ConsumerRecords<K, V>> records) throws Exception {
		for (Entry<String, ConsumerRecords<K, V>> record : records.entrySet()) {
			process(record.getValue());
		}
		
	}

}

package us.dot.its.jpo.ode.bsm;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;
import java.util.PriorityQueue;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Comparator;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/*
 * This class consumes string bsms and processes them for packaging into VSD.
 */
public class BsmProcessor implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(BsmProcessor.class);

	private static final String CONSUMER_GROUP = "group1";

	private String endpoint;
	private String topic;
	private String group;
	private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

	public BsmProcessor(OdeProperties odeProps) {
		this.endpoint = odeProps.getKafkaBrokers();
		this.topic = odeProps.getKafkaTopicBsmRawJson();
		this.group = CONSUMER_GROUP;
		this.bsmQueueMap = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {
		// Properties for the kafka topic
		Properties props = new Properties();
		props.put("bootstrap.servers", endpoint);
		props.put("group.id", group);
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> stringConsumer = new KafkaConsumer<String, String>(props);

		stringConsumer.subscribe(Arrays.asList(topic));
		logger.debug("Subscribed to topic {}", topic);
		while (true) {
			ConsumerRecords<String, String> records = stringConsumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				logger.debug("Consumed record: {}", record.value());
				processBsm(record.value());
			}
		}
	}

	public void processBsm(String bsmString) {
		// convert string json to J2735Bsm
		J2735Bsm bsm = (J2735Bsm) JsonUtils.fromJson(bsmString, J2735Bsm.class);
		String tempId = bsm.getCoreData().getId();
		if (!bsmQueueMap.containsKey(tempId)) {
			//Comparator<J2735Bsm> bsmComparator = new BsmComparator();
			Queue<J2735Bsm> bsmQueue = new PriorityQueue<>(10);
			bsmQueueMap.put(tempId, bsmQueue);
		}
		bsmQueueMap.get(tempId).add(bsm);
		if (bsmQueueMap.get(tempId).size() == 10) {
			// extract the 10 bsms
			J2735Bsm[] bsmArray = (J2735Bsm[]) bsmQueueMap.get(tempId).toArray();
			for(J2735Bsm entry: bsmArray){
				logger.debug("Bsm in array: {}", entry.toString());
			}
			
			// convert them to VSDs
			
			// publish the Vsd to vsdKafka topic
		}
	}

	// Comparator for the priority queue to keep the chronological order of bsms
	private class BsmComparator implements Comparator<J2735Bsm> {
		@Override
		public int compare(J2735Bsm x, J2735Bsm y) {
			// here getTime would return the time the bsm was received by the
			// ode
			// if (x.getTime() < y.getTime())
			// {
			// return -1;
			// }
			// if (x.getTime() > y.getTime())
			// {
			// return 1;
			// }
			return 0;
		}
	}
}

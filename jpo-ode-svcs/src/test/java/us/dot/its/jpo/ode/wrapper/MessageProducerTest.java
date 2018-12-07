package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

public class MessageProducerTest {

	Properties mockProps = new Properties();
	@Mocked
	KafkaProducer<?, ?> mockKafkaProducer;
	@Mocked
	Producer<String, String> mockProducer;
	@Injectable
	ProducerRecord<String, String> mockProducerRecord;

	@Before
	public void setUp() {
		new Expectations() {
			{
				new KafkaProducer<>((Properties) any);
			}
		};
	}

	@Test
	public void shouldConstruct() {

		MessageProducer<String, String> testMessageProducer = new MessageProducer<String, String>("testBrokers", null,
				"testPartitioner", mockProps);
	}

	@Test
	public void testSendNoTopic() {

		MessageProducer<String, String> testMessageProducer = new MessageProducer<String, String>("testBrokers", null,
				"testPartitioner", mockProps);
		testMessageProducer.send(mockProducerRecord);
	}

	@Test
	public void testSendWithTopic() {

		MessageProducer<String, String> testMessageProducer = new MessageProducer<String, String>("testBrokers", null,
				"testPartitioner", mockProps);

		testMessageProducer.setProducer(mockProducer);
		testMessageProducer.send("testTopic", "testKey", "testValue");
	}

	@Test
	public void testSendWithTopicNullKey() {

		MessageProducer<String, String> testMessageProducer = new MessageProducer<String, String>("testBrokers", null,
				"testPartitioner", mockProps);

		testMessageProducer.send("testTopic", null, "testValue");
		assertEquals(KafkaProducer.class, testMessageProducer.getProducer().getClass());
	}

	@Test
	public void testClose() {

		MessageProducer<String, String> testMessageProducer = new MessageProducer<String, String>("testBrokers", null,
				"testPartitioner", mockProps);
		testMessageProducer.close();
	}

	@Test
	public void testDefaultStringMessageProducer() {

		String testBrokers = "bootstrap.servers";
		String testType = "testType123";

		MessageProducer<String, String> actualProducer = MessageProducer.defaultStringMessageProducer(testBrokers,
				testType);
		assertNotNull(actualProducer);

		new Verifications() {
			{
				mockProps.put("bootstrap.servers", testBrokers);
			}
		};
	}

}

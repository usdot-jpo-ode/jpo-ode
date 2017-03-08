package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.*;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class MessageProducerTest {

    @Ignore
    @Test
    public void shouldReturnDefaultStringMessageProducer(@Mocked MessageProducer mockMp,
            @Mocked KafkaProducer mockKafkaProducer) {

        String testBrokers = "bootstrap.servers";
        String testType = "testType123";

        Properties expectedProps = new Properties();
        expectedProps.put("acks", MessageProducer.DEFAULT_PRODUCER_ACKS);
        expectedProps.put("retries", MessageProducer.DEFAULT_PRODUCER_RETRIES);
        expectedProps.put("batch.size", MessageProducer.DEFAULT_PRODUCER_BATCH_SIZE_BYTES);
        expectedProps.put("linger.ms", MessageProducer.DEFAULT_PRODUCER_LINGER_MS);
        expectedProps.put("buffer.memory", MessageProducer.DEFAULT_PRODUCER_BUFFER_MEMORY_BYTES);
        expectedProps.put("key.serializer", MessageProducer.SERIALIZATION_STRING_SERIALIZER);
        expectedProps.put("value.serializer", MessageProducer.SERIALIZATION_STRING_SERIALIZER);

        new Expectations() {
            KafkaProducer<String, String> mockKafkaProducer;
            {
                new KafkaProducer<>(expectedProps);
                //new MessageProducer<String, String>(testBrokers, testType, null, expectedProps);
            }
        };

        MessageProducer<?, ?> actualProducer = MessageProducer.defaultStringMessageProducer(testBrokers, testType);

    }

}

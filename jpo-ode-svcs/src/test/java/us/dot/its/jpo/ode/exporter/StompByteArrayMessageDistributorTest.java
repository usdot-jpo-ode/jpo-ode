package us.dot.its.jpo.ode.exporter;

import static org.junit.Assert.fail;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.util.SerializationUtils;

@RunWith(JMockit.class)
public class StompByteArrayMessageDistributorTest {

    @Tested
    StompByteArrayMessageDistributor testStompByteArrayMessageDistributor;
    @Injectable
    SimpMessagingTemplate mockSimpMessagingTemplate;
    @Injectable
    String topic = "testTopic123";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCall(@Mocked final SerializationUtils mockSerializationUtils,
            @Mocked ConsumerRecord mockConsumerRecord) {

        new Expectations() {
            {
                new SerializationUtils<>();
                result = mockSerializationUtils;
                
                mockSerializationUtils.deserialize((byte[]) any);
                result = mockConsumerRecord;

                mockConsumerRecord.value();
                result = any;
            }
        };

        try {
            testStompByteArrayMessageDistributor.setRecord(mockConsumerRecord);
            testStompByteArrayMessageDistributor.call();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

}

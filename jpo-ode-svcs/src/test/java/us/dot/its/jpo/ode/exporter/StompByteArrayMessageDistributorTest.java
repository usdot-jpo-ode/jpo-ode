package us.dot.its.jpo.ode.exporter;

import static org.junit.Assert.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.subscriber.Subscriber;
import us.dot.its.jpo.ode.util.SerializationUtils;

@RunWith(JMockit.class)
public class StompByteArrayMessageDistributorTest {

    @Test
    public void shouldConstructAndCall(@Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Injectable ConsumerRecord<String, byte[]> mockConsumerRecord, @Mocked final SerializationUtils<J2735Bsm> mockSerializationUtils, @Mocked Object mockObject) {

        String testTopic = "testTopic123";

        new Expectations() {
            {
                mockConsumerRecord.value();
                result = (byte[]) any;
                
//                mockConsumerRecord.topic();
//                result = null;
//                mockConsumerRecord.partition();
//                result = null;
//                mockConsumerRecord.offset();
//                result = null;
                
                new TopicPartition(anyString, anyInt);
                result = null;
                
                new SerializationUtils<>();
                mockSerializationUtils.deserialize((byte[]) any);
                result = mockObject;
                
                mockObject.toString();
                result = "testExpectedResult123";
                
                mockSimpMessagingTemplate.convertAndSend(testTopic, (Subscriber) any);
                
                
            }
        };

        StompByteArrayMessageDistributor testSSNMP = new StompByteArrayMessageDistributor(mockSimpMessagingTemplate,
                testTopic);
        
        testSSNMP.setRecord(mockConsumerRecord);

        try {
            testSSNMP.call();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

}

package us.dot.its.jpo.ode.exporter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.stomp.StompContent;

@RunWith(JMockit.class)
public class StompStringMessageDistributorTest {

    @Test
    public void shouldConstructAndConvertAndSend(@Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Injectable ConsumerRecord<String, String> mockConsumerRecord) {

        String testTopic = "testTopic123";

        new Expectations() {
            {
                mockSimpMessagingTemplate.convertAndSend(testTopic, (StompContent) any);
                mockConsumerRecord.value();
                result = anyString;
            }
        };

        StompStringMessageDistributor testSSNMP = new StompStringMessageDistributor(mockSimpMessagingTemplate,
                testTopic);

        testSSNMP.setRecord(mockConsumerRecord);

        try {
            assertNull(testSSNMP.call());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }

    }

}

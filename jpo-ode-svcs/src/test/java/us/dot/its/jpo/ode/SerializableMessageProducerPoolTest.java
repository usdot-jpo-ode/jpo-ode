package us.dot.its.jpo.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.Producer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class SerializableMessageProducerPoolTest {

    @Tested
    SerializableMessageProducerPool<?, ?> testSerializableMessageProducerPool;
    @Injectable
    OdeProperties mockOdeProperties;

    @Before
    public void setUp() {
        new Expectations() {
            {
                mockOdeProperties.getProperty(anyString, anyInt);
                result = "test1";
                mockOdeProperties.getProperty(anyString, anyString);
                result = "test2";
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate(@Mocked final MessageProducer<?, ?> mockMessageProducer) {
        new Expectations() {
            {
                new MessageProducer<String, String>(anyString, anyString, anyString, 
                      (Properties) any, (Set<String>)any);
            }
        };

        assertTrue(testSerializableMessageProducerPool.create() instanceof MessageProducer);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testValidateTrue(@Injectable MessageProducer mockMessageProducer, @Mocked Producer<?, ?> mockProducer) {

        new Expectations() {
            {
                mockMessageProducer.getProducer();
                result = mockProducer;
            }
        };

        assertTrue(testSerializableMessageProducerPool.validate(mockMessageProducer));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testValidateFalse(@Injectable MessageProducer mockMessageProducer) {

        new Expectations() {
            {
                mockMessageProducer.getProducer();
                result = null;
            }
        };

        assertFalse(testSerializableMessageProducerPool.validate(mockMessageProducer));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testExpire(@Injectable MessageProducer mockMessageProducer) {
        testSerializableMessageProducerPool.expire(mockMessageProducer);

        new Verifications() {
            {
                mockMessageProducer.close();
            }
        };
    }

    @Test
    public void testSettersAndGetters() {

        testSerializableMessageProducerPool.setBrokers("testBrokers123");
        assertEquals("testBrokers123", testSerializableMessageProducerPool.getBrokers());

        testSerializableMessageProducerPool.setPartitionerClass("testPartitionerClass123");
        assertEquals("testPartitionerClass123", testSerializableMessageProducerPool.getPartitionerClass());

        testSerializableMessageProducerPool.setType("testType123");
        assertEquals("testType123", testSerializableMessageProducerPool.getType());

        testSerializableMessageProducerPool.setProps(null);
        assertNull(testSerializableMessageProducerPool.getProps());
    }

}

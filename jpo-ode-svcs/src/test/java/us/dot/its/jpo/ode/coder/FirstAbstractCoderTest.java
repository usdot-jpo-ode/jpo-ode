package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class FirstAbstractCoderTest {

    @Tested
    BsmCoder testBsmCoder;

    @Mocked
    Asn1Plugin mockAsn1Plugin;
    @Mocked
    J2735Bsm mockJ2735Bsm;
    @Mocked
    SerializableMessageProducerPool<String, byte[]> mockSerializableMessageProducerPool;

    @Test
    public void test_decodeFromHexAndPublish_shouldThrowExceptionEmpty(@Mocked final Scanner mockScanner) {

        new Expectations() {
            {
                mockScanner.hasNextLine();
                result = false;
            }
        };

        try {
            testBsmCoder.decodeFromHexAndPublish(null);
            fail("Expected IOException");
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }

        new Verifications() {
            {
                EventLogger.logger.info("Empty file received");
            }
        };
    }

    @Test
    public void test_decodeFromHexAndPublish(@Mocked final PluginFactory mockPluginFactory,
            @Injectable OdeProperties mockOdeProperties, @Mocked final Scanner mockScanner,
            @Mocked final SerializationUtils<Object> mockSerializationUtils,
            @Mocked final SerializableMessageProducerPool<?, ?> unusedSerializableMessageProducerPool,
            @Mocked final MessageProducer<?, ?> unusedMessageProducer) {

        BsmCoder hexTestBsmCoder = new BsmCoder(mockOdeProperties);

        try {
            new Expectations() {
                {
                    mockScanner.hasNextLine();
                    returns(true, false);

                    mockAsn1Plugin.decodeUPERBsmHex(anyString);
                    result = mockJ2735Bsm;

                    mockSerializationUtils.serialize(any);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            hexTestBsmCoder.decodeFromHexAndPublish(null);

        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void test_decodeFromStreamAndPublish_null() {

        new Expectations() {
            {
                mockAsn1Plugin.decodeUPERBsmStream((InputStream) any);
                result = null;
            }
        };

        try {
            testBsmCoder.setAsn1Plugin(mockAsn1Plugin);
            testBsmCoder.decodeFromStreamAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void test_decodeFromStreamAndPublish(@Mocked final PluginFactory mockPluginFactory,
            @Injectable OdeProperties mockOdeProperties, @Mocked final Scanner mockScanner,
            @Mocked final SerializationUtils<Object> mockSerializationUtils,
            @Mocked final SerializableMessageProducerPool<?, ?> unusedSerializableMessageProducerPool,
            @Mocked final MessageProducer<?, ?> unusedMessageProducer) {

        new Expectations() {
            {
                mockAsn1Plugin.decodeUPERBsmStream((InputStream) any);
                returns(mockJ2735Bsm, null);
            }
        };

        try {
            testBsmCoder.setAsn1Plugin(mockAsn1Plugin);
            testBsmCoder.setMessageProducerPool(mockSerializableMessageProducerPool);
            testBsmCoder.decodeFromStreamAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void test_decodeFromStreamAndPublish_catchException() {

        new Expectations() {
            {
                mockAsn1Plugin.decodeUPERBsmStream((InputStream) any);
                result = new IOException("testException123");
            }
        };

        try {
            testBsmCoder.setAsn1Plugin(mockAsn1Plugin);
            testBsmCoder.decodeFromStreamAndPublish(null);
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
            assertTrue(e.getMessage().startsWith("Error decoding data."));
        }
    }

}

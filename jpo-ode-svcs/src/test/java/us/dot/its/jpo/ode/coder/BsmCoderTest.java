package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class BsmCoderTest {

    @Injectable
    OdeProperties mockOdeProperties;
    @Mocked
    Asn1Plugin mockAsn1Plugin;
    @Mocked
    SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool;
    @Mocked
    Asn1Object mockAsn1Object;
    
    // Override the logger factory for cleaner build logs
    @Mocked 
    LoggerFactory mockLoggerFactory;

    @Test
    public void shouldConstruct() {
        // trivial test that no exceptions are thrown
        BsmCoder testBsmCoder = new BsmCoder();
    }

    @Test
    public void shouldConstructWithParameter(@Mocked final PluginFactory unused) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        BsmCoder testBsmCoder = new BsmCoder(mockOdeProperties);

        assertNotNull("odeProperties null", testBsmCoder.odeProperties);
        assertNotNull("asn1Coder null", testBsmCoder.asn1Coder);
        assertNotNull("messageProducerPool null", testBsmCoder.messageProducerPool);
    }

    @Test
    public void constructorCatchAndLogException(@Mocked final PluginFactory unusedPluginFactory) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = new ClassNotFoundException("testException123");

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        BsmCoder testBsmCoder = new BsmCoder(mockOdeProperties);

    }

    @Test
    public void decodeStringShouldReturnAsn1Object(@Mocked final PluginFactory unused) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    mockAsn1Plugin.decodeUPERBsmHex(anyString);
                    result = mockAsn1Object;

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("Incorrect object returned", mockAsn1Object, new BsmCoder(mockOdeProperties).decode("test"));
    }

    @Test
    public void decodeInputStreamShouldReturnAsn1Object(@Mocked final PluginFactory unused,
            @Mocked InputStream mockInputStream) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    mockAsn1Plugin.decodeUPERBsmStream((InputStream) any);
                    result = mockAsn1Object;

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("Incorrect object returned", mockAsn1Object,
                new BsmCoder(mockOdeProperties).decode(mockInputStream));
    }

    @Test
    public void shouldPublish(@Mocked final PluginFactory unused,
            @Mocked SerializationUtils<J2735Bsm> mockSerializationUtils, @Mocked J2735Bsm mockJ2735Bsm) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    new SerializableMessageProducerPool<>(mockOdeProperties);

                    new SerializationUtils<>();

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }


        new BsmCoder(mockOdeProperties).publish(mockJ2735Bsm);

    }
}

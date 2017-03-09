package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class BsmCoderTest {

    @Test
    public void shouldConstruct() {
        // trivial test that no exceptions are thrown
        BsmCoder testBsmCoder = new BsmCoder();
    }

    @Test
    public void shouldConstructWithParameter(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

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
    public void constructorCatchAndLogException(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unusedPluginFactory, @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool) {
        try {
            new Expectations() {
                {

                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

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
    public void decodeStringShouldReturnAsn1Object(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin, @Mocked Asn1Object mockAsn1Object,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    mockAsn1Plugin.UPER_DecodeBsmHex(anyString);
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
    public void decodeInputStreamShouldReturnAsn1Object(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin, @Mocked Asn1Object mockAsn1Object,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked InputStream mockInputStream) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    mockAsn1Plugin.UPER_DecodeBsmStream((InputStream) any);
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
    public void shouldPublish(@Mocked OdeProperties mockOdeProperties, @Mocked final PluginFactory unused,
            @Mocked Asn1Plugin mockAsn1Plugin, @Mocked Asn1Object mockAsn1Object,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked SerializationUtils<J2735Bsm> mockSerializationUtils, @Mocked J2735Bsm mockJ2735Bsm) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    new SerializableMessageProducerPool<>(mockOdeProperties);

                    new SerializationUtils<>();

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        String testTopic = "testTopic";

        new BsmCoder(mockOdeProperties).publish(testTopic, mockJ2735Bsm);

    }

}

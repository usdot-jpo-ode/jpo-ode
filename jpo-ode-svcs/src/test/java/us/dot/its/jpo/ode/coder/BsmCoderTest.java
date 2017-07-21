package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import us.dot.its.jpo.ode.plugin.asn1.J2735Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class BsmCoderTest {

    @Injectable
    OdeProperties mockOdeProperties;
    @Mocked
    J2735Plugin mockAsn1Plugin;
    @Mocked
    SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool;
    @Mocked
    MessageProducer<String, String> mockMessageProducer;
    @Mocked
    Asn1Object mockAsn1Object;
    
    // Override the logger factory for cleaner build logs
    @Mocked 
    LoggerFactory mockLoggerFactory;
    
    private Path filepath = Paths.get("dirname", "filename");

    @Test
    public void shouldConstructWithParameter(@Mocked final PluginFactory unused) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        BsmStreamDecoderPublisher testBsmCoder = 
                new BsmStreamDecoderPublisher(mockOdeProperties, filepath);

        assertNotNull("odeProperties null", testBsmCoder.odeProperties);
        assertNotNull("asn1Coder null", testBsmCoder.j2735Coder);
    }

    @Test
    public void constructorCatchAndLogException(@Mocked final PluginFactory unusedPluginFactory) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = new ClassNotFoundException("testException123");
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        new BsmStreamDecoderPublisher(mockOdeProperties, filepath);
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

        assertEquals("Incorrect object returned", mockAsn1Object, 
            new BsmStreamDecoderPublisher(mockOdeProperties, filepath)
            .decode("test"));
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
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("Incorrect object returned", mockAsn1Object,
                new BsmStreamDecoderPublisher(mockOdeProperties, filepath)
                .decode(mockInputStream));
    }

    @Test
    public void shouldPublish(@Mocked final PluginFactory unused,
            @Mocked SerializationUtils<J2735Bsm> mockSerializationUtils, @Mocked J2735Bsm mockJ2735Bsm) {
        try {
            new Expectations() {
                {
                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }


        new BsmStreamDecoderPublisher(mockOdeProperties, filepath)
        .publish(mockJ2735Bsm);

    }
}

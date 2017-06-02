package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class MessageFrameCoderTest {
	
	@Mocked
    MessageProducer<String, String> mockMessageProducer;

    @Test
    public void shouldConstruct() {
        // trivial test that no exceptions are thrown
        new MessageFrameStreamDecoderPublisher();
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
        MessageFrameStreamDecoderPublisher testMessageFrameCoder = new MessageFrameStreamDecoderPublisher(mockOdeProperties);

        assertNotNull("odeProperties null", testMessageFrameCoder.odeProperties);
        assertNotNull("asn1Coder null", testMessageFrameCoder.asn1Coder);
        assertNotNull("messageProducerPool null", testMessageFrameCoder.messageProducerPool);
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
        new MessageFrameStreamDecoderPublisher(mockOdeProperties);

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

                    mockAsn1Plugin.decodeUPERMessageFrameHex(anyString);
                    result = mockAsn1Object;

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("Incorrect object returned", mockAsn1Object,
                new MessageFrameStreamDecoderPublisher(mockOdeProperties).decode("test"));
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

                    mockAsn1Plugin.decodeUPERMessageFrameStream((InputStream) any);
                    result = mockAsn1Object;

                    new SerializableMessageProducerPool<>(mockOdeProperties);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("Incorrect object returned", mockAsn1Object,
                new MessageFrameStreamDecoderPublisher(mockOdeProperties).decode(mockInputStream));
    }

    @Test
    public void shouldPublish(@Mocked OdeProperties mockOdeProperties, @Mocked final PluginFactory unused,
            @Mocked Asn1Plugin mockAsn1Plugin, @Mocked Asn1Object mockAsn1Object,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked SerializationUtils<J2735MessageFrame> mockSerializationUtils,
            @Mocked J2735MessageFrame mockJ2735MessageFrame) {
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

        new MessageFrameStreamDecoderPublisher(mockOdeProperties).publish(mockJ2735MessageFrame);

    }

}

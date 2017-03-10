package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class AbstractCoderTest {
    @Injectable
    private static Logger logger;
    @Injectable
    private Asn1Plugin asn1Coder;
    @Injectable
    private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
    @Mocked
    private MessageProducer<String, byte[]> producer;
    @Mocked
    OdeProperties mockOdeProperties;

    @Ignore
    @Test
    public void testDecodeFromStreamAndPublish() throws Exception {
        
        
        final InputStream is = new ByteArrayInputStream("is".getBytes());

        Asn1Object decoded = new J2735Bsm();
        new Expectations() {
            {
                asn1Coder.UPER_DecodeBsmStream(is);
                result = decoded;
                times = 1;
                asn1Coder.UPER_DecodeBsmStream(is);
                result = null;
                times = 1;
                //bsmCoder.publish("topic", decoded);
            }
        };

        new BsmCoder(mockOdeProperties).decodeFromStreamAndPublish(is, "topic");
    }
    
    @Ignore
    @Test
    public void shouldDecodeFromStreamAndPublish(
            //@Mocked OdeProperties mockOdeProperties, 
            @Mocked final PluginFactory mockPluginFactory,
            
            
            @Mocked BsmCoder mockBsmCoder,
            @Injectable SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked MessageProducer<String, byte[]> mockMessageProducer,
            @Mocked Asn1Plugin mockAsn1Plugin
            , @Injectable InputStream mockInputStream
            , @Injectable SerializationUtils<J2735Bsm> mockSerializer
            ) {
        
        
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;
                    
                    //PluginFactory.getPluginByName(anyString);
                    //result = mockAsn1Plugin;
//                    
                    mockAsn1Plugin.UPER_DecodeBsmStream((InputStream) any );
                    result = null;
//                    
                   new SerializableMessageProducerPool<>( (OdeProperties) any );
                   
                   new SerializationUtils<>();
                   mockSerializer.serialize((J2735Bsm) any);
                   result = null;
                    
                   mockSerializableMessagePool.checkOut();
                    result = mockMessageProducer;
                    
                    mockMessageProducer.send(anyString, null, (byte[]) any);
                    times = 1;
                    
                    mockSerializableMessagePool.checkIn((MessageProducer<String, byte[]>) any);
                    times = 1;
                    
                }
            };
        } catch(Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        BsmCoder testBsmCoder = new BsmCoder(mockOdeProperties);
        testBsmCoder.setAsn1Plugin(mockAsn1Plugin);
        try {
            testBsmCoder.decodeFromStreamAndPublish(mockInputStream, null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
        
    }

    @Test
    public void shouldPublishString(@Mocked OdeProperties mockOdeProperties, @Mocked final PluginFactory unused,
            @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked MessageProducer<String, String> mockMessageProducer) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;
                    mockOdeProperties.getKafkaBrokers();
                    result = anyString;
                    mockOdeProperties.getKafkaProducerType();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;

                    new SerializableMessageProducerPool<>(mockOdeProperties);

                    MessageProducer.defaultStringMessageProducer(anyString, anyString);
                    result = mockMessageProducer;
                    mockMessageProducer.send(anyString, null, anyString);
                    result = null;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            new BsmCoder(mockOdeProperties).publish("testTopic", "testMessage");
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void shouldDecodeFromHexAndPublish(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked InputStream mockInputStream, @Mocked Scanner mockScanner, @Mocked J2735Bsm mockAsn1Object) {
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

                    new Scanner(mockInputStream);
                    mockScanner.hasNextLine();
                    returns(true, false);

                    mockScanner.nextLine();
                    result = anyString;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        try {
            new BsmCoder(mockOdeProperties).decodeFromHexAndPublish(mockInputStream, "testTopic");
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    @Test
    public void shouldThrowExceptionAndLogEmptyFile(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked InputStream mockInputStream, @Mocked Scanner mockScanner, @Mocked J2735Bsm mockAsn1Object) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;
                    
                    new SerializableMessageProducerPool<>(mockOdeProperties);

                    new Scanner(mockInputStream);
                    mockScanner.hasNextLine();
                    result = false;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        try {
            new BsmCoder(mockOdeProperties).decodeFromHexAndPublish(mockInputStream, "testTopic");
        } catch (IOException e) {
            assertEquals("Incorrect exception thrown", IOException.class, e.getClass());
        }
        
        new Verifications() {{
            EventLogger.logger.info("Error occurred while decoding message: {}", (Object) any);
            new IOException("Empty file received");
        }};
    }
    
    @Test
    public void shouldThrowExceptionAndLogException(@Mocked OdeProperties mockOdeProperties,
            @Mocked final PluginFactory unused, @Mocked Asn1Plugin mockAsn1Plugin,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked InputStream mockInputStream, @Mocked Scanner mockScanner, @Mocked J2735Bsm mockAsn1Object) {
        try {
            new Expectations() {
                {
                    mockOdeProperties.getAsn1CoderClassName();
                    result = anyString;

                    PluginFactory.getPluginByName(anyString);
                    result = mockAsn1Plugin;
                    
                    new SerializableMessageProducerPool<>(mockOdeProperties);

                    new Scanner(mockInputStream);
                    result = new IOException("testException123");
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        try {
            new BsmCoder(mockOdeProperties).decodeFromHexAndPublish(mockInputStream, "testTopic");
        } catch (IOException e) {
            assertEquals("Incorrect exception thrown", IOException.class, e.getClass());
        }
        
        new Verifications() {{
            EventLogger.logger.info("Error occurred while decoding message: {}", (Object) any);
            new IOException("Error decoding data: " + anyString, (Throwable) any);
        }};
    }

}

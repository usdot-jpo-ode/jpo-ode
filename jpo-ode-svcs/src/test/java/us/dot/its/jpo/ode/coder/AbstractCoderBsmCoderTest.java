package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedData;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedDataPayload;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.ToBeSignedData;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Opaque;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class AbstractCoderBsmCoderTest {

    @Tested
    BsmStreamDecoderPublisher testBsmCoder;
    @Injectable
    OdeProperties odeProperties;
    @Injectable
    Path filePath;
    
    @Mocked
    J2735MessageFrame mockJ2735MessageFrame;

    @Mocked
    J2735Bsm mockJ2735Bsm;

    @Mocked MessageProducer<String, OdeObject> objectProducer;
    
    @Mocked Ieee1609Dot2Data mockieee1609dot2Data;
    @Mocked Ieee1609Dot2Content mockSecuredContent;
    @Mocked SignedData mockSignedData;
    @Mocked ToBeSignedData mockToBeSignedData;
    @Mocked SignedDataPayload mockPayload;
    @Mocked Ieee1609Dot2Data mockUnsecuredData;
    @Mocked Ieee1609Dot2Content mockUnsecuredContent;
    @Mocked Opaque mockUnsecuredContentData;
    
    @Mocked Oss1609dot2Coder ieee1609dotCoder;
    @Mocked OssJ2735Coder j2735Coder;
    
    @Before
    public void setup() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        new Expectations(PluginFactory.class) {
            {
                PluginFactory.getPluginByName(anyString);
                result = j2735Coder;
            }
        };
    }
    
    @Test
    public void decodeHexAndPublishShouldThrowExceptionEmpty(
        @Mocked final Scanner mockScanner) {

        new Expectations() {
            {
                mockScanner.hasNextLine();
                result = false;
            }
        };

        try {
            testBsmCoder.decodeHexAndPublish(null);
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
    public void decodeHexAndPublishSignedMessageFrame(
        @Mocked final Scanner mockScanner) {

        try {
            new Expectations() {
                {
                    mockScanner.hasNextLine();
                    returns(true, false);

                    ieee1609dotCoder.decodeIeee1609Dot2DataHex(anyString);
                    result = mockieee1609dot2Data;
                    mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                    mockSecuredContent.getSignedData(); result = mockSignedData;
                    mockSignedData.getTbsData(); result = mockToBeSignedData;
                    mockToBeSignedData.getPayload(); result = mockPayload;
                    mockPayload.getData(); result = mockUnsecuredData;
                    mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                    mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                    j2735Coder.decodeUPERMessageFrameBytes(mockUnsecuredContentData.byteArrayValue());
                    result = mockJ2735MessageFrame;
                    mockJ2735MessageFrame.getValue();
                    result = mockJ2735Bsm;
                    
                    odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                    objectProducer.send(anyString, null, (OdeObject) any); 
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testBsmCoder.decodeHexAndPublish(null);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeHexAndPublishSignedBsm(@Mocked final Scanner mockScanner) {

        try {
            new Expectations(PluginFactory.class) {
                {
                    mockScanner.hasNextLine();
                    returns(true, false);

                    ieee1609dotCoder.decodeIeee1609Dot2DataHex(anyString);
                    result = mockieee1609dot2Data;
                    mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                    mockSecuredContent.getSignedData(); result = mockSignedData;
                    mockSignedData.getTbsData(); result = mockToBeSignedData;
                    mockToBeSignedData.getPayload(); result = mockPayload;
                    mockPayload.getData(); result = mockUnsecuredData;
                    mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                    mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                    j2735Coder.decodeUPERMessageFrameBytes(mockUnsecuredContentData.byteArrayValue());
                    result = null;
                    j2735Coder.decodeUPERBsmBytes(mockUnsecuredContentData.byteArrayValue());
                    result = mockJ2735Bsm;
                    
                    odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                    objectProducer.send(anyString, null, (OdeObject) any); 
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testBsmCoder.decodeHexAndPublish(null);

        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeHexAndPublishUnsignedMessageFrame(@Mocked final Scanner mockScanner) {

        try {
            new Expectations(PluginFactory.class, CodecUtils.class) {
                {
                    mockScanner.hasNextLine();
                    returns(true, false);

                    CodecUtils.fromHex(anyString); result = new byte[0];
                    
                    ieee1609dotCoder.decodeIeee1609Dot2DataHex(anyString);
                    result = null;

                    j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                    result = mockJ2735MessageFrame;
                    mockJ2735MessageFrame.getValue();
                    result = mockJ2735Bsm;
                    
                    odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                    objectProducer.send(anyString, null, (OdeObject) any); 
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testBsmCoder.decodeHexAndPublish(null);

        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeHexAndPublishUnsignedBsm(@Mocked final Scanner mockScanner) {

        try {
            new Expectations(PluginFactory.class, CodecUtils.class) {
                {
                    mockScanner.hasNextLine();
                    returns(true, false);

                    CodecUtils.fromHex(anyString); result = new byte[0];
                    
                    ieee1609dotCoder.decodeIeee1609Dot2DataHex(anyString);
                    result = null;

                    j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                    result = null;
                    j2735Coder.decodeUPERBsmBytes((byte[]) any);
                    result = mockJ2735Bsm;
                    
                    odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                    objectProducer.send(anyString, null, (OdeObject) any); 
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testBsmCoder.decodeHexAndPublish(null);

        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishSignedNull() {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                result = mockieee1609dot2Data;

                mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                mockSecuredContent.getSignedData(); result = mockSignedData;
                mockSignedData.getTbsData(); result = mockToBeSignedData;
                mockToBeSignedData.getPayload(); result = mockPayload;
                mockPayload.getData(); result = mockUnsecuredData;
                mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                
                j2735Coder.decodeUPERMessageFrameBytes(mockUnsecuredContentData.byteArrayValue());
                result = null;
                j2735Coder.decodeUPERBsmBytes((byte[]) any);
                result = null;
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishUnsignedNull() {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                result = null;

                j2735Coder.decodeUPERMessageFrameStream((InputStream) any);
                result = null;
                j2735Coder.decodeUPERBsmStream((InputStream) any);
                result = null;
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishSignedMessageFrame () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                returns(mockieee1609dot2Data, null);
                
                mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                mockSecuredContent.getSignedData(); result = mockSignedData;
                mockSignedData.getTbsData(); result = mockToBeSignedData;
                mockToBeSignedData.getPayload(); result = mockPayload;
                mockPayload.getData(); result = mockUnsecuredData;
                mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                result = mockJ2735MessageFrame;
                
                mockJ2735MessageFrame.getValue();
                result = mockJ2735Bsm;
                
                j2735Coder.decodeUPERMessageFrameStream((InputStream) any);
                result = null;
                
                j2735Coder.decodeUPERBsmStream((InputStream) any);
                result = null;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishSignedBsm () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                returns(mockieee1609dot2Data, null);
                
                mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                mockSecuredContent.getSignedData(); result = mockSignedData;
                mockSignedData.getTbsData(); result = mockToBeSignedData;
                mockToBeSignedData.getPayload(); result = mockPayload;
                mockPayload.getData(); result = mockUnsecuredData;
                mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                returns(null, null);
                
                j2735Coder.decodeUPERBsmBytes((byte[]) any);
                returns(mockJ2735Bsm, null);
                
                j2735Coder.decodeUPERMessageFrameStream((InputStream) any);
                result = null;
                
                j2735Coder.decodeUPERBsmStream((InputStream) any);
                result = null;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishUnsignedMessageFrame () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                returns(null, null);
                
                j2735Coder.decodeUPERMessageFrameStream((InputStream) any);
                returns(mockJ2735MessageFrame, null);
                
                mockJ2735MessageFrame.getValue();
                result = mockJ2735Bsm;
                
                j2735Coder.decodeUPERBsmStream((InputStream) any);
                result = null;

                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishUnsignedBsm () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                returns(null, null);
                
                j2735Coder.decodeUPERMessageFrameStream((InputStream) any);
                returns(null, null);
                
                j2735Coder.decodeUPERBsmStream((InputStream) any);
                returns(mockJ2735Bsm, null);
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBinaryAndPublishShouldThrowException() {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataStream((InputStream) any);
                result = new IOException("testException123");
            }
        };

        try {
            testBsmCoder.decodeBinaryAndPublish(null);
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
            assertTrue(e.getMessage().startsWith("Error decoding data."));
        }
    }

    @Test
    public void decodeBytesAndPublishSignedMessageFrame () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataBytes((byte[]) any);
                result = mockieee1609dot2Data;
                
                mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                mockSecuredContent.getSignedData(); result = mockSignedData;
                mockSignedData.getTbsData(); result = mockToBeSignedData;
                mockToBeSignedData.getPayload(); result = mockPayload;
                mockPayload.getData(); result = mockUnsecuredData;
                mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                result = mockJ2735MessageFrame;
                
                mockJ2735MessageFrame.getValue();
                result = mockJ2735Bsm;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBytesAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBytesAndPublishSignedBsm () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataBytes((byte[]) any);
                result = mockieee1609dot2Data;
                
                mockieee1609dot2Data.getContent(); result = mockSecuredContent;
                mockSecuredContent.getSignedData(); result = mockSignedData;
                mockSignedData.getTbsData(); result = mockToBeSignedData;
                mockToBeSignedData.getPayload(); result = mockPayload;
                mockPayload.getData(); result = mockUnsecuredData;
                mockUnsecuredData.getContent(); result = mockUnsecuredContent;
                mockUnsecuredContent.getUnsecuredData(); result = mockUnsecuredContentData;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                result = null;
                
                j2735Coder.decodeUPERBsmBytes((byte[]) any);
                result = mockJ2735Bsm;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBytesAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBytesAndPublishUnsignedMessageFrame () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataBytes((byte[]) any);
                result = null;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                result = mockJ2735MessageFrame;
                
                mockJ2735MessageFrame.getValue();
                result = mockJ2735Bsm;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBytesAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void decodeBytesAndPublishUnsignedBsm () {

        new Expectations() {
            {
                ieee1609dotCoder.decodeIeee1609Dot2DataBytes((byte[]) any);
                result = null;
                
                j2735Coder.decodeUPERMessageFrameBytes((byte[]) any);
                result = null;
                
                j2735Coder.decodeUPERBsmBytes((byte[]) any);
                result = mockJ2735Bsm;
                
                odeProperties.getKafkaTopicRawBsmPojo(); result = anyString;
                objectProducer.send(anyString, null, (OdeObject) any); 
            }
        };

        try {
            testBsmCoder.decodeBytesAndPublish(null);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }

}

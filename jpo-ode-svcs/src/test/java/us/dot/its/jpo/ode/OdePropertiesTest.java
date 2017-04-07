package us.dot.its.jpo.ode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class OdePropertiesTest {
    
    @Tested
    OdeProperties testOdeProperties;
    @Injectable
    Environment mockEnv;
    
    

    @Test
    public void testInit() {
        try {
            testOdeProperties.init();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    @Test
    public void initShouldCatchUnknownHostException(@Mocked final InetAddress mockInetAddress) {
        try {
            new Expectations() {{
                InetAddress.getLocalHost();
                result = new UnknownHostException("testException123");
            }};
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        try {
            testOdeProperties.init();
        } catch (Exception e) {
            fail("Unexpected exception in init: " + e);
        }
    }
    
    @Test
    public void testSettersAndGetters() {
        
        String testAsn1CoderClassName = "testAsn1CoderClassName123456";
        String testDdsCasPassword = "testDdsCasPassword123456";
        String testDdsCasUrl = "testDdsCasUrl123456";
        String testDdsCasUsername = "testDdsCasUsername123456";
        String testDdsWebsocketUrl = "testDdsWebsocketUrl123456";
        String testKafkaBrokers = "testKafkaBrokers123456";
        String testKafkaProducerType = "testKafkaProducerType123456";
        String testPluginsLocations = "testpluginsLocations123456";
        String testUploadLocationBsm = "testuploadLocationBsm123456";
        String testUploadLocationMessageFrame = "testuploadLocationMessageFrame123456";
        String testUploadLocationRoot = "testUploadLocationRoot123456";
        
        testOdeProperties.setAsn1CoderClassName(testAsn1CoderClassName);
        testOdeProperties.setDdsCasPassword(testDdsCasPassword);
        testOdeProperties.setDdsCasUrl(testDdsCasUrl);
        testOdeProperties.setDdsCasUsername(testDdsCasUsername);
        testOdeProperties.setDdsWebsocketUrl(testDdsWebsocketUrl);
        testOdeProperties.setEnv(mockEnv);
        testOdeProperties.setEnvironment(mockEnv);
        testOdeProperties.setKafkaBrokers(testKafkaBrokers);
        testOdeProperties.setKafkaProducerType(testKafkaProducerType);
        testOdeProperties.setPluginsLocations(testPluginsLocations);
        testOdeProperties.setUploadLocationBsm(testUploadLocationBsm);
        testOdeProperties.setUploadLocationMessageFrame(testUploadLocationMessageFrame);
        testOdeProperties.setUploadLocationRoot(testUploadLocationRoot);
        
        assertEquals("Incorrect testAsn1CoderClassName", testAsn1CoderClassName, testOdeProperties.getAsn1CoderClassName());
        assertEquals("Incorrect testDdsCasPassword", testDdsCasPassword, testOdeProperties.getDdsCasPassword());
        assertEquals("Incorrect testDdsCasUrl", testDdsCasUrl, testOdeProperties.getDdsCasUrl());
        assertEquals("Incorrect testDdsCasUsername", testDdsCasUsername, testOdeProperties.getDdsCasUsername());
        assertEquals("Incorrect testDdsWebsocketUrl", testDdsWebsocketUrl, testOdeProperties.getDdsWebsocketUrl());
        assertEquals("Incorrect testEnv", mockEnv, testOdeProperties.getEnv());
        assertEquals("Incorrect testKafkaBrokers", testKafkaBrokers, testOdeProperties.getKafkaBrokers());
        assertEquals("Incorrect testKafkaProducerType", testKafkaProducerType, testOdeProperties.getKafkaProducerType());
        assertEquals("Incorrect testpluginsLocations", testPluginsLocations, testOdeProperties.getPluginsLocations());
        assertEquals("Incorrect testUploadLocationBsm", testUploadLocationBsm, testOdeProperties.getUploadLocationBsm());
        assertEquals("Incorrect testUploadLocationMessageFrame", testUploadLocationMessageFrame, testOdeProperties.getUploadLocationMessageFrame());
        assertEquals("Incorrect testUploadLocationRoot", testUploadLocationRoot, testOdeProperties.getUploadLocationRoot());
        testOdeProperties.getHostId();
        testOdeProperties.getProperty("testProperty");
        testOdeProperties.getProperty("testProperty", 5);
        testOdeProperties.getProperty("testProperty", "testDefaultValue");
        testOdeProperties.getUploadLocations();
    }

}

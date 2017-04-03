package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.RTCMPackage;
import us.dot.its.jpo.ode.j2735.dsrc.RTCMmessage;
import us.dot.its.jpo.ode.j2735.dsrc.RTCMmessageList;

/**
 * -- Summary --
 * JUnit test class for OssRTCMPackage
 * 
 * Verifies correct conversion from generic RTCMPackage to compliant-J2735RTCMPackage
 * 
 * This test class is trivial and simply tests that a sample message can be added to a list. RTCMHeader is already 
 * tested by OssRTCMheaderTest.
 * 
 * -- Documentation --
 * Data Frame: DF_RTCMPackage
 * Use: The DF_RTCMPackage data frame is used to convey RTCM messages which deal with differential corrections 
 * between users from one mobile device to another. Encapsulated messages are those defined in RTCM Standard 
 * 10403.1 for Differential GNSS (Global Navigation Satellite Systems) Services - Version 3 adopted on July 1st 2011 
 * and its successors.
 * ASN.1 Representation:
 *    RTCMPackage ::= SEQUENCE { -- precise antenna position and noise data for a rover
 *       rtcmHeader RTCMheader OPTIONAL,
 *       
 *       -- one or more RTCM messages
 *       msgs RTCMmessageList,
 *       ...
 *       }
 *
 */
public class OssRTCMPackageTest {
    
    /**
     * Test that an octet string message with value (2) returns string value (02)
     */
    @Test
    public void shouldCreateTestMessage() {
        
        Integer testInput = 0b00000010;
        String expectedValue = "02";
        
        byte[] testInputBytes = {testInput.byteValue()};
        
        RTCMmessage testRTCMmessage = new RTCMmessage(testInputBytes);
        
        RTCMmessageList testRTCMmessageList = new RTCMmessageList();
        testRTCMmessageList.add(testRTCMmessage);

        RTCMPackage testRTCMpackage = new RTCMPackage();
        testRTCMpackage.msgs = testRTCMmessageList;

        String actualValue = OssRTCMPackage.genericRTCMPackage(testRTCMpackage).getMsgs().get(0);
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssRTCMPackage> constructor = OssRTCMPackage.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
        constructor.newInstance();
        fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
        assertEquals(InvocationTargetException.class, e.getClass());
      }
    }

}

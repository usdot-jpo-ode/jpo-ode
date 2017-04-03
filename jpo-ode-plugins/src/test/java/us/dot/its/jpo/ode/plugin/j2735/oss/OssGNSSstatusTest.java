package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.GNSSstatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;

/**
 * -- Summary --
 * JUnit test class for OssGNSSstatus
 * 
 * Verifies correct conversion from generic GNSSstatus to compliant-J2735GNSSstatus
 * 
 * -- Documentation --
 * Data Element: DE_GNSSstatus
 * Use: The DE_GNSSstatus data element is used to relate the current state of a GPS/GNSS rover or base system 
 * in terms of its general health, lock on satellites in view, and use of any correction information. Various 
 * bits can be asserted (made to a value of one) to reflect these values. A GNSS set with unknown health and 
 * no tracking or corrections would be represented by setting the unavailable bit to one. A value of zero shall 
 * be used when a defined data element is unavailable. The term "GPS" in any data element name in this standard 
 * does not imply that it is only to be used for GPS-type GNSS systems.
 * ASN.1 Representation:
 *    GNSSstatus ::= BIT STRING {
 *       unavailable (0), -- Not Equipped or unavailable
 *       isHealthy (1),
 *       isMonitored (2),
 *       baseStationType (3), -- Set to zero if a moving base station,
 *          -- or if a rover device (an OBU),
 *          -- set to one if it is a fixed base station
 *       aPDOPofUnder5 (4), -- A dilution of precision greater than 5
 *       inViewOfUnder5 (5), -- Less than 5 satellites in view
 *       localCorrectionsPresent (6), -- DGPS type corrections used
 *       networkCorrectionsPresent (7) -- RTK type corrections used
 *       } (SIZE(8))
 */
public class OssGNSSstatusTest {

    /**
     * Test input bit string "00000000" returns "false" for all flag values
     */
    @Test
    public void shouldReturnAllOffGNSSstatus() {
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = 0b00000000;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }
        
    }
    
    /**
     * Test input bit string "11111111" returns "true" for all flag values
     */
    @Test
    public void shouldReturnAllOnGNSSstatus() {
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b11111111;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
    }
    
    /**
     * Test input bit string "10000000" returns "true" for "unavailable" only
     */
    @Test
    public void shouldReturnGNSSstatusUnavailable() {
        
        String elementTested = "unavailable";
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b10000000;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "00000010" returns "true" for "isHealthy" only
     */
    @Test
    public void shouldReturnGNSSstatusIsHealthy() {
        
        String elementTested = "isHealthy";
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b01000000;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "01000000" returns "true" for "localCorrectionsPresent" only
     */
    @Test
    public void shouldReturnGNSSstatusLocalCorrectionsPresent() {
        
        String elementTested = "localCorrectionsPresent";
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b00000010;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "10000000" returns "true" for "networkCorrectionsPresent" only
     */
    @Test
    public void shouldReturnGNSSstatusNetworkCorrectionsPresent() {
        
        String elementTested = "networkCorrectionsPresent";
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b00000001;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "01000010" returns "true" for "isHealthy" and "localCorrectionsPresent" only
     */
    @Test
    public void shouldReturnTwoGNSSstatus() {
        
        String elementTested1 = "isHealthy";
        String elementTested2 = "localCorrectionsPresent";
        
        byte[] testInputBytes = new byte[1];
        testInputBytes[0] = (byte) 0b01000010;
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        J2735GNSSstatus actualGNSSstatus = OssGNSSstatus.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssGNSSstatus> constructor = OssGNSSstatus.class.getDeclaredConstructor();
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

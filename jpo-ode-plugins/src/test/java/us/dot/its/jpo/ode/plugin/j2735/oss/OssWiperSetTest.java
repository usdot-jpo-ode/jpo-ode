package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.WiperRate;
import us.dot.its.jpo.ode.j2735.dsrc.WiperSet;
import us.dot.its.jpo.ode.j2735.dsrc.WiperStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

/**
 * -- Summary --
 * JUnit test class for OssWiperSet
 * 
 * Verifies correct conversion from generic WiperSet to compliant-J2735WiperSet
 * 
 * Independent variables for these tests are set to 0
 * 
 * -- Documentation --
 * Data Frame: DF_WiperSet
 * Use: The DF_WiperSet data frame provides the current status of the wiper systems on the subject vehicle, including 
 * front and rear wiper systems (where equipped).
 * ASN.1 Representation:
 *    WiperSet ::= SEQUENCE {
 *       statusFront WiperStatus,
 *       rateFront WiperRate,
 *       statusRear WiperStatus OPTIONAL,
 *       rateRear WiperRate OPTIONAL
 *       }
 *       
 * Data Element: DE_WiperStatus
 * Use: The current status of a wiper system on the subject vehicle.
 * The "Wiper Status" Probe Data Element is intended to inform other users whether or not it was raining/snowing at the
 * vehicle's location at the time it was taken (such as the Probe Data snapshot). The element also provides an indication as
 * to how hard it was raining/snowing by including the "Swipes Per Minute" of the wiper blades across the windshield. The
 * higher the "Swipes Per Minute", the harder it was raining/snowing. The element also includes whether the wipers were
 * turned on manually (driver activated) or automatically (rain sensor activated) to provide additional information as to driving
 * conditions in the area of the vehicle.
 * ASN.1 Representation:
 *    WiperStatus ::= ENUMERATED {
 *       unavailable (0), -- Not Equipped with wiper status
 *                        -- or wiper status is unavailable
 *       off (1),
 *       intermittent (2),
 *       low (3),
 *       high (4),
 *       washerInUse (5), -- washing solution being used
 *       automaticPresent (6), -- Auto wiper equipped
 *       ...
 *       }
 *       
 * Data Element: DE_WiperRate
 * Use: The current rate at which wiper sweeps are taking place on the subject vehicle, in units of sweeps per minute. 
 * A value of 1 is used for any sweep rate with a period greater than 60 seconds.
 * ASN.1 Representation:
 *    WiperRate ::= INTEGER (0..127) -- units of sweeps per minute
 */
public class OssWiperSetTest {
    
    // Front wiper status tests
    
    /**
     * Test that the front wiper status value (0) returns (unavailable)
     */
    @Test
    public void shouldReturnFrontWiperStatusUnavailable() {
        
        Integer testInput = 0;
        J2735WiperStatus expectedValue = J2735WiperStatus.UNAVAILABLE;
        
        WiperStatus testWiperStatusFront = new WiperStatus(testInput);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum front wiper status value (1) returns (off)
     */
    @Test
    public void shouldReturnFrontWiperStatusMinimum() {

        Integer testInput = 1;
        J2735WiperStatus expectedValue = J2735WiperStatus.OFF;
        
        WiperStatus testWiperStatusFront = new WiperStatus(testInput);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum front wiper status value (6) returns (automaticPresent)
     */
    @Test
    public void shouldReturnFrontWiperStatusMaximum() {
        
        Integer testInput = 6;
        J2735WiperStatus expectedValue = J2735WiperStatus.AUTOMATICPRESENT;
        
        WiperStatus testWiperStatusFront = new WiperStatus(testInput);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a front wiper status value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrontWiperStatusBelowLowerBound() {

        Integer testInput = -1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(testInput);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getStatusFront();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a front wiper status value (7) above the upper bound (6) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrontWiperStatusAboveUpperBound() {

        Integer testInput = 7;
        
        WiperStatus testWiperStatusFront = new WiperStatus(testInput);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getStatusFront();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // Rear wiper status tests
    
    /**
     * Test that a rear wiper status value (0) returns (unavailable)
     */
    @Test
    public void shouldReturnRearWiperStatusUnavailable() {

        Integer testInput = 0;
        J2735WiperStatus expectedValue = J2735WiperStatus.UNAVAILABLE;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(testInput);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum rear wiper status value (1) returns (off)
     */
    @Test
    public void shouldReturnRearWiperStatusMinimum() {

        Integer testInput = 1;
        J2735WiperStatus expectedValue = J2735WiperStatus.OFF;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(testInput);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum rear wiper status value (6) returns (automaticPresent)
     */
    @Test
    public void shouldReturnRearWiperStatusMaximum() {

        Integer testInput = 6;
        J2735WiperStatus expectedValue = J2735WiperStatus.AUTOMATICPRESENT;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(testInput);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        J2735WiperStatus actualValue = OssWiperSet.genericWiperSet(testWiperSet).getStatusRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a rear wiper status value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRearWiperStatusBelowLowerBound() {
        
        Integer testInput = -1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(testInput);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getStatusRear();
            fail("Exepcted IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a rear wiper status value (7) above the upper bound (6) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRearWiperStatusAboveUpperBound() {

        Integer testInput = 7;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(testInput);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getStatusRear();
            fail("Exepcted IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    // Front wiper rate tests
    
    /**
     * Test that the minimum front wiper rate value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumFrontWiperRate() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the front wiper rate value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumFrontWiperRate() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the front wiper rate value (126) returns (126)
     */
    @Test
    public void shouldReturnCornerCaseMaximumFrontWiperRate() {
        
        Integer testInput = 126;
        Integer expectedValue = 126;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum front wiper rate value (127) returns (127)
     */
    @Test
    public void shouldReturnMaximumFrontWiperRate() {
        
        Integer testInput = 127;
        Integer expectedValue = 127;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a front wiper rate value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrontWiperRateBelowLowerBound() {
        
        Integer testInput = -1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a front wiper rate value (128) above the upper bound (127) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrontWiperRateAboveUpperBound() {
        
        Integer testInput = 128;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(testInput);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getRateFront();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    // Rear wiper rate tests
    
    /**
     * Test that the minimum rear wiper rate value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumRearWiperRate() {

        Integer testInput = 0;
        Integer expectedValue = 0;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case minimum rear wiper rate value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumRearWiperRate() {

        Integer testInput = 1;
        Integer expectedValue = 1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case maximum rear wiper rate value (126) returns (126)
     */
    @Test
    public void shouldReturnCornerCaseMaximumRearWiperRate() {

        Integer testInput = 126;
        Integer expectedValue = 126;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum rear wiper rate value (127) returns (127)
     */
    @Test
    public void shouldReturnMaximumRearWiperRate() {

        Integer testInput = 127;
        Integer expectedValue = 127;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        Integer actualValue = OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a rear wiper rate value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRearWiperRateBelowLowerBound() {

        Integer testInput = -1;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a rear wiper rate value (128) above the upper bound (127) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRearWiperRateAboveUpperBound() {
        
        Integer testInput = 128;
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(testInput);
        
        WiperSet testWiperSet = new WiperSet(testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
        try {
            OssWiperSet.genericWiperSet(testWiperSet).getRateRear();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssWiperSet > constructor = OssWiperSet.class.getDeclaredConstructor();
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

package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssSpeedOrVelocity;

/**
 * -- Summary --
 * JUnit test class for OssSpeedOrVelocity
 * 
 * Verifies correct conversion from generic speed or generic velocity to J2735-compliant speed or velocity
 * per ASN.1 specification.
 *
 * -- Documentation --
 * Data Element: DE_Speed
 * Use: This data element represents the vehicle speed expressed in unsigned units of 0.02 meters per second. A value of
 * 8191 shall be used when the speed is unavailable. 
 * ASN.1 Representation:
 *    Speed ::= INTEGER (0..8191) -- Units of 0.02 m/s
 *       -- The value 8191 indicates that
 *       -- speed is unavailable
 *       
 * Data Element: DE_Velocity
 * Use: This data element represents the velocity of an object, typically a vehicle speed or the recommended speed of
 * travel along a roadway, expressed in unsigned units of 0.02 meters per second. When used with motor vehicles it may 
 * be combined with the transmission state to form a data frame for use. A value of 8191 shall be used when the speed 
 * is unavailable. Note that Velocity as used here is intended to be a scalar value and not a vector.
 * ASN.1 Representation:
 *    Velocity ::= INTEGER (0..8191) 
 *    -- Units of 0.02 m/s 
 *    -- The value 8191 indicates that 
 *    -- velocity is unavailable
 */
public class OssSpeedOrVelocityTest {

    // Speed tests

    /**
     * Test that an undefined speed flag of (8191) returns (null)
     */
    @Test
    public void shouldReturnUndefinedSpeedValue() {

        Integer testInput = 8191;
        BigDecimal expectedValue = null;

        Speed testSpeed = new Speed(testInput);
        BigDecimal actualValue = OssSpeedOrVelocity.genericSpeed(testSpeed);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a minimum speed of (0) returns (0.00)
     */
    @Test
    public void shouldReturnMinimumSpeedValue() {

        Integer testValue = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);

        Speed testSpeed = new Speed(testValue);
        BigDecimal actualValue = OssSpeedOrVelocity.genericSpeed(testSpeed);
        
        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum speed of (8190) returns (163.80)
     */
    @Test
    public void shouldReturnMaximumSpeedValue() {

        Integer testInput = 8190;
        BigDecimal expectedValue = BigDecimal.valueOf(163.80).setScale(2);

        Speed testSpeed = new Speed(testInput);
        BigDecimal actualValue = OssSpeedOrVelocity.genericSpeed(testSpeed);
        
        assertEquals(expectedValue, actualValue);
        
    }

    /**
     * Test that a known speed value of (1341) [~60mph] returns (26.82)
     */
    @Test
    public void shouldReturnKnownSpeedValue() {

        Integer testValue = 1341;
        BigDecimal expectedValue = BigDecimal.valueOf(26.82);

        Speed testSpeed = new Speed(testValue);
        BigDecimal actualValue = OssSpeedOrVelocity.genericSpeed(testSpeed);
        
        assertEquals(expectedValue, actualValue);
        
    }

    /**
     * Test that a speed value (-1) below the lower bound 0 throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSpeedBelowLowerBound() {

        Integer testValue = -1;
        Speed testSpeed = new Speed(testValue);

        try {
            OssSpeedOrVelocity.genericSpeed(testSpeed);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that a speed value (8192) above the upper bound 8191 throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSpeedAboveUpperBound() {

        Integer testValue = 8192;
        Speed testSpeed = new Speed(testValue);

        try {
            OssSpeedOrVelocity.genericSpeed(testSpeed);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    // Velocity tests

    /**
     * Test that an undefined velocity flag value 8191 returns null
     */
    @Test
    public void shouldReturnUndefinedVelocityValue() {

        Integer testValue = 8191;
        BigDecimal expectedValue = null;

        Velocity testVelocity = new Velocity(testValue);
        BigDecimal actualValue = OssSpeedOrVelocity.genericVelocity(testVelocity);

        assertEquals(expectedValue, actualValue);
        
    }

    /**
     * Test that a minimum velocity value 0 returns 0.00
     */
    @Test
    public void shouldReturnMinimumVelocityValue() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);

        Velocity testVelocity = new Velocity(testInput);
        BigDecimal actualValue = OssSpeedOrVelocity.genericVelocity(testVelocity);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum velocity value of (8190) returns (163.80)
     */
    @Test
    public void shouldReturnMaximumVelocityValue() {

        Integer testValue = 8190;
        BigDecimal expectedValue = BigDecimal.valueOf(163.80).setScale(2);

        Velocity testVelocity = new Velocity(testValue);
        BigDecimal actualValue = OssSpeedOrVelocity.genericVelocity(testVelocity);

        assertEquals(expectedValue, actualValue);
        
    }

    /**
     * Test that a known velocity value of (1341) [~60mph] returns (26.82)
     */
    @Test
    public void shouldReturnKnownVelocityValue() {

        Integer testInput = 1341;
        BigDecimal expectedValue = BigDecimal.valueOf(26.82);

        Velocity testVelocity = new Velocity(testInput);
        BigDecimal actualValue = OssSpeedOrVelocity.genericVelocity(testVelocity);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a velocity value below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVelocityBelowLowerBound() {

        Integer testValue = -1;
        Velocity testVelocity = new Velocity(testValue);

        try {
            OssSpeedOrVelocity.genericVelocity(testVelocity);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that a velocity value above the upper bound (8191) throws IllegalArgumentExceptions
     */
    @Test
    public void shouldThrowExceptionVelocityAboveUpperBound() {

        Integer testValue = 8192;

        Velocity testVelocity = new Velocity(testValue);

        try {
            OssSpeedOrVelocity.genericVelocity(testVelocity);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssSpeedOrVelocity> constructor = OssSpeedOrVelocity.class.getDeclaredConstructor();
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

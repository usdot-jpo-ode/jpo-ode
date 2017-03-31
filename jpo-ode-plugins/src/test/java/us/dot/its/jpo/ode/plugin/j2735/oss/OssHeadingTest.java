package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.CoarseHeading;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssHeading;

/**
 * Test class for OssHeading Tests that CoarseHeading values return appropriate
 * generic heading values
 * 
 * Test cases: 1) CoarseHeading of 0 returns 0 2) CoarseHeading of 239 returns
 * 358.5 3) Undefined CoarseHeading of 240 returns null 4) CoarseHeading greater
 * than 240 throws IllegalArgumentException 5) CoarseHeading less than 0 throws
 * IllegalArgumentException
 *
 */
public class OssHeadingTest {

    // CoarseHeading ::= INTEGER (0..240)
    // -- Where the LSB is in units of 1.5 degrees
    // -- over a range of 0~358.5 degrees
    // -- the value 240 shall be used for unavailable

    /**
     * Test that minimum coarse heading (0) returns correct heading angle (0.0)
     */
    @Test
    public void shouldReturnCoarseHeadingMin() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);

        CoarseHeading testHeading = new CoarseHeading(testInput);
        BigDecimal actualValue = OssHeading.genericHeading(testHeading);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that maximum coarse heading (239) returns correct heading angle (358.5)
     */
    @Test
    public void shouldReturnCoarseHeadingMax() {

        Integer testInput = 239;
        BigDecimal expectedValue = BigDecimal.valueOf(358.5);

        CoarseHeading testHeading = new CoarseHeading(testInput);
        BigDecimal actualValue = OssHeading.genericHeading(testHeading);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that undefined coarse heading flag (240) returns (null)
     */
    @Test
    public void shouldReturnCoarseHeadingUndefined() {
        
        Integer testInput = 240;
        BigDecimal expectedValue = null;

        CoarseHeading testHeading = new CoarseHeading(testInput);
        BigDecimal actualValue = OssHeading.genericHeading(testHeading);

        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that known coarse heading (11) returns (16.5)
     */
    @Test
    public void shouldReturnCoarseHeadingKnown() {
        
        Integer testInput = 11;
        BigDecimal expectedValue = BigDecimal.valueOf(16.5);
        
        CoarseHeading testHeading = new CoarseHeading(testInput);
        BigDecimal actualValue = OssHeading.genericHeading(testHeading);
        
        assertEquals(expectedValue, actualValue);
        
    }

    /**
     * Test that a coarse heading greater than 240 throws exception
     */
    @Test
    public void shouldThrowExceptionHeadingOutOfBoundsHigh() {
        
        Integer testInput = 241;
        CoarseHeading testHeading = new CoarseHeading(testInput);

        try {
            OssHeading.genericHeading(testHeading);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

    /**
     * Test that a coarse heading less than 0 throws exception
     */
    @Test
    public void shouldThrowExceptionHeadingOutOfBoundsLow() {
        
        Integer testInput = -1;
        CoarseHeading testHeading = new CoarseHeading(testInput);
        
        try {
            OssHeading.genericHeading(testHeading);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssHeading> constructor = OssHeading.class.getDeclaredConstructor();
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

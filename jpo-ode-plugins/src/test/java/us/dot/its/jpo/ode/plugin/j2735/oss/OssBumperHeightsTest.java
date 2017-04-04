package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.BumperHeight;
import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBumperHeights;

/**
 * -- Summary --
 * JUnit test class for OssBumperHeights
 * 
 * Verifies that OssBumperHeights properly converts a generic BumperHeights object 
 * into a compliant J2735BumperHeights object per ASN.1 specifications.
 * 
 * -- Documentation --
 * Data Frame: DF_BumperHeights
 * Use: The DF Bumper Heights data frame conveys the height of the front and rear 
 * bumper of the vehicle or object (can also be used with trailers). 
 * ASN.1 Representation:
 * BumperHeights ::= SEQUENCE {
 *    front BumperHeight,
 *    rear BumperHeight
 *   }
 *   
 * Data Element: DE_BumperHeight
 * Use: The DE_Bumper Height data element conveys the height of one of the bumpers of the 
 * vehicle or object. In cases of vehicles with complex bumper shapes, the center of the 
 * mass of the bumper (where the bumper can best absorb an impact) should be used.
 * ASN.1 Representation:
 * BumperHeight ::= INTEGER (0..127) -- in units of 0.01 meters from ground surface.
 */
public class OssBumperHeightsTest {

    /**
     * Test that minimum bumper height 0 returns (0.00)
     */
    @Test
    public void shouldReturnMinimumBumperHeights() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        BumperHeight testBumperHeightFront = new BumperHeight(testInput);
        BumperHeight testBumperHeightRear = new BumperHeight(testInput);

        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);

        BigDecimal actualBumperHeightsFront = OssBumperHeights.genericBumperHeights(testBumperHeights).getFront();
        BigDecimal actualBumperHeightsRear = OssBumperHeights.genericBumperHeights(testBumperHeights).getRear();

        assertEquals("Minimum front bumper height 0 should return 0:", expectedValue, actualBumperHeightsFront);
        assertEquals("Minimum rear bumper height 0 should return 0:", expectedValue, actualBumperHeightsRear);

    }

    /**
     * Test that maximum bumper height 127 returns 1.27
     */
    @Test
    public void shouldReturnMaximumBumperHeights() {

        Integer testInput = 127;
        BigDecimal expectedValue = BigDecimal.valueOf(1.27);

        BumperHeight testBumperHeightFront = new BumperHeight(testInput);
        BumperHeight testBumperHeightRear = new BumperHeight(testInput);
        
        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);

        BigDecimal actualBumperHeightsFront = OssBumperHeights.genericBumperHeights(testBumperHeights).getFront();
        BigDecimal actualBumperHeightsRear = OssBumperHeights.genericBumperHeights(testBumperHeights).getRear();

        assertEquals("Maximum front bumper height 127 should return 1.27", expectedValue, actualBumperHeightsFront);
        assertEquals("Maximum rear bumper height 127 should return 1.27", expectedValue, actualBumperHeightsRear);
        
    }

    /**
     * Test that known bumper height value 85 returns 0.85
     */
    @Test
    public void shouldReturnKnownBumperHeights() {

        Integer testHeight = 85;

        BigDecimal expectedValue = BigDecimal.valueOf(0.85);
        BumperHeight testBumperHeight = new BumperHeight(testHeight);

        BumperHeights testBumperHeights = new BumperHeights(testBumperHeight, testBumperHeight);

        J2735BumperHeights actualBumperHeights = OssBumperHeights.genericBumperHeights(testBumperHeights);

        assertEquals("Known front bumper height 85 should return 0.85", expectedValue, actualBumperHeights.getFront());
        assertEquals("Known rear bumper height 85 should return 0.85", expectedValue, actualBumperHeights.getRear());

    }

    /**
     * Test that a front bumper height value below 0 throws IllegalArgumentException
     * 
     * Rear bumper height is the independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionBumperHeightsFrontBelowLowerBound() {
        
        Integer testInput = -1;
        Integer controlValue = 0;
        
        BumperHeight testBumperHeightFront = new BumperHeight(testInput);
        BumperHeight testBumperHeightRear = new BumperHeight(controlValue);
        
        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);
        
        try {
            OssBumperHeights.genericBumperHeights(testBumperHeights);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

    /**
     * Test that a rear bumper height value below 0 throws IllegalArgumentException
     * 
     * Front bumper height is the independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionBumperHeightsRearBelowLowerBound() {
        
        Integer testInput = -1;
        Integer controlValue = 0;
        
        BumperHeight testBumperHeightFront = new BumperHeight(controlValue);
        BumperHeight testBumperHeightRear = new BumperHeight(testInput);
        
        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);
        
        try {
            OssBumperHeights.genericBumperHeights(testBumperHeights);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a front bumper height value above 127 throws IllegalArgumentException
     * 
     * Rear bumper height is the independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionBumperHeightsFrontAboveUpperBound() {
        
        Integer testInput = 128;
        Integer controlValue = 0;
        
        BumperHeight testBumperHeightFront = new BumperHeight(testInput);
        BumperHeight testBumperHeightRear = new BumperHeight(controlValue);
        
        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);
        
        try {
            OssBumperHeights.genericBumperHeights(testBumperHeights);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a rear bumper height value above 127 throws IllegalArgumentException
     * 
     * Front bumper height is the independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionBumperHeightsRearAboveUpperBound() {
        
        Integer testInput = 128;
        Integer controlValue = 0;
        
        BumperHeight testBumperHeightFront = new BumperHeight(controlValue);
        BumperHeight testBumperHeightRear = new BumperHeight(testInput);
        
        BumperHeights testBumperHeights = new BumperHeights(testBumperHeightFront, testBumperHeightRear);
        
        try {
            OssBumperHeights.genericBumperHeights(testBumperHeights);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssBumperHeights> constructor = OssBumperHeights.class.getDeclaredConstructor();
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

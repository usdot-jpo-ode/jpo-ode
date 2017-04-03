package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AntennaOffsetSet;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B09;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;

/**
 * -- Summary --
 * JUnit test class for OssAntennaOffSet
 * 
 * Verifies correct conversion from generic AntennaOffset to compliant J2735AntennaOffset
 * 
 * Independent variables for the tests are set to 0
 * 
 * -- Documentation --
 * Data Frame: DF_AntennaOffsetSet
 * Use: The DF_AntennaOffsetSet data frame is a collection of three offset values in an orthogonal coordinate 
 * system which describe how far the electrical phase center of an antenna is in each axis from a nearby known 
 * anchor point in units of 1 cm. When the antenna being described is on a vehicle, the signed offset shall be 
 * in the coordinate system defined in section 11.4.
 * ASN.1 Representation:
 *    AntennaOffsetSet ::= SEQUENCE {
 *       antOffsetX Offset-B12, -- a range of +- 20.47 meters
 *       antOffsetY Offset-B09, -- a range of +- 2.55 meters
 *       antOffsetZ Offset-B10 -- a range of +- 5.11 meters
 *       }
 * 
 * Data Element: DE_Offset_B12
 * Use: A 12-bit delta offset in X, Y or Z direction from some known point. For non-vehicle centric coordinate 
 * frames of reference, non-vehicle centric coordinate frames of reference, offset is positive to the East (X) 
 * and to the North (Y) directions. The most negative value shall be used to indicate an unknown value.
 * ASN.1 Representation:
 *    Offset-B12 ::= INTEGER (-2048..2047) -- a range of +- 20.47 meters
 *    
 * Data Element: DE_Offset_B09
 * Use: A 9-bit delta offset in X, Y or Z direction from some known point. For non-vehicle centric coordinate 
 * frames of reference, offset is positive to the East (X) and to the North (Y) directions. The most negative 
 * value shall be used to indicate an unknown value.
 * ASN.1 Representation:
 *    Offset-B09 ::= INTEGER (-256..255) -- a range of +- 2.55 meters
 *    
 * Data Element: DE_Offset_B10
 * Use: A 10-bit delta offset in X, Y or Z direction from some known point. For non-vehicle centric coordinate 
 * frames of reference, offset is positive to the East (X) and to the North (Y) directions. The most negative 
 * value shall be used to indicate an unknown value.
 * ASN.1 Representation:
 *    Offset-B10 ::= INTEGER (-512..511) -- a range of +- 5.11 meters
 *
 */
public class OssAntennaOffsetSetTest {

    // X offset tests
    /**
     * Test that the X Offset-B12 flag value (-2048) returns (null)
     */
    @Test
    public void shouldReturnUndefinedXOffset() {
        
        Integer testInput = -2048;
        BigDecimal expectedValue = null;
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that an X Offset-B12 minimum value (-2047) returns (-20.47)
     */
    @Test
    public void shouldReturnMinimumXOffset() {

        Integer testInput = -2047;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.47);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case minimum X offset value (-2046) returns (-20.46)
     */
    @Test
    public void shouldReturnCornerCaseMinimumXOffset() {
        
        Integer testInput = -2046;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.46);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a known middle X offset value (11) returns (0.11) 
     */
    @Test
    public void shouldReturnMiddleXOffset() {

        Integer testInput = 11;
        BigDecimal expectedValue = BigDecimal.valueOf(0.11);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case maximum X offset value (2046) returns (20.46)
     */
    @Test
    public void shouldReturnCornerCaseMaximumXOffset() {

        Integer testInput = 2046;
        BigDecimal expectedValue = BigDecimal.valueOf(20.46);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum X offset value (2047) returns (20.47)
     */
    @Test
    public void shouldReturnMaximumXOffset() {

        Integer testInput = 2047;
        BigDecimal expectedValue = BigDecimal.valueOf(20.47);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet).getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that an X offset value (-2049) below the lower bound (-2048) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionXOffsetBelowLowerBound() {
        
        Integer testInput = -2049;
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        try {
            OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that an X offset value (2048) above the upper bound (2047) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionXOffsetAboveUpperBound() {
        
        Integer testInput = 2048;
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        try {
            OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // Y offset tests
    /**
     * Test that the Y offset undefined flag value (-256) returns (null)
     */
    @Test
    public void shouldReturnUndefinedYOffset() {
        
        Integer testInput = -256;
        BigDecimal expectedValue = null;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
    }
    
    
    /**
     * Test that the minimum Y offset value (-255) returns (-2.55)
     */
    @Test
    public void shouldReturnMinimumYOffset() {

        Integer testInput = -255;
        BigDecimal expectedValue = BigDecimal.valueOf(-2.55);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case minimum Y offset value (-254) returns (-2.54)
     */
    @Test
    public void shouldReturnCornerCaseMinimumYOffset() {

        Integer testInput = -254;
        BigDecimal expectedValue = BigDecimal.valueOf(-2.54);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a known middle Y offset value (-5) returns (-0.05)
     */
    @Test
    public void shouldReturnMiddleYOffset() {

        Integer testInput = -5;
        BigDecimal expectedValue = BigDecimal.valueOf(-0.05);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case maximum Y offset value (254) returns (2.54)
     */
    @Test
    public void shouldReturnCornerCaseMaximumYOffset() {

        Integer testInput = 254;
        BigDecimal expectedValue = BigDecimal.valueOf(2.54);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum Y offset value (255) returns (2.55)
     */
    @Test
    public void shouldReturnMaximumYOffset() {

        Integer testInput = 255;
        BigDecimal expectedValue = BigDecimal.valueOf(2.55);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetY();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a Y offset value (-257) below the lower bound (-256) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionYOffsetBelowLowerBound() {

        Integer testInput = -257;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        try {
            OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a Y offset value (256) above the upper bound (255) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionYOffsetAboveUpperBound() {

        Integer testInput = 256;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(testInput);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        try {
            OssAntennaOffsetSet.genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // Z offset tests
    /**
     * Test that the Z offset undefined flag value (-512) returns (null)
     */
    @Test
    public void shouldReturnUndefinedZOffset() {

        Integer testInput = -512;
        BigDecimal expectedValue = null;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that the Z offset minimum value (-511) returns (-5.11)
     */
    @Test
    public void shouldReturnMinimumZOffset() {

        Integer testInput = -511;
        BigDecimal expectedValue = BigDecimal.valueOf(-5.11);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case Z offset minimum value (-510) returns (-5.10)
     */
    @Test
    public void shouldReturnCornerCaseMinimumZOffset() {

        Integer testInput = -510;
        BigDecimal expectedValue = BigDecimal.valueOf(-5.10).setScale(2);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a known middle Z offset value (27) returns (0.27)
     */
    @Test
    public void shouldReturnMiddleZOffset() {

        Integer testInput = 27;
        BigDecimal expectedValue = BigDecimal.valueOf(0.27);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case maximum Z offset value (510) returns (5.10)
     */
    @Test
    public void shouldReturnCornerCaseMaximumZOffset() {

        Integer testInput = 510;
        BigDecimal expectedValue = BigDecimal.valueOf(5.10).setScale(2);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that the maximum Z offset value (511) returns (5.11)
     */
    @Test
    public void shouldReturnMaximumZOffset() {

        Integer testInput = 511;
        BigDecimal expectedValue = BigDecimal.valueOf(5.11);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        BigDecimal actualValue = OssAntennaOffsetSet
                .genericAntennaOffsetSet(testAntennaOffsetSet)
                .getAntOffsetZ();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a Z offset value (-513) below the lower bound (-512) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionZOffsetBelowLowerBound() {

        Integer testInput = -513;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        try {
            OssAntennaOffsetSet
                    .genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a Z offset value (512) above the upper bound (511) throws illegalArgumentException
     */
    @Test
    public void shouldThrowExceptionZOffsetAboveUpperBound() {

        Integer testInput = 512;
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(testInput);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        try {
            OssAntennaOffsetSet
                    .genericAntennaOffsetSet(testAntennaOffsetSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }    
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssAntennaOffsetSet> constructor = OssAntennaOffsetSet.class.getDeclaredConstructor();
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

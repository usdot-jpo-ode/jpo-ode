package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssOffset;

/**
 * -- Summary --
 * Test class for OssOffset class
 * 
 * Tests conversion from VertOffset-B07 to ASN VertOffset Test conversion from Offset-B12 to ASN Offset-B12
 * 
 * -- Documentation --
 * Data Element: DE_Offset_B12
 * Use: A 12-bit delta offset in X, Y or Z direction from some known point. For non-vehicle centric 
 * coordinate frames of reference, non-vehicle centric coordinate frames of reference, offset is positive 
 * to the East (X) and to the North (Y) directions. The most negative value shall be used to indicate 
 * an unknown value.
 * ASN.1 Representation:
 *    Offset-B12 ::= INTEGER (-2048..2047)
 *       -- a range of +- 20.47 meters
 * 
 * Data Element: DE_VertOffset-B07
 * Use: A 7-bit vertical delta offset in the Z direction from the last point. The offset is positive 
 * to the Vertical (Z) direction. The most negative value shall be used to indicate an unknown value. 
 * Unlike similar horizontal offsets, the LSB used is 10 centimeters (not one centimeter).
 * ASN.1 Representation:
 *    VertOffset-B07 ::= INTEGER (-64..63)
 *       -- LSB units of of 10 cm 
 *       -- with a range of +- 6.3 meters vertical 
 *       -- value 63 to be used for 63 or greater 
 *       -- value -63 to be used for -63 or greater 
 *       -- value -64 to be unavailable
 *
 */
public class OssOffsetTest {
    
    // Offset-B12 tests

    /**
     * Test that a minimum Offset-B12 value of -2047 returns -20.47
     */
    @Test
    public void shouldReturnMinimumOffsetB12() {

        Integer testInput = -2047;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.47);

        Offset_B12 testOffset_B12 = new Offset_B12(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum value of +2047 returns +20.47
     */
    @Test
    public void shouldReturnMaximumOffsetB12() {

        Integer testInput = 2047;
        BigDecimal expectedValue = BigDecimal.valueOf(20.47);
        
        Offset_B12 testOffset_B12 = new Offset_B12(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known value of 1234 returns 12.34
     */
    @Test
    public void shouldReturnKnownOffsetB12() {

        Integer testInput = 1234;
        BigDecimal expectedValue = BigDecimal.valueOf(12.34);
        
        Offset_B12 testOffset_B12 = new Offset_B12(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that undefined flag -2048 returns null
     */
    @Test
    public void shouldReturnUndefinedOffsetB12() {

        Integer testInput = -2048;
        BigDecimal expectedValue = null;

        Offset_B12 testOffset_B12 = new Offset_B12(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that a value (2048) above the upper limit value 2047 throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionOffsetB12AboveUpperBound() {

        Integer testInput = 2048;

        Offset_B12 testOffset_B12 = new Offset_B12(testInput);
        
        try {
            BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that a value (-2049) below the lower limit value -2048 throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionOffsetB12BelowLowerBound() {

        Integer testInput = -2049;

        Offset_B12 testOffset_B12 = new Offset_B12(testInput);

        try {
            BigDecimal actualValue = OssOffset.genericOffset(testOffset_B12);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }
    
    // VertOffset-B07 tests

    /**
     * Test that a minimum vertical offset of -63 returns -6.3
     */
    @Test
    public void shouldReturnMinimumVertOffsetB07() {

        Integer testInput = -63;
        BigDecimal expectedValue = BigDecimal.valueOf(-6.3);

        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum vertical offset of 63 returns 6.3
     */
    @Test
    public void shouldReturnMaximumVertOffsetB07() {

        Integer testInput = 63;
        BigDecimal expectedValue = BigDecimal.valueOf(6.3);
        
        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);
        
        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that a known vertical offset of 25 returns 2.5
     */
    @Test
    public void shouldReturnKnownVertOffsetB07() {

        Integer testInput = 25;
        BigDecimal expectedValue = BigDecimal.valueOf(2.5);

        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a flag value offset of -64 indicates undefined by returning null
     */
    @Test
    public void shouldReturnUndefinedVertOffsetB07() {

        Integer testInput = -64;
        BigDecimal expectedValue = null;

        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an offset value (64) above the upper bound 63 is reduced to 63 and returned as 6.3
     */
    @Test
    public void shouldReduceVertOffsetB07AboveUpperBoundToUpperBound() {

        Integer testInput = 64;
        BigDecimal expectedValue = BigDecimal.valueOf(6.3);

        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an offset below the lower bound -63 is reduced to -63 and returned as -6.3
     */
    @Test
    public void shouldIncreaseVertOffsetB07BelowLowerBoundToLowerBound() {

        Integer testInput = -65;
        BigDecimal expectedValue = BigDecimal.valueOf(-6.3);

        VertOffset_B07 testVertOffset_B07 = new VertOffset_B07(testInput);
        BigDecimal actualValue = OssOffset.genericOffset(testVertOffset_B07);
        
        assertEquals(expectedValue, actualValue);

    }

}

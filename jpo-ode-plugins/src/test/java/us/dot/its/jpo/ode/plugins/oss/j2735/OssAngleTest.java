package us.dot.its.jpo.ode.plugins.oss.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;

/**
 * -- Summary -- Test class for oss.j2735.OssAngle Checks conversion from
 * generic angle to degree angle
 * 
 * Four test cases: 1) Minimum 0 returns 0 2) Maximum 28799 returns 359.9875 3)
 * Undefined angle (indicated as 28800) returns null 4) Random generic angle
 * returns correct corresponding degree angle 5) Angle less than 0 throws
 * IllegalArgumentException 6) Angle greater than 28800 throws
 * IllegalArgumentException
 *
 */
public class OssAngleTest {

    /**
     * Tests that the minimum input angle (0) returns the minimum decimal value
     * (0)
     */
    @Test
    public void shouldReturnZeroAngle() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO;

        Angle testAngle = new Angle(testInput);
        BigDecimal actualValue = OssAngle.genericAngle(testAngle);

        assertEquals(expectedValue.longValue(), actualValue.longValue());

    }

    /**
     * Tests that the max input angle (28799) returns the max decimal value
     * (359.9875)
     */
    @Test
    public void shouldReturnMaxAngle() {

        Integer testInput = 28799;
        BigDecimal expectedValue = BigDecimal.valueOf(359.9875);

        Angle testAngle = new Angle(testInput);
        BigDecimal actualValue = OssAngle.genericAngle(testAngle);

        assertEquals(expectedValue.longValue(), actualValue.longValue());

    }

    /**
     * Tests that known input angle (14400) returns the max decimal value
     * (180.0)
     */
    @Test
    public void shouldReturnKnownAngle() {

        Integer testInput = 14400;
        BigDecimal expectedValue = BigDecimal.valueOf(180.0);

        Angle testAngle = new Angle(testInput);
        BigDecimal actualValue = OssAngle.genericAngle(testAngle);

        assertEquals(expectedValue.longValue(), actualValue.longValue());

    }

    /**
     * Tests that input angle (28800) returns (null) per ASN.1 specification: "A
     * value of 28800 shall be used when Angle is unavailable"
     */
    @Test
    public void shouldReturnNullAngle() {

        Integer testInput = 28800;
        BigDecimal expectedValue = null;

        Angle testAngle = new Angle(testInput);
        BigDecimal actualValue = OssAngle.genericAngle(testAngle);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input angle greater than 28800 throws exception
     */
    @Test
    public void shouldThrowExceptionAboveUpperBound() {

        Integer testValue = 28801;
        Angle testAngle = new Angle(testValue);

        try {
            BigDecimal actualValue = OssAngle.genericAngle(testAngle);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e.getClass().equals(IllegalArgumentException.class));
        }
    }

    /**
     * Test that an input angle less than 0 throws exception
     */
    @Test
    public void shouldThrowExceptionBelowLowerBound() {

        Integer testValue = -1;
        Angle testAngle = new Angle(testValue);

        try {
            BigDecimal actualValue = OssAngle.genericAngle(testAngle);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e.getClass().equals(IllegalArgumentException.class));
        }
    }

}

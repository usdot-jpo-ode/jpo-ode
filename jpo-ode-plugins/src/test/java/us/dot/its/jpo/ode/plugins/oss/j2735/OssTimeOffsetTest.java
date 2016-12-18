package us.dot.its.jpo.ode.plugins.oss.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;

/**
 * -- Summary --
 * Test class for OssTimeOffset
 * 
 * Verifies correct conversion from generic TimeOffset to compliant-J2735TimeOffSett
 * 
 * -- Documentation --
 * Data Element: DE_TimeOffset
 * Use: The DE_TimeOffset data element is used to convey an offset in time from a known point. It is typically used to 
 * relate a set of measurements made in the recent past, such as a set of path points. The above methodology is used 
 * when the offset is incorporated in data frames other than DF_PathHistoryPoint. Refer to the Use paragraph of 
 * DF_PathHistory for the methodology to calculate this data element for use in DF_PathHistoryPoint.
 * ASN.1 Representation:
 *    TimeOffset ::= INTEGER (1..65535) 
 *         -- LSB units of of 10 mSec, 
 *       -- with a range of 0.01 seconds to 10 minutes and 55.34 seconds 
 *       -- a value of 65534 to be used for 655.34 seconds or greater 
 *       -- a value of 65535 to be unavailable
 */
public class OssTimeOffsetTest {

    /**
     * Test that an undefined time offset flag value 65535 returns null
     */
    @Test
    public void shouldReturnUndefinedTimeOffset() {

        Integer testValue = 65535;
        BigDecimal expectedValue = null;

        TimeOffset testTimeOffset = new TimeOffset(testValue);
        BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a minimum time offset of (1) returns (0.01)
     */
    @Test
    public void shouldReturnMinimumTimeOffset() {

        Integer testValue = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.01);
        
        TimeOffset testTimeOffset = new TimeOffset(testValue);
        BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum time offset of (65534) returns (655.34)
     */
    @Test
    public void shouldReturnMaximumTimeOffset() {

        Integer testValue = 65534;
        BigDecimal expectedValue = BigDecimal.valueOf(655.34);

        TimeOffset testTimeOffset = new TimeOffset(testValue);
        BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that a known time offset of (15234) returns (152.34)
     */
    @Test
    public void shouldReturnKnownTimeOffset() {

        Integer testValue = 15234;
        BigDecimal expectedValue = BigDecimal.valueOf(152.34);

        TimeOffset testTimeOffset = new TimeOffset(testValue);
        BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that a time offset value below the lower bound (1) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionTimeOffsetBelowLowerBound() {

        Integer testValue = 0;

        TimeOffset testTimeOffset = new TimeOffset(testValue);
        
        try {
            BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

    /**
     * Test that a time offset value above the upper bound (65535) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionTimeOffsetAboveUpperBound() {

        Integer testValue = 65536;

        TimeOffset testTimeOffset = new TimeOffset(testValue);

        try {
            BigDecimal actualValue = OssTimeOffset.genericTimeOffset(testTimeOffset);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

}

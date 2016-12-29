package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B18;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B12;

/**
 * -- Summary --
 * JUnit test class for OssPathHistoryPoint
 * 
 * Verifies correct conversion from generic PathHistoryPoint to compliant-J2735PathHistoryPoint
 * 
 * -- Documentation --
 * Data Frame: DF_PathHistoryPoint
 * Use: The PathHistoryPoint data frame is used to convey a single point in the path of an object (typically 
 * a motor vehicle) described as a sequence of such position points. The sequence and number of these points 
 * (defined in another data frame) is selected to convey the desired level of accuracy and precision required 
 * by the application. The lat-long offset units used in the PathHistoryPointType data frame support units of 
 * 1/10th micro degrees of lat and long. The elevation offset units are in 10cm units. The time is expressed in 
 * units of 10 milliseconds. The PositionalAccuracy entry uses 3 elements to relate the pseudorange noise measured 
 * in the system. The heading and speed are not offset values, and follow the units defined in the ASN comments. 
 * All of these items are defined further in the relevant data entries.
 * ASN.1 Representation:
 *    PathHistoryPoint ::= SEQUENCE {
 *       latOffset OffsetLL-B18,
 *       lonOffset OffsetLL-B18,
 *       elevationOffset VertOffset-B12,
 *       timeOffset TimeOffset,
 *          -- Offset backwards in time
 *       speed Speed OPTIONAL,
 *          -- Speed over the reported period
 *       posAccuracy PositionalAccuracy OPTIONAL,
 *          -- The accuracy of this value
 *       heading CoarseHeading OPTIONAL,
 *          -- overall heading
 *       ...
 *       }
 * 
 * Data Element: DE_OffsetLL-B18
 * Use: An 18-bit delta offset in Lat or Long direction from the last point. The offset is positive to the East and 
 * to the North directions. In LSB units of 0.1 microdegrees (unless a zoom is employed). The most negative value 
 * shall be used to indicate an unknown value. It should be noted that while the precise range of the data element 
 * in degrees is a constant value, the equivalent length in meters will vary with the position on the earth that is 
 * used. The above methodology is used when the offset is incorporated in data frames other than DF_PathHistoryPoint. 
 * Refer to the Use paragraph of DF_PathHistory for the methodology to calculate this data element for use in 
 * DF_PathHistoryPoint.
 * ASN.1 Representation:
 *    OffsetLL-B18 ::= INTEGER (-131072..131071) 
 *       -- A range of +- 0.0131071 degrees 
 *       -- The value +131071 shall be used for values >= than +0.0131071 degrees 
 *       -- The value -131071 shall be used for values <= than -0.0131071 degrees 
 *       -- The value -131072 shall be used unknown 
 *       -- In LSB units of 0.1 microdegrees (unless a zoom is employed)
 */
public class OssPathHistoryPointTest {
    
    // latOffset tests
    /**
     * Test that the undefined flag lat offset value (-131072) returns (null)
     */
    @Test
    public void shouldCreateUndefinedLatOffset() {
        
        Integer testInput = -131072;
        BigDecimal expectedValue = null;
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        //Speed testSpeed = null;
        //PositionalAccuracy testPositionalAccuracy = null;
        //CoarseHeading testHeading = null;
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        //testPathHistoryPoint.setSpeed(testSpeed);
        //testPathHistoryPoint.setPosAccuracy(testPositionalAccuracy);
        //testPathHistoryPoint.setHeading(testHeading);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that the minimum lat offset value (-131071) returns (-0.0131071)
     */
    @Test
    public void shouldCreateMinimumLatOffset() {
        
        Integer testInput = -131071;
        BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case minimum lat offset value (-131070) returns (-0.0131070)
     */
    @Test
    public void shouldCreateCornerCaseMinimumLatOffset() {
        
        Integer testInput = -131070;
        BigDecimal expectedValue = BigDecimal.valueOf(-0.0131070).setScale(7);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a lat offset value (1927) returns (0.0001927)
     */
    @Test
    public void shouldCreateMiddleLatOffset() {
        
        Integer testInput = 1927;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0001927);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case maximum lat offset value (131070) returns (0.0131070)
     */
    @Test
    public void shouldCreateCornerCaseMaximumLatOffset() {
        
        Integer testInput = 131070;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0131070).setScale(7);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that the maximum lat offset value (131071) returns (0.0131071)
     */
    @Test
    public void shouldCreateMaximumLatOffset() {
        Integer testInput = 131071;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a lat offset value (-131073) below the lower bound (-131071) is increased to the lower bound
     */
    @Test
    public void shouldIncreaseLatOffsetBelowLowerBound() {
        
        Integer testInput = -131073;
        BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a lat offset value (131073) above the upper bound (131071) is reduced to the upper bound
     */
    @Test
    public void shouldReduceLatOffsetAboveUpperBound() {
        
        Integer testInput = 131073;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(0);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        BigDecimal actualValue = OssPathHistoryPoint
                .genericPathHistoryPoint(testPathHistoryPoint)
                .getLatOffset();
              
        assertEquals(expectedValue, actualValue);
    }
    // lonOffset tests
    // elevationOffset tests
    // timeOffset tests
    // speed tests
    // posAccuracy tests
    // heading tests

}

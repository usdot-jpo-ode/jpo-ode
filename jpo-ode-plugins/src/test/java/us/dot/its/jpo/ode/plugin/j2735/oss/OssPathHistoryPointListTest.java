package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B18;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPointList;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B12;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

/**
 * -- Summary --
 * JUnit test class for OssPathHistoryPointList
 * 
 * Verifies correct conversion from generic PathHistoryPointList to compliant-J2735PathHistoryPointList
 * 
 * Notes:
 * - This test class a simple test of list creation
 * - Actual value tests are performed by OssPathHistoryPointTest
 * 
 * -- Documentation --
 * Data Frame: DF_PathHistoryPointList
 * Use: The PathHistoryPointList data frame consists of a list of PathHistoryPoint entries. Note that 
 * implementations may use fewer than the maximum number of path history points allowed.
 * ASN.1 Representation:
 * PathHistoryPointList ::= SEQUENCE (SIZE(1..23)) OF PathHistoryPoint
 *
 */
public class OssPathHistoryPointListTest {
    
    /**
     * Test that an empty path history point list can be created
     */
    @Test
    public void shouldCreateEmptyPathHistoryPointList() {
        
        PathHistoryPointList emptyPointList = new PathHistoryPointList();
        
        List<J2735PathHistoryPoint> actualList = OssPathHistoryPointList.genericPathHistoryPointList(emptyPointList);
        
        assertTrue("Expected isEmpty() to return true", actualList.isEmpty());
        assertEquals("Expected size() to return 0", 0, actualList.size());
        
    }
    
    /**
     * Test that a path history point list with one point is correctly created
     */
    @Test
    public void shouldCreateListWithOneEntry() {
        
        Integer testInput = 1927;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0001927);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(1);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        PathHistoryPointList testPointList = new PathHistoryPointList();
        testPointList.add(testPathHistoryPoint);
        
        List<J2735PathHistoryPoint> actualList = OssPathHistoryPointList.genericPathHistoryPointList(testPointList);
        
        BigDecimal actualValue = actualList.get(0).getLatOffset();
        
        assertEquals(expectedValue, actualValue);
        assertEquals("Expected size() to return 1", 1, actualList.size());
        assertFalse("Expected isEmpty() to return false", actualList.isEmpty());
        
    }
    
    /**
     * Test that a path history point list with the max number of entries (23) can be successfully created
     */
    @Test
    public void shouldCreateListWithMaxNumberEntries() {
        
        Integer testInput = 1927;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0001927);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(1);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        PathHistoryPointList testPointList = new PathHistoryPointList();
        
        for (int i = 0; i < 23; i++) {
            testPointList.add(testPathHistoryPoint);
        }
        
        List<J2735PathHistoryPoint> actualList = OssPathHistoryPointList.genericPathHistoryPointList(testPointList);
        
        BigDecimal actualValue = actualList.get(22).getLatOffset();
        
        assertEquals(expectedValue, actualValue);
        assertEquals("Expected size() to return 23", 23, actualList.size());
        assertFalse("Expected isEmpty() to return false", actualList.isEmpty());
        
    }
    
    /**
     * Test that a path history list with (24) entries is returned with only the first (23) entries
     */
    @Test
    public void shouldReduceListLengthAboveUpperBound() {
        
        Integer testInput = 1927;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0001927);
        
        OffsetLL_B18 testLatOffset = new OffsetLL_B18(testInput);
        OffsetLL_B18 testLonOffset = new OffsetLL_B18(0);
        VertOffset_B12 testElevationOffset = new VertOffset_B12(0);
        TimeOffset testTimeOffset = new TimeOffset(1);
        
        PathHistoryPoint testPathHistoryPoint = new PathHistoryPoint();
        testPathHistoryPoint.setLatOffset(testLatOffset);
        testPathHistoryPoint.setLonOffset(testLonOffset);
        testPathHistoryPoint.setElevationOffset(testElevationOffset);
        testPathHistoryPoint.setTimeOffset(testTimeOffset);
        
        PathHistoryPointList testPointList = new PathHistoryPointList();
        
        for (int i = 0; i < 24; i++) {
            testPointList.add(testPathHistoryPoint);
        }
        
        List<J2735PathHistoryPoint> actualList = OssPathHistoryPointList.genericPathHistoryPointList(testPointList);
        
        BigDecimal actualValue = actualList.get(22).getLatOffset();
        
        assertEquals(expectedValue, actualValue);
        assertEquals("Expected size() to return 23", 23, actualList.size());
        assertFalse("Expected isEmpty() to return false", actualList.isEmpty());
    }

}

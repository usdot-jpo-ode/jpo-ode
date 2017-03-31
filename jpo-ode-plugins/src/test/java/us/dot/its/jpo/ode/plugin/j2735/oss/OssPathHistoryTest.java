package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.FullPositionVector;
import us.dot.its.jpo.ode.j2735.dsrc.GNSSstatus;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B18;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistory;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPointList;
import us.dot.its.jpo.ode.j2735.dsrc.TimeConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B12;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistory;


/**
 * -- Summary --
 * JUnit test class for OssPathHistory
 * 
 * Verifies correct conversion from generic PathHistory to compliant-J2735PathHistory
 * 
 * This is a trivial test that verifies a PathHistory object can be successfully created.
 * 
 * FullPositionVector is tested by OssFullPositionVectorTest
 * GNSSstatus is tested by OssGNSSstatusTest
 * PathHistoryPointList is tested by OssPathHistoryPointListTest
 * 
 * -- Documentation --
 * Data Frame: DF_PathHistory
 * ASN.1 Representation:
 *    PathHistory ::= SEQUENCE {
 *       initialPosition FullPositionVector OPTIONAL,
 *       currGNSSstatus GNSSstatus OPTIONAL,
 *       crumbData PathHistoryPointList,
 *       ...
 *       }
 */
public class OssPathHistoryTest {
    
    /**
     * Test that an empty path history object can be created
     */
    @Test
    public void shouldCreateEmptyPathHistory() {
        
        PathHistoryPointList testPathHistoryPointList = new PathHistoryPointList();
        
        PathHistory testPathHistory = new PathHistory(testPathHistoryPointList);
        
        J2735PathHistory actualPathHistory = OssPathHistory.genericPathHistory(testPathHistory);
        
        assertTrue(actualPathHistory.getCrumbData().isEmpty());
    }
    
    /**
     * Test that a path history object with a known path history point and path history point 
     * list can be created
     */
    @Test
    public void shouldCreatePathHistoryWithKnownPathHistoryPoint() {
        
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
        
        PathHistory testPathHistory = new PathHistory();
        testPathHistory.crumbData = testPointList;
        
        J2735PathHistory actualPathHistory = OssPathHistory.genericPathHistory(testPathHistory);
        
        BigDecimal actualValue = actualPathHistory.getCrumbData().get(0).getLatOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a path history object with a known full position vector can be created
     */
    @Test
    public void shouldCreatePathHistoryWithKnownFullPositionVector() {
        
        Integer testInput = -1799999998;
        BigDecimal expectedValue = BigDecimal.valueOf(-179.9999998);
        
        Longitude testLong = new Longitude(testInput);
        Latitude testLat = new Latitude(0);
        Elevation testElevation = new Elevation(0);
        TimeConfidence testTimeConfidence = new TimeConfidence(0);
        
        FullPositionVector testFPV = new FullPositionVector();
        testFPV.set_long(testLong);
        testFPV.setLat(testLat);
        testFPV.setElevation(testElevation);
        testFPV.setTimeConfidence(testTimeConfidence);
        
        PathHistory testPathHistory = new PathHistory();
        testPathHistory.crumbData = new PathHistoryPointList();
        testPathHistory.initialPosition = testFPV;
        
        J2735PathHistory actualPathHistory = OssPathHistory.genericPathHistory(testPathHistory);
        
        BigDecimal actualValue = actualPathHistory.getInitialPosition().getPosition().getLongitude();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    @Test
    public void shouldCreatePathHistoryWithKnownGNSSstatus() {
        
        Integer testInput = 0b01000010;
        String elementTested1 = "isHealthy";
        String elementTested2 = "localCorrectionsPresent";
        
        byte[] testInputBytes = {testInput.byteValue()};
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        PathHistoryPointList testPathHistoryPointList = new PathHistoryPointList();
        
        PathHistory testPathHistory = new PathHistory();
        testPathHistory.crumbData = testPathHistoryPointList;
        testPathHistory.currGNSSstatus = testGNSSstatus;
        
        J2735PathHistory actualPathHistory = OssPathHistory.genericPathHistory(testPathHistory);
        
        for (Map.Entry<String, Boolean> curVal : actualPathHistory.getCurrGNSSstatus().entrySet()) {
            if(curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssPathHistory> constructor = OssPathHistory.class.getDeclaredConstructor();
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

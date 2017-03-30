package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDetection;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDirection;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDistance;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAccelerationThreshold;
import us.dot.its.jpo.ode.plugin.j2735.J2735VertEvent;

/**
 * -- Summary --
 * JUnit test class for OssObstacleDetection
 * 
 * Verifies correct conversion from generic ObstacleDetection to compliant J2735ObstactleDetection
 * 
 * Notes:
 * Tested elements:
 * - obDist
 * - vertEvent
 * 
 * Untested elements: (handled by other testing classes)
 * - description is a list of ITIS codes and are not part of the ASN specifications
 * - obDirect is tested by OssAngleTest
 * - locationDetails is tested by OssNamedNumberTest
 * - dateTime is tested by OssDDateTimeTest
 * 
 * -- Documentation --
 * Data Frame: DF_ObstacleDetection
 * Use: The DF_ObstacleDetection data frame is used to relate basic location information about a detect obstacle or 
 * a road hazard in a vehicles path.
 * ASN.1 Representation:
 *    ObstacleDetection ::= SEQUENCE {
 *       obDist ObstacleDistance, -- Obstacle Distance
 *       obDirect ObstacleDirection, -- Obstacle Direction
 *       description ITIS.ITIScodes(523..541) OPTIONAL,
 *          -- Uses a limited set of ITIS codes
 *       locationDetails ITIS.GenericLocations OPTIONAL,
 *       dateTime DDateTime, -- Time detected
 *       vertEvent VerticalAccelerationThreshold OPTIONAL,
 *          -- Any wheels which have
 *          -- exceeded the acceleration point
 *       ...
 *       }
 *       
 * Data Element: DE_ObstacleDistance
 * Use: This data element draws from the output of a forward sensing system to report the presence of an obstacle 
 * and its measured distance from the vehicle detecting and reporting the obstacle. This information can be used 
 * by road authorities to investigate and remove the obstacle, as well as by other vehicles in advising drivers 
 * or on-board systems of the obstacle location. Distance is expressed in meters.
 * ASN.1 Representation:
 *    ObstacleDistance ::= INTEGER (0..32767) -- LSB units of meters
 * 
 * Data Element: DE_VerticalAccelerationThreshold
 * Use: A bit string enumerating when a preset threshold for vertical acceleration is exceeded at each wheel.
 * The "Wheel that exceeded Vertical G Threshold" data element is intended to inform Probe Data Users which vehicle 
 * wheel has exceeded a pre-determined threshold of a percent change in vertical G acceleration at the time a Probe 
 * Data snapshot was taken. This element is primarily intended to be used in the detection of potholes and similar 
 * road abnormalities. This element only provides information for four-wheeled vehicles. The element informs the user 
 * if the vehicle is not equipped with accelerometers on its wheels or that the system is off. When a wheel does 
 * exceed the threshold, the element provides details on the particular wheel by specifying Left Front, Left Rear, 
 * Right Front and Right Rear.
 * ASN.1 Representation:
 *    VerticalAccelerationThreshold ::= BIT STRING {
 *       notEquipped (0), -- Not equipped or off
 *       leftFront (1), -- Left Front Event
 *       leftRear (2), -- Left Rear Event
 *       rightFront (3), -- Right Front Event
 *       rightRear (4) -- Right Rear Event
 *       } (SIZE(5))
 *    
 */
public class OssObstacleDetectionTest {
    
    private DDateTime mockDDateTime;
    
    /**
     * Create a mock DDateTime object before tests are run. This is necessary to prevent null pointer exception 
     * upon OssObstacleDetection.genericObstacleDetection() method call.
     */
    @Before
    public void setUpMockDDateTime() {

        DYear year = new DYear(0);
        DMonth month = new DMonth(0);
        DDay day = new DDay(0);
        DHour hour = new DHour(0);
        DMinute minute = new DMinute(0);
        DSecond second = new DSecond(0);
        DOffset offset = new DOffset(0);
        
        mockDDateTime = new DDateTime(year, month, day, hour, minute, second, offset);
        
    }

    // Distance tests
    
    /**
     * Test that minimum distance value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumDistance() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        Integer actualValue = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum distance value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumDistance() {

        Integer testInput = 1;
        Integer expectedValue = 1;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        Integer actualValue = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known middle distance value (15012) returns (15012)
     */
    @Test
    public void shouldReturnMiddleDistance() {
        
        Integer testInput = 15012;
        Integer expectedValue = 15012;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        Integer actualValue = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum distance value (32766) returns (32766)
     */
    @Test
    public void shouldReturnCornerCaseMaximumDistance() {

        Integer testInput = 32766;
        Integer expectedValue = 32766;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        Integer actualValue = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum distance value (32767) returns (32767)
     */
    @Test
    public void shouldReturnMaximumDistance() {

        Integer testInput = 32767;
        Integer expectedValue = 32767;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        Integer actualValue = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a distance value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionDistanceBelowLowerBound() {

        Integer testInput = -1;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        try {
            OssObstacleDetection
                    .genericObstacleDetection(testObstacleDetection);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a distance value (32768) above the upper bound (32767) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionDistanceAboveUpperBound() {
        
        Integer testInput = 32768;

        Integer testVertEventBitString = 0b00000;
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(testInput);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        try {
            OssObstacleDetection
                    .genericObstacleDetection(testObstacleDetection);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    // VerticalAccelerationEvent tests
    
    /**
     * Test bitstring value "00000" returns "false" for all vert event flags
     */
    @Test
    public void shouldCreateAllOffVertEvent() {
        
        Integer testInput = 0b00000;
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }
        
    }
    
    /**
     * Test bitstring input "11111" returns "true" for all vert event flags
     */
    @Test
    public void shouldCreateAllOnVertEvent() {
        
        Integer testInput = 0b11111;
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
        
    }
    
    /**
     * Test input bitstring value "00001" returns true for "notEquipped" only
     */
    @Test
    public void shouldCreateNotEquippedVertEvent() {
        
        Integer testInput = 0b00001;
        String elementTested = "notEquipped";
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bitstring "00010" returns "true" for "leftFront" only
     */
    @Test
    public void shouldCreateLeftFrontVertEvent() {
        
        Integer testInput = 0b00010;
        String elementTested = "leftFront";
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test input bitstring value "01000" returns "true" for "rightFront" only
     */
    @Test
    public void shouldCreateRightFrontVertEvent() {
        
        Integer testInput = 0b01000;
        String elementTested = "rightFront";
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test input bitstring value "10000" returns "true" for "rightRear" only
     */
    @Test
    public void shouldCreateRightRearVertEvent() {
        
        Integer testInput = 0b10000;
        String elementTested = "rightRear";
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test input bitstring value "01010" returns "true" for "leftFront" and "rightFront" only
     */
    @Test
    public void shouldCreateTwoVertEvents() {
        
        Integer testInput = 0b01010;
        String elementTested1 = "leftFront";
        String elementTested2 = "rightFront";
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setVertEvent(testVertEvent);
        testObstacleDetection.setDateTime(mockDDateTime);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssObstacleDetection > constructor = OssObstacleDetection.class.getDeclaredConstructor();
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

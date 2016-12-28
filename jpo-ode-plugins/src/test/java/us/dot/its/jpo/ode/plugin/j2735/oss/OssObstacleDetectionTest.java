package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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

    // distance tests
    
    // vert event tests
    
    /**
     * Test bitstring value "00000" returns "false" for all vert event flags
     */
    @Test
    public void shouldCreateAllOffVertEvent() {
        
        Integer testInput = 0b00000;
        
        byte[] testVertEventBytes = {testInput.byteValue()};
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);

        // Required elements
        ObstacleDistance testDist = new ObstacleDistance(0);
        ObstacleDirection testDirect = new ObstacleDirection(0);
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection();
        testObstacleDetection.setObDist(testDist);
        testObstacleDetection.setObDirect(testDirect);
        testObstacleDetection.setDateTime(mockDDateTime);
        testObstacleDetection.setVertEvent(testVertEvent);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .vertEvent;
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }
        
    }
    
    @Ignore
    @Test
    public void shouldCreateAllOnVertEvent() {
        
    }
    
    @Ignore
    @Test
    public void shouldCreateNotEquippedVertEvent() {
        
        Integer testVertEventBitString = 0b00000;
        String elementTested = "notEquipped";
        
        
        byte[] testVertEventBytes = {testVertEventBitString.byteValue()};
        
        VerticalAccelerationThreshold testVertEvent = new VerticalAccelerationThreshold(testVertEventBytes);
        
        //ObstacleDistance testDist = new ObstacleDistance();
        //ObstacleDirection testDirect = new ObstacleDirection();
        //DDateTime testDateTime = new DDateTime();
        
        ObstacleDistance testDist = null;
        ObstacleDirection testDirect = null;
        DDateTime testDateTime = null;
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection(testDist, testDirect, testDateTime);
        testObstacleDetection.setVertEvent(testVertEvent);
        
        J2735VertEvent actualVertEvent = OssObstacleDetection
                .genericObstacleDetection(testObstacleDetection)
                .vertEvent;
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Ignore
    @Test
    public void shouldCreateLeftFrontVertEvent() {
        
    }
    
    @Ignore
    @Test
    public void shouldCreateRightFrontVertEvent() {
        
    }
    
    @Ignore
    @Test
    public void shouldCreateRightRearVertEvent() {
        
    }
    
    @Ignore
    @Test
    public void shouldCreateTwoVertEvents() {
        
    }

}

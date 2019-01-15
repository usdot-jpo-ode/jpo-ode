/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary --
 * JUnit test class for ObstacleDetectionBuilder
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
public class ObstacleDetectionBuilderTest {
    

    // Distance tests
    
    /**
     * Test that minimum distance value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumDistance() {
        

        Integer expectedValue = 0;
    
        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }

    
        Integer actualValue = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum distance value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumDistance() {


        Integer expectedValue = 1;
        
        int obDist = 1;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known middle distance value (15012) returns (15012)
     */
    @Test
    public void shouldReturnMiddleDistance() {
          
     Integer expectedValue = 15012;
        
        int obDist = 15012;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum distance value (32766) returns (32766)
     */
    @Test
    public void shouldReturnCornerCaseMaximumDistance() {

     
     Integer expectedValue = 32766;
        
        int obDist = 32766;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum distance value (32767) returns (32767)
     */
    @Test
    public void shouldReturnMaximumDistance() {


     Integer expectedValue = 32767;
        
        int obDist = 32767;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getObDist();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a distance value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionDistanceBelowLowerBound() {


        
        int obDist = -1;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "0b00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        try {
            ObstacleDetectionBuilder
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
        

        int obDist = 32768;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "0b00000";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        try {
            ObstacleDetectionBuilder
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
        
     
           int obDist = 0;
           int obDirect = 0;
           String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
           int description = 523;
           String vertEvent = "00000";
           
           JsonNode testObstacleDetection = null;
           try {
              testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                      "<obstacleDetection><obDist>" + obDist + "</obDist>"
                    + "<obDirect>" + obDirect + "</obDirect>"
                    + "<dateTime>" + dateTime + "</dateTime>"
                    + "<description>" + description + "</description>"
                   // + "<locationDetails>" + locationDetails + "</locationDetails>"
                    + "<vertEvent>" + vertEvent + "</vertEvent>"
                    + "</obstacleDetection>"
                    , JsonNode.class);
           } catch (XmlUtilsException e) {
              fail("XML parsing error:" + e);
           }
           
        
           J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
        
        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "11111";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
                .genericObstacleDetection(testObstacleDetection)
                .getVertEvent();
        
        for (Map.Entry<String, Boolean> curVal : actualVertEvent.entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
        
    }
    
    /**
     * Test input value "00001" returns true for "notEquipped" only
     */
    @Test
    public void shouldCreateNotEquippedVertEvent() {
        
        
        String elementTested = "notEquipped";
        

        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00001";
        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
        
        String elementTested = "leftFront";

        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "00010";

        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
        

        String elementTested = "rightFront";

     
        
        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "01000";

        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
        
        String elementTested = "rightRear";
        

        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        String locationDetails = "<name>on-bridges</name><value>7937</value>";
        String vertEvent = "10000";

        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 //+ "<description></description>"
                 + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
        
        String elementTested1 = "leftFront";
        String elementTested2 = "rightFront";
        
        
        int obDist = 0;
        int obDirect = 0;
        String dateTime = "<year>0</year><month>0</month><day>0</day><hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>";
        int description = 523;
        String vertEvent = "01010";

        
        JsonNode testObstacleDetection = null;
        try {
           testObstacleDetection = (JsonNode) XmlUtils.fromXmlS(
                   "<obstacleDetection><obDist>" + obDist + "</obDist>"
                 + "<obDirect>" + obDirect + "</obDirect>"
                 + "<dateTime>" + dateTime + "</dateTime>"
                 + "<description>" + description + "</description>"
                // + "<locationDetails>" + locationDetails + "</locationDetails>"
                 + "<vertEvent>" + vertEvent + "</vertEvent>"
                 + "</obstacleDetection>"
                 , JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735BitString actualVertEvent = ObstacleDetectionBuilder
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
      Constructor<ObstacleDetectionBuilder > constructor = ObstacleDetectionBuilder.class.getDeclaredConstructor();
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

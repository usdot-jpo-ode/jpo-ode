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
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * -- Summary --
 * JUnit test class for GNSSstatus
 * 
 * Verifies correct conversion from JsonNode to compliant-J2735GNSSstatus
 * 
 * -- Documentation --
 * Data Element: DE_GNSSstatus
 * Use: The DE_GNSSstatus data element is used to relate the current state of a GPS/GNSS rover or base system 
 * in terms of its general health, lock on satellites in view, and use of any correction information. Various 
 * bits can be asserted (made to a value of one) to reflect these values. A GNSS set with unknown health and 
 * no tracking or corrections would be represented by setting the unavailable bit to one. A value of zero shall 
 * be used when a defined data element is unavailable. The term "GPS" in any data element name in this standard 
 * does not imply that it is only to be used for GPS-type GNSS systems.
 * ASN.1 Representation:
 *    GNSSstatus ::= BIT STRING {
 *       unavailable (0), -- Not Equipped or unavailable
 *       isHealthy (1),
 *       isMonitored (2),
 *       baseStationType (3), -- Set to zero if a moving base station,
 *          -- or if a rover device (an OBU),
 *          -- set to one if it is a fixed base station
 *       aPDOPofUnder5 (4), -- A dilution of precision greater than 5
 *       inViewOfUnder5 (5), -- Less than 5 satellites in view
 *       localCorrectionsPresent (6), -- DGPS type corrections used
 *       networkCorrectionsPresent (7) -- RTK type corrections used
 *       } (SIZE(8))
 */
public class GNSSstatusBuilderTest {

    /**
     * Test input bit string "00000000" returns "false" for all flag values
     */
    @Test
    public void shouldReturnAllOffGNSSstatus() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("00000000");
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }
        
    }

    public static ObjectNode buildTestGNSSstatus(String testInput) {
      ObjectNode testGNSSstatus = JsonUtils.newNode()
          .put(GNSSstatusBuilder.GNSS_STATUS, testInput);
      return testGNSSstatus;
    }
    
    /**
     * Test input bit string "11111111" returns "true" for all flag values
     */
    @Test
    public void shouldReturnAllOnGNSSstatus() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("11111111");
       
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
    }
    
    /**
     * Test input bit string "10000000" returns "true" for "unavailable" only
     */
    @Test
    public void shouldReturnGNSSstatusUnavailable() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("10000000");
        String elementTested = "unavailable";
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "00000010" returns "true" for "isHealthy" only
     */
    @Test
    public void shouldReturnGNSSstatusIsHealthy() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("01000000");
        String elementTested = "isHealthy";
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "01000000" returns "true" for "localCorrectionsPresent" only
     */
    @Test
    public void shouldReturnGNSSstatusLocalCorrectionsPresent() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("00000010");
        String elementTested = "localCorrectionsPresent";
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "10000000" returns "true" for "networkCorrectionsPresent" only
     */
    @Test
    public void shouldReturnGNSSstatusNetworkCorrectionsPresent() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("00000001");
        String elementTested = "networkCorrectionsPresent";
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test input bit string "01000010" returns "true" for "isHealthy" and "localCorrectionsPresent" only
     */
    @Test
    public void shouldReturnTwoGNSSstatus() {
        
        JsonNode testGNSSstatus = buildTestGNSSstatus("01000010");
        String elementTested1 = "isHealthy";
        String elementTested2 = "localCorrectionsPresent";
        
        J2735GNSSstatus actualGNSSstatus = GNSSstatusBuilder.genericGNSSstatus(testGNSSstatus);
        
        for (Map.Entry<String, Boolean> curVal : actualGNSSstatus.entrySet()) {
            if(curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<GNSSstatusBuilder> constructor = GNSSstatusBuilder.class.getDeclaredConstructor();
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


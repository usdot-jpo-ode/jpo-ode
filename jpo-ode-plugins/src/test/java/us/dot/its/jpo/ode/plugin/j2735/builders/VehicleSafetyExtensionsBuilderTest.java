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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistory;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathPrediction;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;
import us.dot.its.jpo.ode.util.JsonUtils;

public class VehicleSafetyExtensionsBuilderTest {

   @Test
   public void testEvents() throws BsmPart2ContentBuilderException {

      // set bits 1, 4, 9
      // expect respectively: eventStopLineViolation,
      // eventStabilityControlactivated, eventWipersChanged

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("events", "0100100001000");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      J2735BitString actualEvents = ((J2735VehicleSafetyExtensions) outputContent.getValue()).getEvents();

      // bit 0
      assertFalse("Incorrect eventHazardLights", actualEvents.get("eventHazardLights"));

      // bit 1
      assertTrue("Incorrect eventStopLineViolation", actualEvents.get("eventStopLineViolation"));

      // bit 2
      assertFalse("Incorrect eventABSactivated", actualEvents.get("eventABSactivated"));

      // bit 3
      assertFalse("Incorrect eventTractionControlLoss", actualEvents.get("eventTractionControlLoss"));

      // bit 4
      assertTrue("Incorrect eventStabilityControlactivated", actualEvents.get("eventStabilityControlactivated"));

      // bit 9
      assertTrue("Incorrect eventWipersChanged", actualEvents.get("eventWipersChanged"));

   }

   @Test
   public void testLights() throws BsmPart2ContentBuilderException {

      // set bits 2, 3, 5
      // expect respectively: leftTurnSignalOn,
      // rightTurnSignalOn, automaticLightControlOn

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("lights", "001101000");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      J2735BitString actualLights = ((J2735VehicleSafetyExtensions) outputContent.getValue()).getLights();

      // bit 0
      assertFalse("Incorrect lowBeamHeadlightsOn", actualLights.get("lowBeamHeadlightsOn"));
      // bit 2
      assertTrue("Incorrect leftTurnSignalOn", actualLights.get("leftTurnSignalOn"));
      // bit 3
      assertTrue("Incorrect rightTurnSignalOn", actualLights.get("rightTurnSignalOn"));
      // bit 4
      assertFalse("Incorrect hazardSignalOn", actualLights.get("hazardSignalOn"));
      // bit 5
      assertTrue("Incorrect automaticLightControlOn", actualLights.get("automaticLightControlOn"));
      // bit 8
      assertFalse("Incorrect parkingLightsOn", actualLights.get("parkingLightsOn"));
   }

   @Test
   public void testPathHistory(@Capturing PathHistoryBuilder capturingPathHistoryBuilder)
         throws BsmPart2ContentBuilderException {

      new Expectations() {
         {
            PathHistoryBuilder.genericPathHistory((JsonNode) any);
            result = new J2735PathHistory();
         }
      };

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("pathHistory", "something");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      assertNotNull(((J2735VehicleSafetyExtensions) outputContent.getValue()).getPathHistory());
   }

   @Test
   public void testPathPrediction(@Capturing PathPredictionBuilder capturingPathPredictionBuilder)
         throws BsmPart2ContentBuilderException {

      new Expectations() {
         {
            PathPredictionBuilder.genericPathPrediction((JsonNode) any);
            result = new J2735PathPrediction();
         }
      };

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("pathPrediction", "something");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      assertNotNull(((J2735VehicleSafetyExtensions) outputContent.getValue()).getPathPrediction());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<VehicleSafetyExtensionsBuilder> constructor = VehicleSafetyExtensionsBuilder.class
            .getDeclaredConstructor();
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

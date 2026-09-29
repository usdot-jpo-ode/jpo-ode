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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("events", "0100100001000");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      J2735BitString actualEvents = ((J2735VehicleSafetyExtensions) outputContent.getValue()).getEvents();

      assertFalse(actualEvents.get("eventHazardLights"), "Incorrect eventHazardLights");
      assertTrue(actualEvents.get("eventStopLineViolation"), "Incorrect eventStopLineViolation");
      assertFalse(actualEvents.get("eventABSactivated"), "Incorrect eventABSactivated");
      assertFalse(actualEvents.get("eventTractionControlLoss"), "Incorrect eventTractionControlLoss");
      assertTrue(actualEvents.get("eventStabilityControlactivated"), "Incorrect eventStabilityControlactivated");
      assertTrue(actualEvents.get("eventWipersChanged"), "Incorrect eventWipersChanged");
   }

   @Test
   public void testLights() throws BsmPart2ContentBuilderException {

      J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("lights", "001101000");

      VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

      J2735BitString actualLights = ((J2735VehicleSafetyExtensions) outputContent.getValue()).getLights();

      assertFalse(actualLights.get("lowBeamHeadlightsOn"), "Incorrect lowBeamHeadlightsOn");
      assertTrue(actualLights.get("leftTurnSignalOn"), "Incorrect leftTurnSignalOn");
      assertTrue(actualLights.get("rightTurnSignalOn"), "Incorrect rightTurnSignalOn");
      assertFalse(actualLights.get("hazardSignalOn"), "Incorrect hazardSignalOn");
      assertTrue(actualLights.get("automaticLightControlOn"), "Incorrect automaticLightControlOn");
      assertFalse(actualLights.get("parkingLightsOn"), "Incorrect parkingLightsOn");
   }

   @Test
   public void testPathHistory() throws BsmPart2ContentBuilderException {
      try (MockedStatic<PathHistoryBuilder> pathHistoryStatic = Mockito.mockStatic(PathHistoryBuilder.class)) {
         pathHistoryStatic.when(() -> PathHistoryBuilder.genericPathHistory(any(JsonNode.class)))
               .thenReturn(new J2735PathHistory());

         J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

         ObjectNode testInputNode = JsonUtils.newNode();
         testInputNode.put("pathHistory", "something");

         VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

         assertNotNull(((J2735VehicleSafetyExtensions) outputContent.getValue()).getPathHistory());
      }
   }

   @Test
   public void testPathPrediction() throws BsmPart2ContentBuilderException {
      try (MockedStatic<PathPredictionBuilder> pathPredictionStatic = Mockito.mockStatic(PathPredictionBuilder.class)) {
         pathPredictionStatic.when(() -> PathPredictionBuilder.genericPathPrediction(any(JsonNode.class)))
               .thenReturn(new J2735PathPrediction());

         J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

         ObjectNode testInputNode = JsonUtils.newNode();
         testInputNode.put("pathPrediction", "something");

         VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(outputContent, testInputNode);

         assertNotNull(((J2735VehicleSafetyExtensions) outputContent.getValue()).getPathPrediction());
      }
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

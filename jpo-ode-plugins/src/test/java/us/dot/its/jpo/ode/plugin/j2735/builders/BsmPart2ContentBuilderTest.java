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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmPart2ContentBuilderTest {

   private MockedStatic<VehicleSafetyExtensionsBuilder> vseStatic;
   private MockedStatic<SpecialVehicleExtensionsBuilder> speStatic;
   private MockedStatic<SupplementalVehicleExtensionsBuilder> sveStatic;

   @BeforeEach
   void stubOutBuilders() {
      vseStatic = Mockito.mockStatic(VehicleSafetyExtensionsBuilder.class);
      speStatic = Mockito.mockStatic(SpecialVehicleExtensionsBuilder.class);
      sveStatic = Mockito.mockStatic(SupplementalVehicleExtensionsBuilder.class);
   }

   @AfterEach
   void releaseStaticMocks() {
      vseStatic.close();
      speStatic.close();
      sveStatic.close();
   }

   @Test
   public void testNullPart2IDReturnsNullContent() throws BsmPart2ContentBuilderException {
      assertNull(BsmPart2ContentBuilder.genericPart2Content(JsonUtils.newNode().put("partII-Value", "something")));
   }

   @Test
   public void testNullPart2ValueReturnsNullContent() throws BsmPart2ContentBuilderException {
      assertNull(BsmPart2ContentBuilder.genericPart2Content(JsonUtils.newNode().put("partII-id", "something")));
   }

   @Test
   public void testId0ReturnsVehicleSafetyExtensions() throws BsmPart2ContentBuilderException {
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("partII-Id", 0);
      testInput.put("partII-Value", "something");

      BsmPart2ContentBuilder.genericPart2Content(testInput);

      vseStatic.verify(() ->
            VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(
                  any(), any()), times(1));
      speStatic.verify(() ->
            SpecialVehicleExtensionsBuilder.evaluateSpecialVehicleExt(
                  any(), any()), never());
      sveStatic.verify(() ->
            SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
                  any(), any()), never());
   }

   @Test
   public void testId1ReturnsSpecialVehicleExtensions() throws BsmPart2ContentBuilderException {
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("partII-Id", 1);
      testInput.put("partII-Value", "something");

      BsmPart2ContentBuilder.genericPart2Content(testInput);

      vseStatic.verify(() ->
            VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(
                  any(), any()), never());
      speStatic.verify(() ->
            SpecialVehicleExtensionsBuilder.evaluateSpecialVehicleExt(
                  any(), any()), times(1));
      sveStatic.verify(() ->
            SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
                  any(), any()), never());
   }

   @Test
   public void testId2ReturnsSupplementalVehicleExtensions() throws BsmPart2ContentBuilderException {
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("partII-Id", 2);
      testInput.put("partII-Value", "something");

      BsmPart2ContentBuilder.genericPart2Content(testInput);

      vseStatic.verify(() ->
            VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(
                  any(), any()), never());
      speStatic.verify(() ->
            SpecialVehicleExtensionsBuilder.evaluateSpecialVehicleExt(
                  any(), any()), never());
      sveStatic.verify(() ->
            SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
                  any(), any()), times(1));
   }

   @Test
   public void testExceptions() {
      new BsmPart2ContentBuilderException("message");
      new BsmPart2ContentBuilderException("message", new IOException("123"));
   }

   @Test
   public void testBuildGenericPart2() throws BsmPart2ContentBuilderException {
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("partII-Id", 2);
      testInput.put("partII-Value", "something");

      List<JsonNode> inputList = new ArrayList<>();
      inputList.add(testInput);

      List<J2735BsmPart2Content> outputList = new ArrayList<>();

      BsmPart2ContentBuilder.buildGenericPart2(inputList, outputList);

      assertEquals(1, outputList.size());
      sveStatic.verify(() ->
            SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
                  any(), any()), times(1));
   }

   @Test
   public void testBuildGenericPart2EmptyList() throws BsmPart2ContentBuilderException {

      List<J2735BsmPart2Content> outputList = new ArrayList<>();

      BsmPart2ContentBuilder.buildGenericPart2(null, outputList);

      assertEquals(0, outputList.size());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BsmPart2ContentBuilder> constructor = BsmPart2ContentBuilder.class.getDeclaredConstructor();
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

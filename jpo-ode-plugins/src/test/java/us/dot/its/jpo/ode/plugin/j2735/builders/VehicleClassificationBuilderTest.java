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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;
import us.dot.its.jpo.ode.plugin.j2735.J2735FuelType;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponderGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleType;
import us.dot.its.jpo.ode.util.JsonUtils;

public class VehicleClassificationBuilderTest {

   @Test
   public void testKeyTypeLowerBoundException() {

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("keyType", -1);

      try {
         VehicleClassificationBuilder.genericVehicleClassification(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }

   }

   @Test
   public void testKeyTypeUpperBoundException() {

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("keyType", 256);

      try {
         VehicleClassificationBuilder.genericVehicleClassification(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }

   }

   @Test
   public void testKeyType() {

      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("keyType", 55);

      assertEquals(Integer.valueOf(55),
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getKeyType());
   }

   @Test
   public void testRoleEnum() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.set("role", JsonUtils.newNode().put("roadRescue", true));

      assertEquals(J2735BasicVehicleRole.roadRescue,
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getRole());
   }

   @Test
   public void testIso3883() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("iso3883", 23);

      assertEquals(Integer.valueOf(23),
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getIso3883());
   }

   @Test
   public void testHpmsEnum() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.set("hpmsType", JsonUtils.newNode().put("axleCnt5MultiTrailer", true));

      assertEquals(J2735VehicleType.axleCnt5MultiTrailer,
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getHpmsType());
   }

   @Test
   public void testVehicleType() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.set("vehicleType", JsonUtils.newNode().put("light-vehicles", true));

      assertEquals("light-vehicles",
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getVehicleType().getName());
   }

   @Test
   public void testResponseEquip() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.set("responseEquip", JsonUtils.newNode().put("tanker-or-tender", true));

      assertEquals("tanker-or-tender",
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getResponseEquip().getName());
   }

   @Test
   public void testResponderType() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.set("responderType", JsonUtils.newNode().put("ambulance-units", true));

      assertEquals(J2735ResponderGroupAffected.ambulance_units,
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getResponderType());
   }

   @Test
   public void testFuelTypeLowerBoundException() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("fuelType", -1);

      try {
         VehicleClassificationBuilder.genericVehicleClassification(testInputNode);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testFuelTypeUpperBoundException() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("fuelType", 11);

      try {
         VehicleClassificationBuilder.genericVehicleClassification(testInputNode);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testFuelType() {
      ObjectNode testInputNode = JsonUtils.newNode();
      testInputNode.put("fuelType", 7);

      assertEquals(J2735FuelType.natGasLiquid,
            VehicleClassificationBuilder.genericVehicleClassification(testInputNode).getFuelType());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<VehicleClassificationBuilder> constructor = VehicleClassificationBuilder.class
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

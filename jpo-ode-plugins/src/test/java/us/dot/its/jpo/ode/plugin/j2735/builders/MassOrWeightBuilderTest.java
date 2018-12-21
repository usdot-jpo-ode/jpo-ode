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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class MassOrWeightBuilderTest {

// TrailerMass tests

   /**
    * Test that an undefined trailer mass value of (0) returns (null)
    */
   @Test
   public void shouldReturnUndefinedTrailerMass() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 0);

      Integer expectedValue = null;

      Integer actualValue = MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a minimum trailer mass of (1) returns (500)
    */
   @Test
   public void shouldReturnMinimumTrailerMass() {
      //
      ObjectNode testInput = JsonUtils.newNode().put("mass", 1);

      Integer expectedValue = 500;

      Integer actualValue = MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum trailer mass of (254) returns (127000)
    */
   @Test
   public void shouldReturnMaximumTrailerMass() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 254);

      Integer expectedValue = 127000;

      Integer actualValue = MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a trailer mass of (255) indicates a trailer mass larger than
    * (127000)
    */
   @Test
   public void shouldReturnLargerTrailerMass() {
      ObjectNode testInput = JsonUtils.newNode().put("mass", 255);

      Integer expectedValue = 127000;

      Integer actualValue = MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));

      assertTrue(actualValue > expectedValue);

   }

   /**
    * Test that a known trailer mass of (123) returns (61500)
    */
   @Test
   public void shouldReturnKnownTrailerMass() {
      ObjectNode testInput = JsonUtils.newNode().put("mass", 123);

      Integer expectedValue = 61500;

      Integer actualValue = MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an invalid trailer mass below (0) throws an exception
    */
   @Test
   public void shouldThrowExceptionTrailerMassBelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", -1);

      try {
         MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an invalid trailer mass above (255) throws an exception
    */
   @Test
   public void shouldThrowExceptionTrailerMassAboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 256);

      try {
         MassOrWeightBuilder.genericTrailerMass(testInput.get("mass"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

// TrailerWeight tests

   /**
    * Test that a minimum trailer weight of (0) returns (0)
    */
   @Test
   public void shouldReturnMinimumTrailerWeight() {
      ObjectNode testInput = JsonUtils.newNode().put("weight", 0);

      Integer expectedValue = 0;

      Integer actualValue = MassOrWeightBuilder.genericTrailerWeight(testInput.get("weight"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum trailer weight of (64255) returns (128510)
    */
   @Test
   public void shouldReturnMaximumTrailerWeight() {
      ObjectNode testInput = JsonUtils.newNode().put("weight", 64255);

      Integer expectedValue = 128510;

      Integer actualValue = MassOrWeightBuilder.genericTrailerWeight(testInput.get("weight"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a known trailer weight of (12500) returns (25000)
    */
   @Test
   public void shouldReturnKnownTrailerWeight() {
      ObjectNode testInput = JsonUtils.newNode().put("weight", 12500);

      Integer expectedValue = 25000;

      Integer actualValue = MassOrWeightBuilder.genericTrailerWeight(testInput.get("weight"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an invalid trailer weight below (0) throws an exception
    */
   @Test
   public void shouldThrowExceptionTrailerWeightBelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("weight", -1);

      try {
         MassOrWeightBuilder.genericTrailerWeight(testInput.get("weight"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an invalid trailer weight above (64255) throws an exception
    */
   @Test
   public void shouldThrowExceptionTrailerWeightAboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("weight", 64256);

      try {
         MassOrWeightBuilder.genericTrailerWeight(testInput.get("weight"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

// VehicleMass tests

   /**
    * Test that an input vehicle mass in lowest 50kg step range of (0) returns
    * (0)
    */
   @Test
   public void shouldReturnMinimumVehicleMass50KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 0);

      Integer expectedValue = 0;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass in highest 50kg step range of (80) returns
    * (4000)
    */
   @Test
   public void shouldReturnMaximumVehicleMass50KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 80);

      Integer expectedValue = 4000;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass in lowest 500kg step range of (81) returns
    * (4500)
    */
   @Test
   public void shouldReturnMinimumVehicleMass500KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 81);

      Integer expectedValue = 4500;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass in highest 500kg step range of (200)
    * returns (64000)
    */
   @Test
   public void shouldReturnMaximumVehicleMass500KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 200);

      Integer expectedValue = 64000;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass in lowest 2000kg step range of (201)
    * returns (66000)
    */
   @Test
   public void shouldReturnMinimumVehicleMass2000KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 201);

      Integer expectedValue = 66000;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass in highest 2000kg step range of (253)
    * returns (170000)
    */
   @Test
   public void shouldReturnMaximumVehicleMass2000KGStep() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 253);

      Integer expectedValue = 170000;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input vehicle mass of (254) signifies a vehicle mass greater
    * than (170000)
    */
   @Test
   public void shouldReturnLargerVehicleMass() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 254);

      Integer expectedValue = 170000;

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertTrue(actualValue > expectedValue);

   }

   /**
    * Test that an input vehicle mass of (255) signifies an undefined vehicle
    * mass (null)
    */
   @Test
   public void shouldReturnUndefinedVehicleMass() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 255);

      Integer actualValue = MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));

      assertNull(actualValue);

   }

   /**
    * Test that an input vehicle mass below (0) throws an exception
    */
   @Test
   public void shouldThrowExceptionVehicleMassBelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", -1);

      try {
         MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an input vehicle mass above (255) throws an exception
    */
   @Test
   public void shouldThrowExceptionVehicleMassAboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("mass", 256);

      try {
         MassOrWeightBuilder.genericVehicleMass(testInput.get("mass"));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<MassOrWeightBuilder> constructor = MassOrWeightBuilder.class.getDeclaredConstructor();
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

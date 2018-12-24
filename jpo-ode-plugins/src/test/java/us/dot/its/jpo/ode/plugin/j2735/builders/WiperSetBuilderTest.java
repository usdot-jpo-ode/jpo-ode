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

import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;
import us.dot.its.jpo.ode.util.JsonUtils;

public class WiperSetBuilderTest {

   @Test
   public void testRequiredConversions() {

      J2735WiperStatus expectedWiperStatusFront = J2735WiperStatus.OFF;
      Integer expectedWiperRateFront = 55;

      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 1);
      testInput.put("rateFront", 55);

      J2735WiperSet actualValue = WiperSetBuilder.genericWiperSet(testInput);

      assertEquals(expectedWiperStatusFront, actualValue.getStatusFront());
      assertEquals(expectedWiperRateFront, actualValue.getRateFront());
   }

   @Test
   public void testOptionalConversions() {

      J2735WiperStatus expectedWiperStatusRear = J2735WiperStatus.AUTOMATICPRESENT;
      Integer expectedWiperRateRear = 12;

      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 1);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);

      J2735WiperSet actualValue = WiperSetBuilder.genericWiperSet(testInput);

      assertEquals(expectedWiperStatusRear, actualValue.getStatusRear());
      assertEquals(expectedWiperRateRear, actualValue.getRateRear());
   }

   @Test
   public void testBelowLowerBoundStatusFront() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", -1);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testAboveUpperBoundStatusFront() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 7);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testBelowLowerBoundRateFront() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", -1);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testAboveUpperBoundRateFront() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", 128);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testBelowLowerBoundStatusRear() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", -1);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testAboveUpperBoundStatusRear() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 7);
      testInput.put("rateRear", 12);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testBelowLowerBoundRateRear() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", -1);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testAboveUpperBoundRateRear() {
      ObjectNode testInput = JsonUtils.newNode();

      testInput.put("statusFront", 5);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 128);

      try {
         WiperSetBuilder.genericWiperSet(testInput);
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<WiperSetBuilder> constructor = WiperSetBuilder.class.getDeclaredConstructor();
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

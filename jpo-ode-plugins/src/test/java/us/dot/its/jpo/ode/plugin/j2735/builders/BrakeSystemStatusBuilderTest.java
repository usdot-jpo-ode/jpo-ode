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

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BrakeSystemStatusBuilderTest {

   @Test
   public void testWheelBrakesLeftFront() {

      String expectedTraction = "engaged";
      String expectedAbs = "on";
      String expectedScs = "off";
      String expectedBrakeBoost = "unavailable";
      String expectedAuxBrakes = "off";

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("wheelBrakes", "01000");
      testInput.set("traction", JsonUtils.newNode().put(expectedTraction, true));
      testInput.set("abs", JsonUtils.newNode().put(expectedAbs, true));
      testInput.set("scs", JsonUtils.newNode().put(expectedScs, true));
      testInput.set("brakeBoost", JsonUtils.newNode().put(expectedBrakeBoost, true));
      testInput.set("auxBrakes", JsonUtils.newNode().put(expectedAuxBrakes, true));

      J2735BrakeSystemStatus actualValue = BrakeSystemStatusBuilder.genericBrakeSystemStatus(testInput);

      assertFalse(actualValue.getWheelBrakes().get("unavailable"));
      assertTrue(actualValue.getWheelBrakes().get("leftFront"));
      assertFalse(actualValue.getWheelBrakes().get("leftRear"));
      assertFalse(actualValue.getWheelBrakes().get("rightFront"));
      assertFalse(actualValue.getWheelBrakes().get("rightRear"));

      assertEquals(expectedTraction, actualValue.getTraction());
      assertEquals(expectedAbs, actualValue.getAbs());
      assertEquals(expectedScs, actualValue.getScs());
      assertEquals(expectedBrakeBoost, actualValue.getBrakeBoost());
      assertEquals(expectedAuxBrakes, actualValue.getAuxBrakes());
   }

   @Test
   public void testWheelBrakesUnavailable() {
      String expectedTraction = "engaged";
      String expectedAbs = "on";
      String expectedScs = "off";
      String expectedBrakeBoost = "unavailable";
      String expectedAuxBrakes = "off";

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("wheelBrakes", "00000");
      testInput.set("traction", JsonUtils.newNode().put(expectedTraction, true));
      testInput.set("abs", JsonUtils.newNode().put(expectedAbs, true));
      testInput.set("scs", JsonUtils.newNode().put(expectedScs, true));
      testInput.set("brakeBoost", JsonUtils.newNode().put(expectedBrakeBoost, true));
      testInput.set("auxBrakes", JsonUtils.newNode().put(expectedAuxBrakes, true));

      J2735BrakeSystemStatus actualValue = BrakeSystemStatusBuilder.genericBrakeSystemStatus(testInput);

      assertTrue(actualValue.getWheelBrakes().get("unavailable"));
      assertFalse(actualValue.getWheelBrakes().get("leftFront"));
      assertFalse(actualValue.getWheelBrakes().get("leftRear"));
      assertFalse(actualValue.getWheelBrakes().get("rightFront"));
      assertFalse(actualValue.getWheelBrakes().get("rightRear"));
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BrakeSystemStatusBuilder> constructor = BrakeSystemStatusBuilder.class.getDeclaredConstructor();
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

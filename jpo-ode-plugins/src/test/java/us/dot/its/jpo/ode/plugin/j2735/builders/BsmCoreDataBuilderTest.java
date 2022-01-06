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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmCoreDataBuilderTest {

   @Capturing
   AccelerationSet4WayBuilder capturingAccelerationSet4WayBuilder;
   @Capturing
   PositionalAccuracyBuilder capturingPositionalAccuracyBuilder;
   @Capturing
   SpeedOrVelocityBuilder capturingSpeedOrVelocityBuilder;
   @Capturing
   BrakeSystemStatusBuilder capturingBrakeSystemStatusBuilder;
   @Capturing
   VehicleSizeBuilder capturingVehicleSizeBuilder;

   @Test
   public void testRequiredElements() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 4567);

      testInput.put("lat", 40741895);
      testInput.put("long", -73989308);
      testInput.put("elev", 3456);

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testFlagValues() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 65535);

      testInput.put("lat", 50741895);
      testInput.put("long", -63989308);
      testInput.put("elev", 3456);

      testInput.put("angle", 0x7F);

      ObjectNode testTransmission = JsonUtils.newNode();
      testTransmission.set("unavailable", null);
      testInput.set("transmission", testTransmission);

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testOptionalElements() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 65535);
      testInput.put("heading", 21000);
      testInput.put("angle", 55);

      testInput.put("lat", 50741895);
      testInput.put("long", -63989308);
      testInput.put("elev", 3456);

      ObjectNode testTransmission = JsonUtils.newNode();
      testTransmission.set("unavailable", null);
      testInput.set("transmission", testTransmission);

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testUnsupportedTransmission() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 65535);
      testInput.put("heading", 21000);
      testInput.put("angle", 55);

      testInput.put("lat", 50741895);
      testInput.put("long", -63989308);
      testInput.put("elev", 3456);

      ObjectNode testTransmission = JsonUtils.newNode();
      testTransmission.set("testValue", null);
      testInput.set("transmission", testTransmission);

      assertEquals(BsmCoreDataBuilder.genericBsmCoreData(testInput).getTransmission(), J2735TransmissionState.UNAVAILABLE);
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BsmCoreDataBuilder> constructor = BsmCoreDataBuilder.class.getDeclaredConstructor();
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

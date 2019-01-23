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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.util.JsonUtils;

public class WeatherProbeBuilderTest {

   // AmbientAirTemperature tests

   /**
    * Test that the ambient air temperature undefined flag value (191) returns
    * (null)
    */
   @Test
   public void shouldReturnUnknownAmbientAirTemperature() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 191);

      Integer expectedValue = null;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirTemp();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum ambient air temperature value (0) returns (-40)
    */
   @Test
   public void shouldReturnMinimumAmbientAirTemperature() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 0);

      Integer expectedValue = -40;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirTemp();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case minimum ambient air temperature value (1) returns
    * (-39)
    */
   @Test
   public void shouldReturnCornerCaseMinimumAmbientAirTemperature() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 1);

      Integer expectedValue = -39;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirTemp();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case maximum ambient air temperature value (189)
    * returns (149)
    */
   @Test
   public void shouldReturnCornerCaseMaximumAmbientAirTemperature() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 189);

      Integer expectedValue = 149;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirTemp();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum ambient air temperature value (190) returns (150)
    */
   @Test
   public void shouldReturnMaximumAmbientAirTemperature() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 190);

      Integer expectedValue = 150;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirTemp();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an ambient air temperature value (-1) below the lower bound (0)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionAmbientAirTemperatureBelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", -1);

      try {
         WeatherProbeBuilder.genericWeatherProbe(testInput);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an ambient air temperature value (192) above the upper bound
    * (191) throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionAmbientAirTemperatureAboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("airTemp", 192);

      try {
         WeatherProbeBuilder.genericWeatherProbe(testInput);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // AmbientAirPressure tests

   /**
    * Test that the ambient air pressure undefined flag value (0) returns (null)
    */
   @Test
   public void shouldReturnUndefinedAmbientAirPressure() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 0);

      Integer expectedValue = null;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirPressure();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum ambient air pressure value (1) returns (580)
    */
   @Test
   public void shouldReturnMinimumAmbientAirPressure() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 1);

      Integer expectedValue = 580;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirPressure();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the corner case minimum ambient air pressure value (2) returns
    * (582)
    */
   @Test
   public void shouldReturnCornerCaseMinimumAmbientAirPressure() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 2);

      Integer expectedValue = 582;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirPressure();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the corner case maximum ambient air pressure value (254) returns
    * (1086)
    */
   @Test
   public void shouldReturnCornerCaseMaximumAmbientAirPressure() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 254);

      Integer expectedValue = 1086;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirPressure();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum ambient air pressure value (255) returns (1088)
    */
   @Test
   public void shouldReturnMaximumAmbientAirPressure() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 255);

      Integer expectedValue = 1088;

      Integer actualValue = WeatherProbeBuilder.genericWeatherProbe(testInput).getAirPressure();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an ambient air pressure value (-1) below the lower bound (0)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionAmbientAirPressureBelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", -1);

      try {
         WeatherProbeBuilder.genericWeatherProbe(testInput);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an ambient air pressure value (256) above the upper bound (255)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionAmbientAirPresureAboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("airPressure", 256);

      try {
         WeatherProbeBuilder.genericWeatherProbe(testInput);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   @Test
   public void testRainRates(@Capturing WiperSetBuilder capturingWiperSetBuilder) {

      new Expectations() {
         {
            WiperSetBuilder.genericWiperSet((JsonNode) any);
            result = new J2735WiperSet();
         }
      };

      ObjectNode testInput = JsonUtils.newNode();
      JsonUtils.addNode(testInput, "rainRates", JsonUtils.newNode());

      assertNotNull(WeatherProbeBuilder.genericWeatherProbe(testInput).getRainRates());

   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<WeatherProbeBuilder> constructor = WeatherProbeBuilder.class.getDeclaredConstructor();
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

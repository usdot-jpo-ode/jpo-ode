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
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipSituation;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipYesNo;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherReport;
import us.dot.its.jpo.ode.util.JsonUtils;

public class WeatherReportBuilderTest {

   @Test
   public void testRequiredField() {
      // isRaining is the only required field

      J2735EssPrecipYesNo expectedIsRaining = J2735EssPrecipYesNo.PRECIP;

      ObjectNode testInputNode = JsonUtils.newNode();
      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("precip", null);
      testInputNode.set("isRaining", testIsRaining);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedIsRaining, actualValue.getIsRaining());
   }

   @Test
   public void testUnsupportedIsRaining() {
      // isRaining is the only required field

      J2735EssPrecipYesNo expectedIsRaining = J2735EssPrecipYesNo.NA;

      ObjectNode testInputNode = JsonUtils.newNode();
      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("testIsRaining", null);
      testInputNode.set("isRaining", testIsRaining);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedIsRaining, actualValue.getIsRaining());
   }

   @Test
   public void testRainRateErrorBelowLowerBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("rainRate", -1);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testRainRateErrorAboveUpperBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("rainRate", 65536);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testRainRateFlagValue() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("rainRate", 65535);
      assertNull(WeatherReportBuilder.genericWeatherReport(testInputNode).getRainRate());
   }

   @Test
   public void testRainRateConversion() {
      BigDecimal expectedRainRate = BigDecimal.valueOf(4567.8);

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("rainRate", 45678);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedRainRate, actualValue.getRainRate());
   }

   @Test
   public void testPrecipSituationEnum() {
      J2735EssPrecipSituation expectedPrecipSituation = J2735EssPrecipSituation.RAINHEAVY;

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);

      ObjectNode testPrecipSituation = JsonUtils.newNode();
      testPrecipSituation.set("rainHeavy", null);
      testInputNode.set("precipSituation", testPrecipSituation);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedPrecipSituation, actualValue.getPrecipSituation());
   }

   @Test
   public void testUnsupportedPrecipSituationEnum() {
      J2735EssPrecipSituation expectedPrecipSituation = J2735EssPrecipSituation.UNKNOWN;

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);

      ObjectNode testPrecipSituation = JsonUtils.newNode();
      testPrecipSituation.set("testSituation", null);
      testInputNode.set("precipSituation", testPrecipSituation);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedPrecipSituation, actualValue.getPrecipSituation());
   }

   @Test
   public void testSolarRadiationBelowLowerBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("solarRadiation", -1);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testSolarRadiationAboveUpperBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("solarRadiation", 65536);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testSolarRadiationFlagValue() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("solarRadiation", 65535);
      assertNull(WeatherReportBuilder.genericWeatherReport(testInputNode).getSolarRadiation());
   }

   @Test
   public void testSolarRadiationConversion() {
      Integer expectedSolarRadiation = 34567;

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("solarRadiation", 34567);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedSolarRadiation, actualValue.getSolarRadiation());
   }

   @Test
   public void testMobileFrictionBelowLowerBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("friction", -1);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testMobileFrictionAboveUpperBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("friction", 102);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testMobileFrictionFlagValue() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("friction", 101);
      assertNull(WeatherReportBuilder.genericWeatherReport(testInputNode).getFriction());
   }

   @Test
   public void testMobileFrictionConversion() {
      Integer expectedMobileFriction = 55;

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("friction", 55);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedMobileFriction, actualValue.getFriction());
   }

   @Test
   public void testRoadFrictionBelowLowerBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("roadFriction", -1);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testRoadFrictionAboveUpperBound() {
      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("roadFriction", 51);
      try {
         WeatherReportBuilder.genericWeatherReport(testInputNode);
         fail("Expected exception.");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testRoadFrictionConversion() {
      Integer inputRoadFriction = 37;

      BigDecimal expectedRoadFriction = BigDecimal.valueOf(0.74);

      ObjectNode testInputNode = JsonUtils.newNode();

      ObjectNode testIsRaining = JsonUtils.newNode();
      testIsRaining.set("noprecip", null);
      testInputNode.set("isRaining", testIsRaining);
      testInputNode.put("roadFriction", inputRoadFriction);

      J2735WeatherReport actualValue = WeatherReportBuilder.genericWeatherReport(testInputNode);

      assertEquals(expectedRoadFriction, actualValue.getRoadFriction());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<WeatherReportBuilder> constructor = WeatherReportBuilder.class.getDeclaredConstructor();
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

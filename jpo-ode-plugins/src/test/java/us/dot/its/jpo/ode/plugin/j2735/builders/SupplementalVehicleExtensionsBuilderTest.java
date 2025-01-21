/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import mockit.Capturing;
import mockit.Expectations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;
import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedProfile;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherProbe;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherReport;
import us.dot.its.jpo.ode.util.JsonUtils;

class SupplementalVehicleExtensionsBuilderTest {

  @Test
  void testClassification() {

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("classification", 1);

    J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(outputContent,
            testInput);

    Assertions.assertEquals(Integer.valueOf(1), result.getClassification());
  }

  @Test
  void testVehicleClass(
      @Capturing VehicleClassificationBuilder capturingVehicleClassificationBuilder) {

    new Expectations() {
      {
        VehicleClassificationBuilder.genericVehicleClassification((JsonNode) any);
        result = new J2735VehicleClassification();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("classDetails", "something");

    J2735BsmPart2Content outputContent = new J2735BsmPart2Content();

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(outputContent,
            testInput);

    Assertions.assertNotNull(result.getClassDetails());
  }

  @Test
  void testVehicleData(@Capturing VehicleDataBuilder capturingVehicleDataBuilder) {

    new Expectations() {
      {
        VehicleDataBuilder.genericVehicleData((JsonNode) any);
        result = new J2735VehicleData();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("vehicleData", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getVehicleData());
  }

  @Test
  void testWeatherReport(@Capturing WeatherReportBuilder capturingWeatherReportBuilder) {

    new Expectations() {
      {
        WeatherReportBuilder.genericWeatherReport((JsonNode) any);
        result = new J2735WeatherReport();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse1", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getDoNotUse1());
  }

  @Test
  void testWeatherProbe(@Capturing WeatherProbeBuilder capturingWeatherProbeBuilder) {

    new Expectations() {
      {
        WeatherProbeBuilder.genericWeatherProbe((JsonNode) any);
        result = new J2735WeatherProbe();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse2", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getDoNotUse2());
  }

  @Test
  void testObstacle(@Capturing ObstacleDetectionBuilder capturingObstacleDetectionBuilder) {

    new Expectations() {
      {
        ObstacleDetectionBuilder.genericObstacleDetection((JsonNode) any);
        result = new J2735ObstacleDetection();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse3", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getDoNotUse3());
  }

  @Test
  void testStatus(@Capturing DisabledVehicleBuilder capturingDisabledVehicleBuilder) {

    new Expectations() {
      {
        DisabledVehicleBuilder.genericDisabledVehicle((JsonNode) any);
        result = new J2735DisabledVehicle();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("status", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getStatus());
  }

  @Test
  void testSpeedProfile(@Capturing SpeedProfileBuilder capturingSpeedProfileBuilder) {

    new Expectations() {
      {
        SpeedProfileBuilder.genericSpeedProfile((JsonNode) any);
        result = new J2735SpeedProfile();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse4", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getDoNotUse4());
  }

  @Test
  void testRtcmPackage(@Capturing RTCMPackageBuilder capturingRTCMPackageBuilder) {

    new Expectations() {
      {
        RTCMPackageBuilder.genericRTCMPackage((JsonNode) any);
        result = new J2735RTCMPackage();
      }
    };

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse5", "something");

    J2735SupplementalVehicleExtensions result =
        SupplementalVehicleExtensionsBuilder.evaluateSupplementalVehicleExtensions(
            new J2735BsmPart2Content(), testInput);

    Assertions.assertNotNull(result.getDoNotUse5());
  }

  @Test
  void testConstructorIsPrivate()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    Constructor<SupplementalVehicleExtensionsBuilder> constructor =
        SupplementalVehicleExtensionsBuilder.class.getDeclaredConstructor();
    Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      Assertions.fail("Expected IllegalAccessException.class");
    } catch (Exception e) {
      Assertions.assertEquals(InvocationTargetException.class, e.getClass());
    }
  }

}

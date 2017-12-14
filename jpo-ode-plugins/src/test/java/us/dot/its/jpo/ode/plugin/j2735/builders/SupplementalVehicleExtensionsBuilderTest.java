package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
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
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@Ignore
public class SupplementalVehicleExtensionsBuilderTest {

   @Test
   public void testClassification() {

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("classification", 1);

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(testInput);

      assertEquals(Integer.valueOf(1),
            ((J2735SupplementalVehicleExtensions) outputObject.getValue()).getClassification());
   }

   @Test
   public void testVehicleClass(@Capturing VehicleClassificationBuilder capturingVehicleClassificationBuilder) {

      new Expectations() {
         {
            VehicleClassificationBuilder.genericVehicleClassification((JsonNode) any);
            result = new J2735VehicleClassification();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("classDetails", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getClassDetails());
   }
   
   @Test
   public void testVehicleData(@Capturing VehicleDataBuilder capturingVehicleDataBuilder) {

      new Expectations() {
         {
            VehicleDataBuilder.genericVehicleData((JsonNode) any);
            result = new J2735VehicleData();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("vehicleData", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getVehicleData());
   }
   
   @Test
   public void testWeatherReport(@Capturing WeatherReportBuilder capturingWeatherReportBuilder) {

      new Expectations() {
         {
            WeatherReportBuilder.genericWeatherReport((JsonNode) any);
            result = new J2735WeatherReport();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("weatherReport", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getWeatherReport());
   }
   
   @Test
   public void testWeatherProbe(@Capturing WeatherProbeBuilder capturingWeatherProbeBuilder) {

      new Expectations() {
         {
            WeatherProbeBuilder.genericWeatherProbe((JsonNode) any);
            result = new J2735WeatherProbe();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("weatherProbe", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getWeatherProbe());
   }
   
   @Test
   public void testObstacle(@Capturing ObstacleDetectionBuilder capturingObstacleDetectionBuilder) {

      new Expectations() {
         {
            ObstacleDetectionBuilder.genericObstacleDetection((JsonNode) any);
            result = new J2735ObstacleDetection();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("obstacle", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getObstacle());
   }
   
   @Test
   public void testStatus(@Capturing DisabledVehicleBuilder capturingDisabledVehicleBuilder) {

      new Expectations() {
         {
            DisabledVehicleBuilder.genericDisabledVehicle((JsonNode) any);
            result = new J2735DisabledVehicle();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("status", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getStatus());
   }
   
   @Test
   public void testSpeedProfile(@Capturing SpeedProfileBuilder capturingSpeedProfileBuilder) {

      new Expectations() {
         {
            SpeedProfileBuilder.genericSpeedProfile((JsonNode) any);
            result = new J2735SpeedProfile();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("speedProfile", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getSpeedProfile());
   }
   
   @Test
   public void testRtcmPackage(@Capturing RTCMPackageBuilder capturingRTCMPackageBuilder) {

      new Expectations() {
         {
            RTCMPackageBuilder.genericRTCMPackage((JsonNode) any);
            result = new J2735RTCMPackage();
         }
      };

      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("theRTCM", "something");

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getTheRTCM());
   }
   
   @Test
   public void testEmptyRegional() {


      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      ObjectNode testInput = JsonUtils.newNode();
      testInput.set("regional", JsonUtils.newNode());

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getRegional());
   }
   
   @Test
   public void test1Regional(@Capturing CodecUtils capturingCodecUtils) {


      J2735BsmPart2Content outputObject = new J2735BsmPart2Content();
      
      ObjectNode testRegionalNode = JsonUtils.newNode();
      testRegionalNode.put("regionId", 1);
      testRegionalNode.put("regExtValue", "something");
      
      ObjectNode testInput = JsonUtils.newNode();
      testInput.set("regional", JsonUtils.newArrayNode().add(testRegionalNode));

      SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions( testInput);

      assertNotNull(((J2735SupplementalVehicleExtensions) outputObject.getValue()).getRegional());
   }


}

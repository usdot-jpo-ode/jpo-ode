package us.dot.its.jpo.ode.plugin.j2735;

import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.SpecialVehicleExtensions;
import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.SupplementalVehicleExtensions;
import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.VehicleSafetyExtensions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.util.JsonUtils;

class J2735BsmPart2ContentTest {

  @Test
  void shouldDeserializeBsmPart2Content_VehicleSafetyExtensions() throws IOException {

    final String bsmJson = loadResourceAsString(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/BSM_PartIIContent_VehicleSafetyExtensions.json");

    var deserialized =
        (J2735BsmPart2Content) JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

    Assertions.assertNotNull(deserialized);
    Assertions.assertEquals(VehicleSafetyExtensions, deserialized.getId());
    Assertions.assertInstanceOf(J2735VehicleSafetyExtensions.class, deserialized.getValue());
    var extensions = (J2735VehicleSafetyExtensions) deserialized.getValue();
    Assertions.assertNotNull(extensions.getPathHistory());
    Assertions.assertNotNull(extensions.getPathPrediction());
  }

  @Test
  void shouldDeserializeBsmPart2Content_SupplementalVehicleExtensions() throws IOException {
    final String bsmJson = loadResourceAsString(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/BSM_PartIIContent_SupplementalVehicleExtensions.json");

    var deserialized =
        (J2735BsmPart2Content) JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

    Assertions.assertNotNull(deserialized);
    Assertions.assertEquals(SupplementalVehicleExtensions, deserialized.getId());
    Assertions.assertInstanceOf(J2735SupplementalVehicleExtensions.class, deserialized.getValue());
  }

  @Test
  void shouldDeserializeBsmPart2Content_SpecialVehicleExtensions() throws IOException {

    final String bsmJson = loadResourceAsString(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/BSM_PartIIContent_SpecialVehicleExtensions.json");

    var deserialized =
        (J2735BsmPart2Content) JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

    Assertions.assertNotNull(deserialized);
    Assertions.assertEquals(SpecialVehicleExtensions, deserialized.getId());
    Assertions.assertInstanceOf(J2735SpecialVehicleExtensions.class, deserialized.getValue());
  }

  static String loadResourceAsString(String resourcePath) throws IOException {
    File file = new File(resourcePath);
    byte[] data = Files.readAllBytes(file.toPath());
    return new String(data);
  }
}

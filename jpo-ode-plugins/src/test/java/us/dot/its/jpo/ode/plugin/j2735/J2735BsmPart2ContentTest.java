package us.dot.its.jpo.ode.plugin.j2735;

import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.SpecialVehicleExtensions;
import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.SupplementalVehicleExtensions;
import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.VehicleSafetyExtensions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.util.JsonUtils;

class J2735BsmPart2ContentTest {

  @Test
  void shouldDeserializeBsmPart2Content_VehicleSafetyExtensions() {

    final String bsmJson =
        "{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.0,\"latOffset\":-0.0000491,\"lonOffset\":-0.0000043,\"timeOffset\":0.39},{\"elevationOffset\":-1.1,\"latOffset\":-0.0007303,\"lonOffset\":-0.0001015,\"timeOffset\":6.79},{\"elevationOffset\":-1.1,\"latOffset\":-0.0012664,\"lonOffset\":-0.0002581,\"timeOffset\":10.9},{\"elevationOffset\":-0.7,\"latOffset\":-0.0018413,\"lonOffset\":-0.0005267,\"timeOffset\":14.39}]},\"pathPrediction\":{\"confidence\":70.0,\"radiusOfCurve\":-3139.0}}}";

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
  void shouldDeserializeBsmPart2Content_SupplementalVehicleExtensions() {
    final String bsmJson =
        "{\"id\":\"SupplementalVehicleExtensions\",\"value\":{\"classification\":null,\"classDetails\":null,\"vehicleData\":null,\"doNotUse1\":null,\"doNotUse2\":null,\"doNotUse3\":null,\"status\":null,\"doNotUse4\":null,\"doNotUse5\":null}}";

    var deserialized =
        (J2735BsmPart2Content) JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

    Assertions.assertNotNull(deserialized);
    Assertions.assertEquals(SupplementalVehicleExtensions, deserialized.getId());
    Assertions.assertInstanceOf(J2735SupplementalVehicleExtensions.class, deserialized.getValue());
  }

  @Test
  void shouldDeserializeBsmPart2Content_SpecialVehicleExtensions() {
    final String bsmJson =
        "{\"id\":\"SpecialVehicleExtensions\",\"value\":{\"vehicleAlerts\":null,\"description\":null,\"doNotUse\":null}}";

    var deserialized =
        (J2735BsmPart2Content) JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

    Assertions.assertNotNull(deserialized);
    Assertions.assertEquals(SpecialVehicleExtensions, deserialized.getId());
    Assertions.assertInstanceOf(J2735SpecialVehicleExtensions.class, deserialized.getValue());
  }
}

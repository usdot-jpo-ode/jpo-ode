package us.dot.its.jpo.ode.plugin.j2735;

import org.junit.Test;

import static org.junit.Assert.*;


import static us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id.*;

import us.dot.its.jpo.ode.util.JsonUtils;

public class J2735BsmPart2ContentTest {


    
    @Test
    public void shouldDeserializeBsmPart2Content_VehicleSafetyExtensions() {

        final String bsmJson = "{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.0,\"latOffset\":-0.0000491,\"lonOffset\":-0.0000043,\"timeOffset\":0.39},{\"elevationOffset\":-1.1,\"latOffset\":-0.0007303,\"lonOffset\":-0.0001015,\"timeOffset\":6.79},{\"elevationOffset\":-1.1,\"latOffset\":-0.0012664,\"lonOffset\":-0.0002581,\"timeOffset\":10.9},{\"elevationOffset\":-0.7,\"latOffset\":-0.0018413,\"lonOffset\":-0.0005267,\"timeOffset\":14.39}]},\"pathPrediction\":{\"confidence\":70.0,\"radiusOfCurve\":-3139.0}}}";

        var deserialized = (J2735BsmPart2Content)JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

        assertNotNull(deserialized);
        assertEquals(deserialized.getId(), VehicleSafetyExtensions);
        assertTrue(deserialized.getValue() instanceof J2735VehicleSafetyExtensions);
        var extensions = (J2735VehicleSafetyExtensions)deserialized.getValue();
        assertNotNull(extensions.getPathHistory());
        assertNotNull(extensions.getPathPrediction());
    }

    @Test
    public void shouldDeserializeBsmPart2Content_SupplementalVehicleExtensions() {
        final String bsmJson = "{\"id\":\"SupplementalVehicleExtensions\",\"value\":{\"classification\":null,\"classDetails\":null,\"vehicleData\":null,\"weatherReport\":null,\"weatherProbe\":null,\"obstacle\":null,\"status\":null,\"speedProfile\":null,\"theRTCM\":null}}";

        var deserialized = (J2735BsmPart2Content)JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

        assertNotNull(deserialized);
        assertEquals(deserialized.getId(), SupplementalVehicleExtensions);
        assertTrue(deserialized.getValue() instanceof J2735SupplementalVehicleExtensions);
    }

    @Test
    public void shouldDeserializeBsmPart2Content_SpecialVehicleExtensions() {
        final String bsmJson = "{\"id\":\"SpecialVehicleExtensions\",\"value\":{\"vehicleAlerts\":null,\"description\":null,\"trailers\":null}}";

        var deserialized = (J2735BsmPart2Content)JsonUtils.fromJson(bsmJson, J2735BsmPart2Content.class);

        assertNotNull(deserialized);
        assertEquals(deserialized.getId(), SpecialVehicleExtensions);
        assertTrue(deserialized.getValue() instanceof J2735SpecialVehicleExtensions);
    }
}

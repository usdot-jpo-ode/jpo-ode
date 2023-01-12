package us.dot.its.jpo.ode.model;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.util.Set;

import static org.junit.Assert.*;



import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;
import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeBsmDataTest {

    final String bsmTxJson = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"\",\"longitude\":\"\",\"elevation\":\"\",\"speed\":\"\",\"heading\":\"\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"504becf3-8e20-49cb-a2d7-25b646c34d0f\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-06-17T19:14:21.223956Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"10.11.81.12\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":46,\"id\":\"E6A99808\",\"secMark\":21061,\"position\":{\"latitude\":39.5881304,\"longitude\":-105.0910403,\"elevation\":1692.0},\"accelSet\":{\"accelLong\":-0.07,\"accelYaw\":-0.09},\"accuracy\":{\"semiMajor\":2.0,\"semiMinor\":2.0,\"orientation\":44.49530799},\"transmission\":\"UNAVAILABLE\",\"speed\":22.62,\"heading\":169.3,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"off\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":180,\"length\":480}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.8,\"latOffset\":-0.0001802,\"lonOffset\":0.0000434,\"timeOffset\":0.89},{\"elevationOffset\":4.5,\"latOffset\":-0.0011801,\"lonOffset\":0.0002357,\"timeOffset\":5.7},{\"elevationOffset\":9.3,\"latOffset\":-0.0023623,\"lonOffset\":0.0003881,\"timeOffset\":11.1}]},\"pathPrediction\":{\"confidence\":70.0,\"radiusOfCurve\":0.0}}}]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    final String bsmLogJson = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"bsmLogDuringEvent.gz\",\"recordType\":\"bsmLogDuringEvent\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"40.565771\",\"longitude\":\"-105.0318108\",\"elevation\":\"1487\",\"speed\":\"0.14\",\"heading\":\"205.975\"},\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"801780cb-d91d-444b-8f4d-9c44fe27f5ea\",\"bundleSize\":222,\"bundleId\":71,\"recordId\":221,\"serialNumber\":14725},\"odeReceivedAt\":\"2019-04-09T18:07:12.352Z\",\"schemaVersion\":6,\"recordGeneratedAt\":\"2018-05-01T16:04:23.694Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\",\"data\":{\"coreData\":{\"msgCnt\":95,\"id\":\"31325431\",\"secMark\":23794,\"position\":{\"latitude\":40.5657318,\"longitude\":-105.0318485,\"elevation\":1472.8},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.52,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":12.70,\"semiMinor\":12.40},\"transmission\":\"NEUTRAL\",\"speed\":0.10,\"heading\":250.9125,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.3,\"latOffset\":-0.0000044,\"lonOffset\":-0.0000106,\"timeOffset\":0.59},{\"elevationOffset\":1.5,\"latOffset\":0.0000141,\"lonOffset\":0.0000047,\"timeOffset\":6.99},{\"elevationOffset\":2.8,\"latOffset\":0.0000385,\"lonOffset\":0.0000206,\"timeOffset\":15.09},{\"elevationOffset\":4.2,\"latOffset\":0.0000394,\"lonOffset\":0.0000051,\"timeOffset\":23.19},{\"elevationOffset\":8.6,\"latOffset\":0.0000586,\"lonOffset\":0.0000595,\"timeOffset\":37.89},{\"elevationOffset\":10.2,\"latOffset\":0.0000866,\"lonOffset\":0.0001174,\"timeOffset\":43.80},{\"elevationOffset\":8.5,\"latOffset\":0.0001026,\"lonOffset\":0.0001127,\"timeOffset\":49.20},{\"elevationOffset\":-0.1,\"latOffset\":0.0001183,\"lonOffset\":0.0000434,\"timeOffset\":55.60},{\"elevationOffset\":-8.1,\"latOffset\":0.0001101,\"lonOffset\":-0.0000274,\"timeOffset\":59.09},{\"elevationOffset\":-14.2,\"latOffset\":0.0001019,\"lonOffset\":-0.0000492,\"timeOffset\":61.19},{\"elevationOffset\":-19.0,\"latOffset\":0.0000944,\"lonOffset\":-0.0000738,\"timeOffset\":63.49},{\"elevationOffset\":-31.4,\"latOffset\":0.0000826,\"lonOffset\":-0.0001389,\"timeOffset\":69.19},{\"elevationOffset\":-39.8,\"latOffset\":0.0000788,\"lonOffset\":-0.0001748,\"timeOffset\":73.09},{\"elevationOffset\":-46.7,\"latOffset\":0.0000753,\"lonOffset\":-0.0002035,\"timeOffset\":78.89},{\"elevationOffset\":-48.9,\"latOffset\":0.0000831,\"lonOffset\":-0.0002563,\"timeOffset\":82.09}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":0.0}}},{\"id\":\"SupplementalVehicleExtensions\",\"value\":{}}]}}}";

    
    @Test
    public void shouldDeserializeJson_bsmTx() {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmTxJson, OdeBsmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeBsmMetadata);     
        assertTrue(deserialized.getPayload() instanceof OdeBsmPayload);
        var payload = (OdeBsmPayload)deserialized.getPayload();
        assertTrue(payload.getData() instanceof J2735Bsm);
        var data = (J2735Bsm)payload.getData();
        assertNotNull(data.getPartII());
        assertTrue(data.getPartII().size() == 1);
        assertTrue(data.getPartII().get(0).getValue() instanceof J2735VehicleSafetyExtensions);
    }

    @Test
    public void shouldDeserializeJson_bsmLogDuringEvent() {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmLogJson, OdeBsmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeBsmMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeBsmPayload);
        var payload = (OdeBsmPayload)deserialized.getPayload();
        assertTrue(payload.getData() instanceof J2735Bsm);
        var data = (J2735Bsm)payload.getData();
        assertNotNull(data.getPartII());
        assertTrue(data.getPartII().size() == 2);
        assertTrue(data.getPartII().get(0).getValue() instanceof J2735VehicleSafetyExtensions);
        assertTrue(data.getPartII().get(1).getValue() instanceof J2735SupplementalVehicleExtensions);
    }

    

    @Test
    public void serializationShouldNotAddClassProperty_bsmTx() {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmTxJson, OdeBsmData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void serializationShouldNotAddClassProperty_bsmLogDuringEvent() {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmLogJson, OdeBsmData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson_bsmTx() throws Exception {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmTxJson, OdeBsmData.class);
        final String serialized = deserialized.toJson(false);
        validateJson(serialized);
    }

    @Test
    public void shouldValidateJson_bsmLogDuringEvent() throws Exception {
        final var deserialized = (OdeBsmData)JsonUtils.fromJson(bsmLogJson, OdeBsmData.class);
        final String serialized = deserialized.toJson(false);
        validateJson(serialized);
    }

    private void validateJson(final String serialized) throws Exception {
        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-bsm.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}

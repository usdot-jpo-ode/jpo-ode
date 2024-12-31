package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeSrmDataTest {
    private static final String SCHEMA_VERSION = "8";
    private static final String ASN1_STRING = "001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0";
    
    final String json = String.format("{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"srmTx\",\"securityResultCode\":null,\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSrmPayload\",\"serialId\":{\"streamId\":\"c3ff825f-ed1f-4411-a12e-1ba889f56483\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-12-13T18:58:53.541816Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.21.0.1\",\"srmSource\":\"RSU\",\"asn1\":\"%s\"},\"payload\":{\"data\":{\"timeStamp\":null,\"second\":0,\"sequenceNumber\":1,\"requests\":{\"signalRequestPackage\":[{\"request\":{\"id\":{\"region\":null,\"id\":12109},\"requestID\":4,\"requestType\":\"priorityRequest\",\"inBoundLane\":{\"lane\":13,\"approach\":null,\"connection\":null},\"outBoundLane\":{\"lane\":4,\"approach\":null,\"connection\":null}},\"minute\":null,\"second\":null,\"duration\":10979}]},\"requestor\":{\"id\":{\"entityID\":null,\"stationID\":2366845094},\"type\":{\"role\":\"publicTransport\",\"subrole\":null,\"request\":null,\"iso3883\":null,\"hpmsType\":null},\"position\":{\"position\":{\"latitude\":39.5904915,\"longitude\":-105.0913829,\"elevation\":1685.4},\"heading\":175.9000,\"speed\":null},\"name\":null,\"routeName\":null,\"transitStatus\":null,\"transitOccupancy\":null,\"transitSchedule\":null}},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SRM\"}}", SCHEMA_VERSION, ASN1_STRING);

    @Test
    public void shouldDeserializeJson() {
        final var deserialized = (OdeSrmData)JsonUtils.fromJson(json, OdeSrmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeSrmMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeSrmPayload);
        
    }

    @Test
    public void serializationShouldNotAddClassProperty() {
        final var deserialized = (OdeSrmData)JsonUtils.fromJson(json, OdeSrmData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson() throws Exception {
        final var deserialized = (OdeSrmData)JsonUtils.fromJson(json, OdeSrmData.class);
        final String serialized = deserialized.toJson(false);

        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-srm.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}

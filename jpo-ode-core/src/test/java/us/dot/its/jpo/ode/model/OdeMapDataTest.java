package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeMapDataTest {

    final String json = "{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"mapTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMapPayload\",\"serialId\":{\"streamId\":\"18d7c2e0-9158-4456-916d-5cd4b080d290\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-06-17T19:02:13.083984Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"mapSource\":\"RSU\",\"originIp\":\"10.11.81.25\"},\"payload\":{\"data\":{\"timeStamp\":null,\"msgIssueRevision\":2,\"layerType\":\"intersectionData\",\"layerID\":0,\"intersections\":null,\"roadSegments\":null,\"dataParameters\":null,\"restrictionList\":null},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735MAP\"}}";

    @Test
    public void shouldDeserializeJson() {
        final var deserialized = (OdeMapData)JsonUtils.fromJson(json, OdeMapData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeMapMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeMapPayload);
        
    }

    @Test
    public void serializationShouldNotAddClassProperty() {
        final var deserialized = (OdeMapData)JsonUtils.fromJson(json, OdeMapData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson() throws Exception {
        final var deserialized = (OdeMapData)JsonUtils.fromJson(json, OdeMapData.class);
        final String serialized = deserialized.toJson(false);

        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-map.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}


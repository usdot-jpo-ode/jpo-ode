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

public class OdePsmDataTest {
    private static final String SCHEMA_VERSION = "8";
    private static final String ASN1_STRING = "011d0000201a0000021bd86891de75f84da101c13f042e2214141fff00022c2000270000000163b2cc7986010000";
    
    final String json = String.format("{\"metadata\":{\"logFileName\":\"\",\"maxDurationTime\":0,\"odePacketID\":\"\",\"odeReceivedAt\":\"2023-09-21T15:30:14.926500Z\",\"odeTimStartDateTime\":\"\",\"originIp\":\"192.168.16.1\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdePsmPayload\",\"psmSource\":\"RSU\",\"receivedMessageDetails\":{\"rxSource\":\"NA\"},\"recordGeneratedAt\":\"\",\"recordType\":\"psmTx\",\"sanitized\":false,\"schemaVersion\":%s,\"securityResultCode\":\"success\",\"serialId\":{\"bundleId\":0,\"bundleSize\":1,\"recordId\":0,\"serialNumber\":0,\"streamId\":\"06cc1c17-e331-4806-a8ee-456b98c6517b\"},\"asn1\":\"%s\"},\"payload\":{\"data\":{\"accuracy\":{\"orientation\":44.9951935489,\"semiMajor\":1.0,\"semiMinor\":1.0},\"basicType\":\"aPEDESTRIAN\",\"heading\":8898,\"id\":\"24779D7E\",\"msgCnt\":26,\"position\":{\"latitude\":40.2397377,\"longitude\":-74.2761437},\"secMark\":3564,\"speed\":0},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735PSM\"}}", SCHEMA_VERSION, ASN1_STRING);

    @Test
    public void shouldDeserializeJson() {
        final var deserialized = (OdePsmData)JsonUtils.fromJson(json, OdePsmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdePsmMetadata);
        assertTrue(deserialized.getPayload() instanceof OdePsmPayload);
        
    }

    @Test
    public void serializationShouldNotAddClassProperty() {
        final var deserialized = (OdePsmData)JsonUtils.fromJson(json, OdePsmData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson() throws Exception {
        final var deserialized = (OdePsmData)JsonUtils.fromJson(json, OdePsmData.class);
        final String serialized = deserialized.toJson(false);

        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-psm.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}

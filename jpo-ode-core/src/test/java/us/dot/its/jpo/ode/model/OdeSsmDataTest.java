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

public class OdeSsmDataTest {
    private static final String SCHEMA_VERSION = "8";
    private static final String ASN1_STRING = "001e120000000005e9c04071a26614c06000040ba0";
    
    final String json = String.format("{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"ssmTx\",\"securityResultCode\":null,\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSsmPayload\",\"serialId\":{\"streamId\":\"b9801eb3-66fb-4d36-ae08-3e8f2bcb2026\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-12-13T19:00:42.326229Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.21.0.1\",\"ssmSource\":\"RSU\",\"asn1\":\"%s\"},\"payload\":{\"data\":{\"timeStamp\":null,\"second\":0,\"sequenceNumber\":null,\"status\":{\"signalStatus\":[{\"sequenceNumber\":0,\"id\":{\"region\":null,\"id\":12110},\"sigStatus\":{\"signalStatusPackage\":[{\"requester\":{\"id\":{\"entityID\":null,\"stationID\":2366845094},\"request\":3,\"sequenceNumber\":0,\"role\":null,\"typeData\":{\"role\":\"publicTransport\",\"subrole\":null,\"request\":null,\"iso3883\":null,\"hpmsType\":null}},\"inboundOn\":{\"lane\":23,\"approach\":null,\"connection\":null},\"outboundOn\":null,\"minute\":null,\"second\":null,\"duration\":null,\"status\":\"granted\"}]}}]}},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SSM\"}}", SCHEMA_VERSION, ASN1_STRING);

    @Test
    public void shouldDeserializeJson() {

        final var deserialized = (OdeSsmData)JsonUtils.fromJson(json, OdeSsmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeSsmMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeSsmPayload);
        
    }

    @Test
    public void serializationShouldNotAddClassProperty() {
        final var deserialized = (OdeSsmData)JsonUtils.fromJson(json, OdeSsmData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson() throws Exception {
        final var deserialized = (OdeSsmData)JsonUtils.fromJson(json, OdeSsmData.class);
        final String serialized = deserialized.toJson(false);

        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-ssm.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}

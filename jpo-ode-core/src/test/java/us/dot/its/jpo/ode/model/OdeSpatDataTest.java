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

public class OdeSpatDataTest {
    private static final String SCHEMA_VERSION = "8";
    private static final String ASN1_STRING = "001338000817a780000089680500204642b342b34802021a15a955a940181190acd0acd20100868555c555c00104342aae2aae002821a155715570";
    
    final String json = String.format("{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"spatTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSpatPayload\",\"serialId\":{\"streamId\":\"ed008249-0a8d-47f2-a526-ffd8c30b9810\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-12-24T01:49:54.160478Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"spatSource\":\"V2X\",\"originIp\":\"172.18.0.1\",\"isCertPresent\":false,\"asn1\":\"%s\"},\"payload\":{\"data\":{\"intersectionStateList\":{\"intersectionStatelist\":[{\"id\":{\"id\":12111},\"revision\":0,\"status\":{\"failureFlash\":false,\"noValidSPATisAvailableAtThisTime\":false,\"fixedTimeOperation\":false,\"standbyOperation\":false,\"trafficDependentOperation\":false,\"manualControlIsEnabled\":false,\"off\":false,\"stopTimeIsActivated\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"recentMAPmessageUpdate\":false,\"failureMode\":false,\"noValidMAPisAvailableAtThisTime\":false,\"signalPriorityIsActive\":false,\"preemptIsActive\":false},\"timeStamp\":35176,\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120,\"maxEndTime\":22121}}]}},{\"signalGroup\":4,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":22181,\"maxEndTime\":22181}}]}},{\"signalGroup\":6,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120,\"maxEndTime\":22121}}]}},{\"signalGroup\":8,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":21852,\"maxEndTime\":21852}}]}},{\"signalGroup\":1,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":21852,\"maxEndTime\":21852}}]}},{\"signalGroup\":5,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":21852,\"maxEndTime\":21852}}]}}]}}]}},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SPAT\"}}", SCHEMA_VERSION, ASN1_STRING);

    @Test
    public void shouldDeserializeJson() {
        final var deserialized = (OdeSpatData)JsonUtils.fromJson(json, OdeSpatData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeSpatMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeSpatPayload);
        
    }

    @Test
    public void serializationShouldNotAddClassProperty() {
        final var deserialized = (OdeSpatData)JsonUtils.fromJson(json, OdeSpatData.class);
        final String serialized = deserialized.toJson(false);
        assertFalse(serialized.contains("@class"));
    }

    @Test
    public void shouldValidateJson() throws Exception {
        final var deserialized = (OdeSpatData)JsonUtils.fromJson(json, OdeSpatData.class);
        final String serialized = deserialized.toJson(false);

        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-spat.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(serialized, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}

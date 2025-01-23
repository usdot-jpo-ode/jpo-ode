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

public class OdeMapDataTest {
    private static final String SCHEMA_VERSION = "8";
    private static final String ASN1_STRING = "001283c138003000205e9c014d3eab092ca624b5518202dc3658042800000400023622c60ca009f66d48abfaf81388d8ad18070027d9b2ffcfe9804f13667b1ffd009ec2c76e3ffc82c4e0001004b00c5000000800066c4574101813ecd8b757fae027d9b30e6ff5604ec363561fe7809ec6cd69bfec813c4d8a617fc9027d9b2147008604fb163666000016250000802580228000001000096229e1309b51a6fe4204dd361cf1fe5009f6018e1000096020a00000080004d88a57f84027d9b3827002804ec36087600a009f62c289407282c310001c0440188800000006c46dbe02813ec5816d800710052200000001b11b6fad404fb16054a0000401c8800000006c47b3d24813ec5816d801b100c4200000000af890f12c580007e87100d4200000008af4c0f12c580077e7a2c0004000160002001cb028d000000800052c160bc40b5fffd8a9409d86bfebb5b40141457fef53b76c008b467014145800080002bffcbffc82c6a0001804b024d000000800036c2213c3b013ecd80096d64027d9affd8cdfc04f635ff7983bc09f66c0082aa2014280b1b80006012c0b3400000100004b02bcf0f6d7fe065d602788b0138eb900b1240001012c083400000080009b0c2af0b804fb15fe6de171afff6c63e04ec15fe1de670060e40002581ea8000004000135da6df0180a0a6adc2c00d0143cd51897fda028c8abb25001a0b0680008012c105400000200009aedbefae005053540ee003c0a326a9cf3fed8143c5667780010582c0004009608aa00000080004d76de7ee402829aba88ffdc050f354525fff80a322bcf23fa602c690000c04b0395000000200016bb4fbd4e01414d3215800802940ab108fff2030d2000110126200000001aee5103be050a15f6f1ffc8404d8800000006bb97c18e0142857dfa800010146200000001aee89099a050a15f8720000b05dd000000800046be3743b781428d80e1b00002879b00514b4404f63600827d8c09e22c000400015ffe6007016190000402582ce8000004000135ecee1de80a146c02e54758143cd8059ad3e027b1b00613dd004f102c360000804b055d000000200046bcc7c3c781428d80108c6e02829b002b2ece050a16019a4b29b00ab5c3604f136004e410409ec018a10000960c3a00000080004d7de9878602851b003923cc05053601623b440a0a6bfb8c3a5014140b0640005012c197400000100005afe570ef2050a36003a47c80a0a6bfd2c45f014140b054000501101a8200000001b05a90edc050535ffe605800a0a101b8200000001b08a30ec0050535ffe605300a0a101c8200000005b0c6f0ea4050515ffca0568b0001000e";
    
    final String json = String.format("{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"mapTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMapPayload\",\"serialId\":{\"streamId\":\"18d7c2e0-9158-4456-916d-5cd4b080d290\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-06-17T19:02:13.083984Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"mapSource\":\"RSU\",\"originIp\":\"10.11.81.25\",\"asn1\":\"%s\"},\"payload\":{\"data\":{\"timeStamp\":null,\"msgIssueRevision\":2,\"layerType\":\"intersectionData\",\"layerID\":0,\"intersections\":null,\"roadSegments\":null,\"dataParameters\":null,\"restrictionList\":null},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735MAP\"}}",SCHEMA_VERSION,ASN1_STRING);

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


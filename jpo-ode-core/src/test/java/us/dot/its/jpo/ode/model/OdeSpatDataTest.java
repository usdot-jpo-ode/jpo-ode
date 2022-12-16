package us.dot.its.jpo.ode.model;

import org.junit.Test;
import static org.junit.Assert.*;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeSpatDataTest {
    @Test
    public void shouldDeserializeJson() {

        final String bsmJson = "{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"spatTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSpatPayload\",\"serialId\":{\"streamId\":\"24d9dfcb-1ca0-41b8-8576-f4a00a51218e\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-06-17T19:02:05.366148Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"spatSource\":\"V2X\",\"originIp\":\"10.11.81.12\",\"isCertPresent\":false},\"payload\":{\"data\":{\"timeStamp\":null,\"name\":null,\"intersectionStateList\":null},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SPAT\"}}";

        var deserialized = (OdeSpatData)JsonUtils.fromJson(bsmJson, OdeSpatData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeSpatMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeSpatPayload);
        
    }
}

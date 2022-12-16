package us.dot.its.jpo.ode.model;

import org.junit.Test;
import static org.junit.Assert.*;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeSsmDataTest {
    @Test
    public void shouldDeserializeJson() {

        final String bsmJson = "{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"ssmTx\",\"securityResultCode\":null,\"receivedMessageDetails\":{\"locationData\":null,\"rxSource\":\"NA\"},\"encodings\":null,\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSsmPayload\",\"serialId\":{\"streamId\":\"b9801eb3-66fb-4d36-ae08-3e8f2bcb2026\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2022-12-13T19:00:42.326229Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":null,\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.21.0.1\",\"ssmSource\":\"RSU\"},\"payload\":{\"data\":{\"timeStamp\":null,\"second\":0,\"sequenceNumber\":null,\"status\":{\"signalStatus\":[{\"sequenceNumber\":0,\"id\":{\"region\":null,\"id\":12110},\"sigStatus\":{\"signalStatusPackage\":[{\"requester\":{\"id\":{\"entityID\":null,\"stationID\":2366845094},\"request\":3,\"sequenceNumber\":0,\"role\":null,\"typeData\":{\"role\":\"publicTransport\",\"subrole\":null,\"request\":null,\"iso3883\":null,\"hpmsType\":null}},\"inboundOn\":{\"lane\":23,\"approach\":null,\"connection\":null},\"outboundOn\":null,\"minute\":null,\"second\":null,\"duration\":null,\"status\":\"granted\"}]}}]}},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SSM\"}}";

        var deserialized = (OdeSsmData)JsonUtils.fromJson(bsmJson, OdeSsmData.class);
        assertNotNull(deserialized);
        assertTrue(deserialized.getMetadata() instanceof OdeSsmMetadata);
        assertTrue(deserialized.getPayload() instanceof OdeSsmPayload);
        
    }
}

package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.model.OdeSsmData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeSsmDataCreatorHelperTest {
    @Test
	public void testConstructor() {
		OdeSsmDataCreatorHelper helper = new OdeSsmDataCreatorHelper();
		assertNotNull(helper);
	}

	@Test
	public void testCreateOdeSrmData() {
		String consumedData = "<?xml version=\"1.0\"?><OdeAsn1Data><metadata><logFileName/><recordType>ssmTx</recordType><securityResultCode>success</securityResultCode><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>75b0ddae-5f6e-403d-ae9d-ec41080bb500</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2024-11-11T21:42:11.755Z</odeReceivedAt><schemaVersion>7</schemaVersion><maxDurationTime>0</maxDurationTime><recordGeneratedAt/><recordGeneratedBy>RSU</recordGeneratedBy><sanitized>false</sanitized><odePacketID/><odeTimStartDateTime/><asn1>001E2366CF218CA0B40010BD4C2896A131B71C0450201685AD512AACB3B0105010402E4C6A805000</asn1><originIp>172.18.0.1</originIp><ssmSource>RSU</ssmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>30</messageId><value><SignalStatusMessage><timeStamp>446241</timeStamp><second>36000</second><sequenceNumber>90</sequenceNumber><status><SignalStatus><sequenceNumber>2</sequenceNumber><id><id>12115</id></id><sigStatus><SignalStatusPackage><requester><id><stationID>2823581127</stationID></id><request>1</request><sequenceNumber>10</sequenceNumber><role><publicTransport/></role></requester><inboundOn><lane>5</lane></inboundOn><duration>41323</duration><status><rejected/></status></SignalStatusPackage><SignalStatusPackage><requester><id><stationID>1435923970</stationID></id><request>10</request><sequenceNumber>1</sequenceNumber><role><publicTransport/></role></requester><inboundOn><lane>5</lane></inboundOn><duration>51597</duration><status><rejected/></status></SignalStatusPackage></sigStatus></SignalStatus></status></SignalStatusMessage></value></MessageFrame></data></payload></OdeAsn1Data>";
		try {
			XmlUtils.toObjectNode(consumedData);
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}

		String expectedJson = "{\"metadata\":{\"logFileName\":\"\",\"recordType\":\"ssmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeSsmPayload\",\"serialId\":{\"streamId\":\"75b0ddae-5f6e-403d-ae9d-ec41080bb500\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-11-11T21:42:11.755Z\",\"schemaVersion\":7,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"asn1\":\"001E2366CF218CA0B40010BD4C2896A131B71C0450201685AD512AACB3B0105010402E4C6A805000\",\"originIp\":\"172.18.0.1\",\"ssmSource\":\"RSU\"},\"payload\":{\"data\":{\"timeStamp\":446241,\"second\":36000,\"sequenceNumber\":90,\"status\":{\"signalStatus\":[{\"sequenceNumber\":2,\"id\":{\"id\":12115},\"sigStatus\":{\"signalStatusPackage\":[{\"requester\":{\"id\":{\"stationID\":2823581127},\"request\":1,\"sequenceNumber\":10,\"role\":\"publicTransport\"},\"inboundOn\":{\"lane\":5},\"duration\":41323,\"status\":\"rejected\"},{\"requester\":{\"id\":{\"stationID\":1435923970},\"request\":10,\"sequenceNumber\":1,\"role\":\"publicTransport\"},\"inboundOn\":{\"lane\":5},\"duration\":51597,\"status\":\"rejected\"}]}}]}},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735SSM\"}}";
		OdeSsmData ssmData;
		try {
			ssmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumedData);
			assertNotNull(ssmData);
			assertEquals(ssmData.toJson(), expectedJson);
		} catch (XmlUtilsException e) {
			e.printStackTrace();
		}

	}
}

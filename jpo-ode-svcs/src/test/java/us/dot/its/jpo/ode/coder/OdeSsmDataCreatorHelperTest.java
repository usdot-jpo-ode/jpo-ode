package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

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
		String consumedData = "<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>50b5374e-db5b-410f-84d8-c047b1571190</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2021-10-03T20:00:43.331224Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>ssmTx</recordType><securityResultCode/><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><originIp>172.250.250.77</originIp><ssmSource>RSU</ssmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>30</messageId><value><SignalStatusMessage><second>0</second><status><SignalStatus><sequenceNumber>0</sequenceNumber><id><id>12110</id></id><sigStatus><SignalStatusPackage><requester><id><stationID>2366845094</stationID></id><request>3</request><sequenceNumber>0</sequenceNumber><typeData><role><publicTransport/></role></typeData></requester><inboundOn><lane>23</lane></inboundOn><status><granted/></status></SignalStatusPackage></sigStatus></SignalStatus></status></SignalStatusMessage></value></MessageFrame></data></payload></OdeAsn1Data>";
		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode(consumedData);
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		OdeSsmData ssmData;
		try {
			ssmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumedData);
			assertNotNull(ssmData);
		} catch (XmlUtilsException e) {
			e.printStackTrace();
		}

	}
}

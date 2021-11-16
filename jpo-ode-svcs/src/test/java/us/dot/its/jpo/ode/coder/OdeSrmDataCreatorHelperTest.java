package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.model.OdeSrmData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeSrmDataCreatorHelperTest {
    @Test
	public void testConstructor() {
		OdeSrmDataCreatorHelper helper = new OdeSrmDataCreatorHelper();
		assertNotNull(helper);
	}

	@Test
	public void testCreateOdeSrmData() {
		String consumedData = "<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>18c95c67-a1bd-43e9-b93d-6480b59f8c81</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2021-10-07T06:29:31.198419Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>srmTx</recordType><securityResultCode/><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><originIp>172.250.250.77</originIp><srmSource>RSU</srmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>29</messageId><value><SignalRequestMessage><second>0</second><sequenceNumber>1</sequenceNumber><requests><SignalRequestPackage><request><id><id>12109</id></id><requestID>4</requestID><requestType><priorityRequest/></requestType><inBoundLane><lane>13</lane></inBoundLane><outBoundLane><lane>4</lane></outBoundLane></request><duration>10979</duration></SignalRequestPackage></requests><requestor><id><stationID>2366845094</stationID></id><type><role><publicTransport/></role></type><position><position><lat>395904915</lat><long>-1050913829</long><elevation>16854</elevation></position><heading>14072</heading></position></requestor></SignalRequestMessage></value></MessageFrame></data></payload></OdeAsn1Data>";
		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode(consumedData);
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		OdeSrmData srmData;
		try {
			srmData = OdeSrmDataCreatorHelper.createOdeSrmData(consumedData);
			assertNotNull(srmData);
		} catch (XmlUtilsException e) {
			e.printStackTrace();
		}

	}
}

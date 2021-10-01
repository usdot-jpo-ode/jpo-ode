package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeMapDataCreatorHelperTest {
	@Test
	public void testConstructor() {
		OdeMapDataCreatorHelper helper = new OdeMapDataCreatorHelper();
		assertNotNull(helper);
	}

	@Test
	public void testCreateOdeMapData() {
		String consumedData = "<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>ad1f0062-6ef2-4830-b546-ba97aeff683d</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2020-11-30T23:45:24.913657Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>mapTx</recordType><securityResultCode>success</securityResultCode><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><mapSource>V2X</mapSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>18</messageId><value><MapData><msgIssueRevision>4</msgIssueRevision><layerType><mixedContent/></layerType><layerID>12</layerID><intersections><IntersectionGeometry><id><id>156</id></id><revision>1</revision><refPoint><lat>389284111</lat><long>-772410713</long></refPoint><laneSet><GenericLane><laneID>1</laneID><laneAttributes><directionalUse>00</directionalUse><sharedWith>0000000000</sharedWith><laneType><vehicle>00000000</vehicle></laneType></laneAttributes><nodeList><nodes><NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY><NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY></nodes></nodeList></GenericLane></laneSet></IntersectionGeometry></intersections></MapData></value></MessageFrame></data></payload></OdeAsn1Data>";
		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode("<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>d07badec-84f0-48d8-8d4c-898fceaf4ecc</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2020-11-30T23:45:24.913657Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>mapTx</recordType><securityResultCode>success</securityResultCode><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><mapSource>V2X</mapSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>18</messageId><value><MapData><msgIssueRevision>4</msgIssueRevision><layerType><mixedContent/></layerType><layerID>12</layerID><intersections><IntersectionGeometry><id><id>156</id></id><revision>1</revision><refPoint><lat>389284111</lat><long>-772410713</long></refPoint><laneSet><GenericLane><laneID>1</laneID><laneAttributes><directionalUse>00</directionalUse><sharedWith>0000000000</sharedWith><laneType><vehicle>00000000</vehicle></laneType></laneAttributes><nodeList><nodes><NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY><NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY></nodes></nodeList></GenericLane></laneSet></IntersectionGeometry></intersections></MapData></value></MessageFrame></data></payload></OdeAsn1Data>");
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		OdeMapData mapData;
		try {
			mapData = OdeMapDataCreatorHelper.createOdeMapData(consumedData);
			assertNotNull(mapData);
		} catch (XmlUtilsException e) {
			e.printStackTrace();
		}

	}
}

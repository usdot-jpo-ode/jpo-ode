package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SRM;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class SRMBuilderTest {
    @Test
	public void shouldTranslateSrmSingle() {

		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode(
					"<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>18c95c67-a1bd-43e9-b93d-6480b59f8c81</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2021-10-07T06:29:31.198419Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>srmTx</recordType><securityResultCode/><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><originIp>172.250.250.77</originIp><srmSource>RSU</srmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>29</messageId><value><SignalRequestMessage><second>0</second><sequenceNumber>1</sequenceNumber><requests><SignalRequestPackage><request><id><id>12109</id></id><requestID>4</requestID><requestType><priorityRequest/></requestType><inBoundLane><lane>13</lane></inBoundLane><outBoundLane><lane>4</lane></outBoundLane></request><duration>10979</duration></SignalRequestPackage></requests><requestor><id><stationID>2366845094</stationID></id><type><role><publicTransport/></role></type><position><position><lat>395904915</lat><long>-1050913829</long><elevation>16854</elevation></position><heading>14072</heading></position></requestor></SignalRequestMessage></value></MessageFrame></data></payload></OdeAsn1Data>");
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		J2735SRM actualSrm = SRMBuilder.genericSRM(jsonMap.findValue("SignalRequestMessage"));	
		String expected ="{\"second\":0,\"sequenceNumber\":1,\"requests\":{\"signalRequestPackage\":[{\"request\":{\"id\":{\"id\":12109},\"requestID\":4,\"requestType\":\"priorityRequest\",\"inBoundLane\":{\"lane\":13},\"outBoundLane\":{\"lane\":4}},\"duration\":10979}]},\"requestor\":{\"id\":{\"stationID\":2366845094},\"type\":{\"role\":\"publicTransport\"},\"position\":{\"position\":{\"latitude\":39.5904915,\"longitude\":-105.0913829,\"elevation\":1685.4},\"heading\":175.9000}}}";
		assertEquals(expected, actualSrm.toString()); 
		
	}

    @Test
	public void shouldTranslateSrmList() {

		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode(
					"<OdeAsn1Data><metadata><logFileName/><recordType>srmTx</recordType><securityResultCode/><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>bb49cf33-9403-4746-ac11-0c3e86f39043</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2023-01-11T21:43:39.558284Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><odePacketID/><odeTimStartDateTime/><originIp>172.19.0.1</originIp><srmSource>RSU</srmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>29</messageId><value><SignalRequestMessage><timeStamp>6759</timeStamp><second>0</second><sequenceNumber>7</sequenceNumber><requests><SignalRequestPackage><request><id><id>12114</id></id><requestID>2</requestID><requestType><priorityRequest/></requestType><inBoundLane><lane>11</lane></inBoundLane><outBoundLane><lane>8</lane></outBoundLane></request><duration>25172</duration></SignalRequestPackage><SignalRequestPackage><request><id><id>12114</id></id><requestID>1</requestID><requestType><priorityCancellation/></requestType><inBoundLane><lane>12</lane></inBoundLane><outBoundLane><lane>7</lane></outBoundLane></request><duration>31032</duration></SignalRequestPackage></requests><requestor><id><stationID>2031825062</stationID></id><type><role><publicTransport/></role></type><position><position><lat>395572888</lat><long>-1050834514</long><elevation>16779</elevation></position><heading>16592</heading><speed><transmisson><unavailable/></transmisson><speed>727</speed></speed></position></requestor></SignalRequestMessage></value></MessageFrame></data></payload></OdeAsn1Data>");
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		J2735SRM actualSrm = SRMBuilder.genericSRM(jsonMap.findValue("SignalRequestMessage"));	
		String expected ="{\"timeStamp\":6759,\"second\":0,\"sequenceNumber\":7,\"requests\":{\"signalRequestPackage\":[{\"request\":{\"id\":{\"id\":12114},\"requestID\":2,\"requestType\":\"priorityRequest\",\"inBoundLane\":{\"lane\":11},\"outBoundLane\":{\"lane\":8}},\"duration\":25172},{\"request\":{\"id\":{\"id\":12114},\"requestID\":1,\"requestType\":\"priorityCancellation\",\"inBoundLane\":{\"lane\":12},\"outBoundLane\":{\"lane\":7}},\"duration\":31032}]},\"requestor\":{\"id\":{\"stationID\":2031825062},\"type\":{\"role\":\"publicTransport\"},\"position\":{\"position\":{\"latitude\":39.5572888,\"longitude\":-105.0834514,\"elevation\":1677.9},\"heading\":207.4000,\"speed\":{\"speed\":14.54,\"transmisson\":\"UNAVAILABLE\"}}}}";
		assertEquals(expected, actualSrm.toString()); 
		
	}

    @Test
	public void shouldTranslateSrmEmptyRequestList() {

		JsonNode jsonMap = null;
		try {
			jsonMap = XmlUtils.toObjectNode(
					"<OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>18c95c67-a1bd-43e9-b93d-6480b59f8c81</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2021-10-07T06:29:31.198419Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/><recordType>srmTx</recordType><securityResultCode/><receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><originIp>172.250.250.77</originIp><srmSource>RSU</srmSource></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>29</messageId><value><SignalRequestMessage><second>0</second><sequenceNumber>1</sequenceNumber><requests></requests><requestor><id><stationID>2366845094</stationID></id><type><role><publicTransport/></role></type><position><position><lat>395904915</lat><long>-1050913829</long><elevation>16854</elevation></position><heading>14072</heading></position></requestor></SignalRequestMessage></value></MessageFrame></data></payload></OdeAsn1Data>");
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		J2735SRM actualSrm = SRMBuilder.genericSRM(jsonMap.findValue("SignalRequestMessage"));	
		String expected ="{\"second\":0,\"sequenceNumber\":1,\"requestor\":{\"id\":{\"stationID\":2366845094},\"type\":{\"role\":\"publicTransport\"},\"position\":{\"position\":{\"latitude\":39.5904915,\"longitude\":-105.0913829,\"elevation\":1685.4},\"heading\":175.9000}}}";
		assertEquals(expected, actualSrm.toString()); 
		
	}
}

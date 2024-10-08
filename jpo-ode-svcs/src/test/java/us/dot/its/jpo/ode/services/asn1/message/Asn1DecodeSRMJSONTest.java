package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSrmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeSRMJSONTest {
	private final String json = "{\"metadata\":{\"recordType\":\"srmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"190cbd65-d1e2-488a-ba42-b7d3f03a5c69\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:24:04.113614500Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\",\"srmSource\":\"RSU\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeRawEncodedSRMJson(), "topic.OdeRawEncodedSRMJson");
	}

	@Test
	public void testProcess() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeSRMJSON testDecodeSrmJson = new Asn1DecodeSRMJSON(properties);
		
		OdeAsn1Data resultOdeObj = testDecodeSrmJson.process(json);

		// Validate the metadata
		OdeSrmMetadata jsonMetadataObj = (OdeSrmMetadata) resultOdeObj.getMetadata();
		assertEquals(jsonMetadataObj.getSrmSource(), OdeSrmMetadata.SrmSource.RSU);
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementName(), "unsecuredData");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementType(), "MessageFrame");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getEncodingRule(), EncodingRule.UPER);

		// Validate the payload
		String expectedPayload = "{\"bytes\":\"001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000\"}";
		OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
		assertEquals(jsonPayloadObj.getDataType(), "us.dot.its.jpo.ode.model.OdeHexByteArray");
		assertEquals(jsonPayloadObj.getData().toString(), expectedPayload);
	}
}

package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeSSMJSONTest {
	private final String json = "{\"metadata\":{\"recordType\":\"ssmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c4e4e92d-dccc-45f5-813f-7d36795529a0\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:31:02.907835400Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\",\"ssmSource\":\"RSU\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001E120000000005E9C04071A26614C06000040BA000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeRawEncodedSSMJson(), "topic.OdeRawEncodedSSMJson");
	}

	@Test
	public void testProcess() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeSSMJSON testDecodeSsmJson = new Asn1DecodeSSMJSON(properties);
		
		OdeAsn1Data resultOdeObj = testDecodeSsmJson.process(json);

		// Validate the metadata
		OdeSsmMetadata jsonMetadataObj = (OdeSsmMetadata) resultOdeObj.getMetadata();
		assertEquals(jsonMetadataObj.getSsmSource(), OdeSsmMetadata.SsmSource.RSU);
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementName(), "unsecuredData");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementType(), "MessageFrame");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getEncodingRule(), EncodingRule.UPER);

		// Validate the payload
		String expectedPayload = "{\"bytes\":\"001E120000000005E9C04071A26614C06000040BA000\"}";
		OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
		assertEquals(jsonPayloadObj.getDataType(), "us.dot.its.jpo.ode.model.OdeHexByteArray");
		assertEquals(jsonPayloadObj.getData().toString(), expectedPayload);
	}
}

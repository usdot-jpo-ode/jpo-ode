package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeSPATJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"spatTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"5ec410a3-bec6-4724-9601-1e08778e1dfc\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:43:22.604870100Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"spatSource\":\"RSU\",\"originIp\":\"192.168.0.1\",\"isCertPresent\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C00134700081132000000E437070010434257925790010232119A11CE800C10D095E495E400808684AF24AF20050434257925790030232119A11CE801C10D095E495E401008684AF24AF20000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeRawEncodedSPATJson(), "topic.OdeRawEncodedSPATJson");
	}

	@Test
	public void testProcess() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeSPATJSON testDecodeSpatJson = new Asn1DecodeSPATJSON(properties);
		
		OdeAsn1Data resultOdeObj = testDecodeSpatJson.process(json);

		// Validate the metadata
		OdeSpatMetadata jsonMetadataObj = (OdeSpatMetadata) resultOdeObj.getMetadata();
		assertEquals(jsonMetadataObj.getSpatSource(), OdeSpatMetadata.SpatSource.RSU);
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementName(), "unsecuredData");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementType(), "MessageFrame");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getEncodingRule(), EncodingRule.UPER);

		// Validate the payload
		String expectedPayload = "{\"bytes\":\"00134700081132000000E437070010434257925790010232119A11CE800C10D095E495E400808684AF24AF20050434257925790030232119A11CE801C10D095E495E401008684AF24AF200\"}";
		OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
		assertEquals(jsonPayloadObj.getDataType(), "us.dot.its.jpo.ode.model.OdeHexByteArray");
		assertEquals(jsonPayloadObj.getData().toString(), expectedPayload);
	}
}

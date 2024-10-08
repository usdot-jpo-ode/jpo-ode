package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeTIMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"timMsg\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"9952caf6-81bd-490d-ad95-47dee31c3ba8\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:38:48.578500100Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001F79201000000000012AA366D080729B8987D859717EE22001FFFE4FD0011589D828007E537130FB0B2E2FDC440001F46FFFF002B8B2E46E926E27CE6813D862CB90EDC9B89E11CE2CB8E98F9B89BCC4050518B2E365B66E26AE3B8B2E291A66E2591D8141462CB873969B89396C62CB86AFE9B89208E00000131560018300023E43A6A1351800023E4700EFC51881010100030180C620FB90CAAD3B9C5082080E1DDC905E10168E396921000325A0D73B83279C83010180034801090001260001808001838005008001F0408001828005008001304000041020407E800320409780050080012040000320409900018780032040958005000001E0408183E7139D7B70987019B526B8A950052F5C011D3C4B992143E885C71F95DA6071658082346CC03A50D66801F65288C30AB39673D0494536C559047E457AD291C99C20A7FB1244363E993EE3EE98C78742609340541DA01545A0F7339C26A527903576D30000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeRawEncodedTIMJson(), "topic.OdeRawEncodedTIMJson");
	}

	@Test
	public void testProcess() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeTIMJSON testDecodeTimJson = new Asn1DecodeTIMJSON(properties);
		
		OdeAsn1Data resultOdeObj = testDecodeTimJson.process(json);

		// Validate the metadata
		OdeTimMetadata jsonMetadataObj = (OdeTimMetadata) resultOdeObj.getMetadata();
		assertEquals(jsonMetadataObj.getRecordGeneratedBy(), OdeMsgMetadata.GeneratedBy.RSU);
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementName(), "unsecuredData");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getElementType(), "MessageFrame");
		assertEquals(jsonMetadataObj.getEncodings().get(0).getEncodingRule(), EncodingRule.UPER);

		// Validate the payload
		String expectedPayload = "{\"bytes\":\"001F79201000000000012AA366D080729B8987D859717EE22001FFFE4FD0011589D828007E537130FB0B2E2FDC440001F46FFFF002B8B2E46E926E27CE6813D862CB90EDC9B89E11CE2CB8E98F9B89BCC4050518B2E365B66E26AE3B8B2E291A66E2591D8141462CB873969B89396C62CB86AFE9B89208E00000131560018300023E43A6A1351800023E4700EFC51881010100030180C620FB90CAAD3B9C5082080E1DDC905E10168E396921000325A0D73B83279C83010180034801090001260001808001838005008001F0408001828005008001304000041020407E800320409780050080012040000320409900018780032040958005000001E0408183E7139D7B70987019B526B8A950052F5C011D3C4B992143E885C71F95DA6071658082346CC03A50D66801F65288C30AB39673D0494536C559047E457AD291C99C20A7FB1244363E993EE3EE98C78742609340541DA01545A0F7339C26A527903576D300\"}";
		OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
		assertEquals(jsonPayloadObj.getDataType(), "us.dot.its.jpo.ode.model.OdeHexByteArray");
		assertEquals(jsonPayloadObj.getData().toString(), expectedPayload);
	}
}

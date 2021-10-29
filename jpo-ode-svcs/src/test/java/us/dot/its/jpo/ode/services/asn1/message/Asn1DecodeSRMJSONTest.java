package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeSrmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeSRMJSONTest {
    
    private final String json = "{\"SrmMessageContent\": [{ \"metadata\": { \"utctimestamp\":\"2020-11-30T23:45:24.913657Z\", \"originRsu\":\"172.0.0.24\", \"source\":\"RSU\" }, \"payload\":\"001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0\"}]}";
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeSrmTxPojo(), "topic.OdeSrmTxPojo");
	}

	@Test
	public void testProcessSrmJson() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeSRMJSON testDecodeSrmJson = new Asn1DecodeSRMJSON(properties);
		assertEquals(testDecodeSrmJson.process(json), null);

		// metadata
		OdeData obj = new OdeData();
		OdeSrmMetadata jsonMetadataObj = new OdeSrmMetadata();
		jsonMetadataObj.setOdeReceivedAt("2020-11-30T23:45:24.913657Z");
		jsonObj.put("metadata", jsonMetadataObj);

		// payload
		String encodedPayload = "001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0";
		obj.setMetadata(jsonMetadataObj);
		obj.setPayload(new OdeAsn1Payload(new OdeHexByteArray(encodedPayload)));

		assertEquals("{\"bytes\":\"001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0\"}",
				obj.getPayload().getData().toJson());

	}
}

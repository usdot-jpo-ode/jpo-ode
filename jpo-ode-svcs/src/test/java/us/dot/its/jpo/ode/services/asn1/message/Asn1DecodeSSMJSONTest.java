package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeSSMJSONTest {
    
    private final String json = "{\"SsmMessageContent\": [{ \"metadata\": { \"utctimestamp\":\"2020-11-30T23:45:24.913657Z\", \"originRsu\":\"172.0.0.24\", \"source\":\"RSU\" }, \"payload\":\"001e120000000005e9c04071a26614c06000040ba0\"}]}";
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeSsmPojo(), "topic.OdeSsmPojo");
	}

	@Test
	public void testProcessSsmJson() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeSSMJSON testDecodeSsmJson = new Asn1DecodeSSMJSON(properties);
		assertEquals(testDecodeSsmJson.process(json), null);

		// metadata
		OdeData obj = new OdeData();
		OdeSsmMetadata jsonMetadataObj = new OdeSsmMetadata();
		jsonMetadataObj.setOdeReceivedAt("2020-11-30T23:45:24.913657Z");
		jsonObj.put("metadata", jsonMetadataObj);

		// payload
		String encodedPayload = "001e120000000005e9c04071a26614c06000040ba0";
		obj.setMetadata(jsonMetadataObj);
		obj.setPayload(new OdeAsn1Payload(new OdeHexByteArray(encodedPayload)));

		assertEquals("{\"bytes\":\"001e120000000005e9c04071a26614c06000040ba0\"}",
				obj.getPayload().getData().toJson());

	}
}

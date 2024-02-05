package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodePSMJSONTest {

	private final String json = "{\"PsmMessageContent\": [{ \"metadata\": { \"utctimestamp:\"2020-11-30T23:45:24.913657Z\" }, \"payload\":\"011d0000201a0000024ea0dc91de75f84da102c23f042dc41414ffff0006ba1000270000000111f7ca7986010000\"}]}";
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdePsmJson(), "topic.OdePsmJson");
	}

	@Test
	public void testProcessPsmJson() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodePSMJSON testDecodePsmJson = new Asn1DecodePSMJSON(properties);
		assertEquals(testDecodePsmJson.process(json), null);

		// metadata
		OdeData obj = new OdeData();
		OdePsmMetadata jsonMetadataObj = new OdePsmMetadata();
		jsonMetadataObj.setOdeReceivedAt("2020-11-30T23:45:24.913657Z");
		jsonObj.put("metadata", jsonMetadataObj);

		// payload
		String encodedPayload = "011d0000201a0000024ea0dc91de75f84da102c23f042dc41414ffff0006ba1000270000000111f7ca7986010000";
		obj.setMetadata(jsonMetadataObj);
		obj.setPayload(new OdeAsn1Payload(new OdeHexByteArray(encodedPayload)));

		assertEquals("{\"bytes\":\"011d0000201a0000024ea0dc91de75f84da102c23f042dc41414ffff0006ba1000270000000111f7ca7986010000\"}",
				obj.getPayload().getData().toJson());

	}

}

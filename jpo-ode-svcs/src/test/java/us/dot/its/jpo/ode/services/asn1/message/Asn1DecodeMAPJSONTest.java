package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1DecodeMAPJSONTest {

	private final String json = "{\"MapMessageContent\": [{ \"metadata\": { \"utctimestamp:\"2020-11-30T23:45:24.913657Z\" }, \"payload\":\"00121E38041180000138044CD8EA0F3D3FC4A600000100000000030AE0C0615C18\"}]}";
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testConstructor() {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		assertEquals(properties.getKafkaTopicOdeBsmPojo(), "topic.OdeBsmPojo");
	}

	@Test
	public void testProcessMapJson() throws XmlUtilsException, JSONException {
		OdeProperties properties = new OdeProperties();
		properties.setKafkaBrokers("localhost:9092");
		Asn1DecodeMAPJSON testDecodeMapJson = new Asn1DecodeMAPJSON(properties);
		assertEquals(testDecodeMapJson.process(json), null);

		// metadata
		OdeData obj = new OdeData();
		OdeMapMetadata jsonMetadataObj = new OdeMapMetadata();
		jsonMetadataObj.setOdeReceivedAt("2020-11-30T23:45:24.913657Z");
		jsonObj.put("metadata", jsonMetadataObj);

		// payload
		String encodedPayload = "00121E38041180000138044CD8EA0F3D3FC4A600000100000000030AE0C0615C18";
		obj.setMetadata(jsonMetadataObj);
		obj.setPayload(new OdeAsn1Payload(new OdeHexByteArray(encodedPayload)));

		assertEquals("{\"bytes\":\"00121E38041180000138044CD8EA0F3D3FC4A600000100000000030AE0C0615C18\"}",
				obj.getPayload().getData().toJson());

	}

}

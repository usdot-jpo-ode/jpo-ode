package us.dot.its.jpo.ode.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import lombok.extern.slf4j.Slf4j;

import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessageMessageFrame;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdePsmData;
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.model.OdePsmPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

@Slf4j
public class OdePsmDataCreatorHelper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final XmlMapper XML_MAPPER = new XmlMapper();

	static {
		// Configure XML_MAPPER
		XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		XML_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		XML_MAPPER.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		// This is important - it tells Jackson to unwrap single-value arrays
		XML_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	}

	public OdePsmDataCreatorHelper() {
	}

	public static OdePsmData createOdePsmData(String consumedData) throws XmlUtilsException, JsonProcessingException, IOException {
		ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

		JsonNode metadataNode = consumed.findValue(OdeMsgMetadata.METADATA_STRING);
		if (metadataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) metadataNode;
			object.remove(OdeMsgMetadata.ENCODINGS_STRING);

			// Psm header file does not have a location and use predefined set required
			// RxSource
			ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
			receivedMessageDetails.setRxSource(RxSource.NA);
			JsonNode jsonNode;
			try {
				jsonNode = OBJECT_MAPPER.readTree(receivedMessageDetails.toJson());
				object.set(OdeMsgMetadata.RECEIVEDMSGDETAILS_STRING, jsonNode);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		OdePsmMetadata metadata = (OdePsmMetadata) JsonUtils.fromJson(metadataNode.toString(), OdePsmMetadata.class);

		if (metadata.getSchemaVersion() <= 4) {
			metadata.setReceivedMessageDetails(null);
		}
		
		String messageFrameString = XmlUtils.findXmlContentString(consumedData, "MessageFrame");
		log.warn("MessageFrame XML: " + messageFrameString);
		
		PersonalSafetyMessageMessageFrame psmObject;
		try {
			// First parse to a tree
			JsonNode tree = XML_MAPPER.readTree(messageFrameString);
			log.warn("Tree: " + tree.toPrettyString());
			
			// Get the actual PSM content from inside 'value'
			JsonNode psmNode = tree.path("value").path("PersonalSafetyMessage");
			if (psmNode.isMissingNode()) {
				throw new IOException("Could not find PersonalSafetyMessage in XML");
			}
			
			// Create a new tree with the correct structure
			ObjectNode messageFrame = OBJECT_MAPPER.createObjectNode();
			messageFrame.put("messageId", tree.path("messageId").asText());
			messageFrame.set("value", psmNode);
			
			// Now deserialize from the restructured tree
			psmObject = XML_MAPPER.treeToValue(messageFrame, PersonalSafetyMessageMessageFrame.class);

		// PersonalSafetyMessageMessageFrame psmObject = XML_MAPPER.readValue(messageFrameString, PersonalSafetyMessageMessageFrame.class);

		} catch (Exception e) {
			log.error("Failed to deserialize PSM: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		OdePsmPayload payload = new OdePsmPayload(psmObject);
		return new OdePsmData(metadata, payload);
	}
}

package us.dot.its.jpo.ode.coder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdePsmData;
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.model.OdePsmPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.builders.PSMBuilder;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdePsmDataCreatorHelper {

	public OdePsmDataCreatorHelper() {
	}

	public static OdePsmData createOdePsmData(String consumedData) throws XmlUtilsException {
		ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

		JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
		if (metadataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) metadataNode;
			object.remove(AppContext.ENCODINGS_STRING);

			// Psm header file does not have a location and use predefined set required
			// RxSource
			ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
			receivedMessageDetails.setRxSource(RxSource.NA);
			ObjectMapper objectPsmper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectPsmper.readTree(receivedMessageDetails.toJson());
				object.set(AppContext.RECEIVEDMSGDETAILS_STRING, jsonNode);
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

		OdePsmPayload payload = new OdePsmPayload(PSMBuilder.genericPSM(consumed.findValue("PsmData")));
		return new OdePsmData(metadata, payload);
	}
}

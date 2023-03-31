package us.dot.its.jpo.ode.coder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeSpatData;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.OdeSpatPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.builders.SPATBuilder;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeSpatDataCreatorHelper {

	public OdeSpatDataCreatorHelper() {
	}

	public static OdeSpatData createOdeSpatData(String consumedData) throws XmlUtilsException {
		ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

		JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
		if (metadataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) metadataNode;
			object.remove(AppContext.ENCODINGS_STRING);
			
			//Spat header file does not have a location and use predefined set required RxSource
			ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
			receivedMessageDetails.setRxSource(RxSource.NA);
			 ObjectMapper objectMapper = new ObjectMapper();
			 JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(receivedMessageDetails.toJson());
				object.set(AppContext.RECEIVEDMSGDETAILS_STRING, jsonNode);
			} catch (JsonProcessingException e) {				
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		OdeSpatMetadata metadata = (OdeSpatMetadata) JsonUtils.fromJson(metadataNode.toString(), OdeSpatMetadata.class);
		
		if(metadataNode.findValue("certPresent") != null) {
			boolean isCertPresent = metadataNode.findValue("certPresent").asBoolean();
			metadata.setIsCertPresent(isCertPresent);
		}

		if (metadata.getSchemaVersion() <= 4) {
			metadata.setReceivedMessageDetails(null);
		}

		OdeSpatPayload payload = new OdeSpatPayload(SPATBuilder.genericSPAT(consumed.findValue("SPAT")));
		return new OdeSpatData(metadata, payload);
	}
}

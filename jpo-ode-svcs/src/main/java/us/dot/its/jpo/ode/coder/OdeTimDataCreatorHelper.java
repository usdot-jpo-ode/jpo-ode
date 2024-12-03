package us.dot.its.jpo.ode.coder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.DSRC.TravelerInformation;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeTimDataCreatorHelper {

    public OdeTimDataCreatorHelper() {
	}

	public static OdeTimData createOdeTimDataFromDecoded(String consumedData) throws XmlUtilsException {
		ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

		JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
		if (metadataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) metadataNode;
			object.remove(AppContext.ENCODINGS_STRING);

			// Map header file does not have a location and use predefined set required
			// RxSource
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

		OdeTimMetadata metadata = (OdeTimMetadata) JsonUtils.fromJson(metadataNode.toString(), OdeTimMetadata.class);

		if (metadata.getSchemaVersion() <= 4) {
			metadata.setReceivedMessageDetails(null);
		}

		String travelerInformationXml = XmlUtils.findXmlContentString(consumedData, "TravelerInformation");
		TravelerInformation timObject = (TravelerInformation)XmlUtils.fromXmlS(travelerInformationXml, TravelerInformation.class);
		OdeTimPayload payload = new OdeTimPayload(timObject);
		return new OdeTimData(metadata, payload);
	}

	public static OdeTimData createOdeTimDataFromCreator(String consumedData, OdeMsgMetadata metadata) throws XmlUtilsException {
		String travelerInformationXml = XmlUtils.findXmlContentString(consumedData, "TravelerInformation");
		TravelerInformation timObject = (TravelerInformation)XmlUtils.fromXmlS(travelerInformationXml, TravelerInformation.class);
		OdeTimPayload payload = new OdeTimPayload(timObject);
		return new OdeTimData(metadata, payload);
	}
}

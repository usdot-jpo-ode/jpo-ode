package us.dot.its.jpo.ode.coder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeSsmData;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.model.OdeSsmPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.builders.SSMBuilder;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeSsmDataCreatorHelper {

    public OdeSsmDataCreatorHelper() {
    }

    public static OdeSsmData createOdeSsmData(String consumedData) throws XmlUtilsException {
        ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

        JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
        if (metadataNode instanceof ObjectNode) {
            ObjectNode object = (ObjectNode) metadataNode;
            object.remove(AppContext.ENCODINGS_STRING);

            // Ssm header file does not have a location and use predefined set required
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
        
        OdeSsmMetadata metadata = (OdeSsmMetadata) JsonUtils.fromJson(metadataNode.toString(), OdeSsmMetadata.class);

        if (metadata.getSchemaVersion() <= 4) {
            metadata.setReceivedMessageDetails(null);
        }

        OdeSsmPayload payload = new OdeSsmPayload(SSMBuilder.genericSSM(consumed.findValue("SignalStatusMessage")));
        return new OdeSsmData(metadata, payload);
    }
}

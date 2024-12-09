package us.dot.its.jpo.ode.coder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * Helper class for deserializing TIM messages in XML/XER format into POJOs.
 */
@Slf4j
public class OdeTimDataCreatorHelper {

  /**
   * Deserializes XML/XER from the UDP decoded pipeline.
   *
   * @param consumedData The XML/XER as a String.
   */
  public static OdeTimData createOdeTimDataFromDecoded(String consumedData) 
      throws XmlUtilsException {
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
      } catch (Exception e) {
        log.error("Failed to read JSON node: {}", e.getMessage());
      }
    }

    OdeTimMetadata metadata = (OdeTimMetadata) JsonUtils.fromJson(metadataNode.toString(), 
        OdeTimMetadata.class);

    if (metadata != null && metadata.getSchemaVersion() <= 4) {
      metadata.setReceivedMessageDetails(null);
    }

    String travelerInformationXml = XmlUtils.findXmlContentString(consumedData, 
        "TravelerInformation");
    TravelerInformation timObject = (TravelerInformation) XmlUtils.fromXmlS(travelerInformationXml,
        TravelerInformation.class);
    OdeTimPayload payload = new OdeTimPayload(timObject);
    return new OdeTimData(metadata, payload);
  }

  /**
   * Deserializes XML/XER from the TIM creator endpoint.
   *
   * @param consumedData The XML/XER as a String.
   * @param metadata The pre-built ODE metadata object with unique TIM creator data.
   */
  public static OdeTimData createOdeTimDataFromCreator(String consumedData, OdeMsgMetadata metadata)
      throws XmlUtilsException {
    String travelerInformationXml = XmlUtils.findXmlContentString(consumedData, 
        "TravelerInformation");
    TravelerInformation timObject = (TravelerInformation) XmlUtils.fromXmlS(travelerInformationXml,
        TravelerInformation.class);
    OdeTimPayload payload = new OdeTimPayload(timObject);
    return new OdeTimData(metadata, payload);
  }
}

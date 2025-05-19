package us.dot.its.jpo.ode.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;

/**
 * Helper class for creating OdeMessageFrameData objects from consumed data.
 */
@Slf4j
public class OdeMessageFrameDataCreatorHelper {

  private OdeMessageFrameDataCreatorHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Creates an OdeMessageFrameData object from consumed XML data.
   *
   * @param consumedData The XML data string to be processed
   * @return OdeMessageFrameData object containing the processed data
   * @throws JsonProcessingException if there is an error processing the JSON data
   */
  public static OdeMessageFrameData createOdeMessageFrameData(String consumedData)
      throws JsonProcessingException {
    ObjectNode consumed = XmlUtils.getPlainXmlMapper().readValue(consumedData, ObjectNode.class);
    JsonNode metadataNode = consumed.findValue(OdeMsgMetadata.METADATA_STRING);
    if (metadataNode instanceof ObjectNode object) {
      object.remove(OdeMsgMetadata.ENCODINGS_STRING);

      // Spat header file does not have a location and use predefined set required RxSource
      ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
      receivedMessageDetails.setRxSource(RxSource.NA);

      JsonNode jsonNode;
      try {
        jsonNode = JsonUtils.getPlainMapper().readTree(receivedMessageDetails.toJson());
        object.set(OdeMsgMetadata.RECEIVEDMSGDETAILS_STRING, jsonNode);
      } catch (IOException e) {
        log.error("Failed to parse receivedMessageDetails", e);
        throw new RuntimeException(e);
      }
    }

    OdeMessageFrameMetadata metadata =
        XmlUtils.getPlainXmlMapper().convertValue(metadataNode, OdeMessageFrameMetadata.class);

    if (metadataNode.findValue("certPresent") != null) {
      boolean isCertPresent = metadataNode.findValue("certPresent").asBoolean();
      metadata.setCertPresent(isCertPresent);
    }

    if (metadata.getSchemaVersion() <= 4) {
      metadata.setReceivedMessageDetails(null);
    }

    JsonNode messageFrameNode = consumed.findValue("MessageFrame");
    MessageFrame<?> messageFrame =
        XmlUtils.getPlainXmlMapper().convertValue(messageFrameNode, MessageFrame.class);
    OdeMessageFramePayload payload = new OdeMessageFramePayload(messageFrame);
    return new OdeMessageFrameData(metadata, payload);
  }
}

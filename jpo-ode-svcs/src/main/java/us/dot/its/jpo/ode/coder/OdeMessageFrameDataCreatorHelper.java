package us.dot.its.jpo.ode.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.RxSource;

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
   * @param simpleObjectMapper The ObjectMapper for simple JSON operations
   * @param simpleXmlMapper The XmlMapper for XML operations
   * @return OdeMessageFrameData object containing the processed data
   * @throws JsonProcessingException if there is an error processing the JSON data
   */
  public static OdeMessageFrameData createOdeMessageFrameData(String consumedData, 
      ObjectMapper simpleObjectMapper, XmlMapper simpleXmlMapper) throws JsonProcessingException {
    ObjectNode consumed = simpleXmlMapper.readValue(consumedData, ObjectNode.class);
    JsonNode metadataNode = consumed.findValue(OdeMsgMetadata.METADATA_STRING);
    if (metadataNode instanceof ObjectNode object) {
      object.remove(OdeMsgMetadata.ENCODINGS_STRING);      
    }

    OdeMessageFrameMetadata metadata =
          simpleXmlMapper.convertValue(metadataNode, OdeMessageFrameMetadata.class);
    // Header file does not have a location and use predefined set required RxSource
    metadata.getReceivedMessageDetails().setRxSource(RxSource.NA);

    if (metadata.getSchemaVersion() <= 4) {
      metadata.setReceivedMessageDetails(null);
    }

    JsonNode messageFrameNode = consumed.findValue("MessageFrame");
    MessageFrame<?> messageFrame =
        simpleXmlMapper.convertValue(messageFrameNode, MessageFrame.class);
    OdeMessageFramePayload payload = new OdeMessageFramePayload(messageFrame);
    return new OdeMessageFrameData(metadata, payload);
  }
}

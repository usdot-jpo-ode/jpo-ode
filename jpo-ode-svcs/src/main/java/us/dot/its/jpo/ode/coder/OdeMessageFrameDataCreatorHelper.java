package us.dot.its.jpo.ode.coder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.deser.XmlReadContext;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.ServiceRequest;

/**
 * Helper class for creating OdeMessageFrameData objects from consumed data.
 */
@Slf4j
public class OdeMessageFrameDataCreatorHelper {

  private OdeMessageFrameDataCreatorHelper() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Reads the MessageFrame directly from its original payload/data XML path so mixed XML choices
   * retain their source order during generic subtype binding.
   *
   * @param xml The complete ODE XML document
   * @param simpleXmlMapper The XmlMapper for XML operations
   * @return The typed MessageFrame
   * @throws JsonProcessingException If the expected MessageFrame element cannot be read
   */
  public static MessageFrame<?> readMessageFrame(String xml, XmlMapper simpleXmlMapper)
      throws JsonProcessingException {
    try (JsonParser parser = simpleXmlMapper.createParser(xml)) {
      JsonToken token;
      while ((token = parser.nextToken()) != null) {
        if (token == JsonToken.START_OBJECT && parser instanceof FromXmlParser xmlParser
            && isPayloadDataMessageFrame(xmlParser)) {
          return simpleXmlMapper.readValue(parser, MessageFrame.class);
        }
      }
      throw JsonMappingException.from(parser,
          "MessageFrame was not found at payload/data/MessageFrame");
    } catch (JsonProcessingException e) {
      throw e;
    } catch (java.io.IOException e) {
      throw JsonMappingException.from((JsonParser) null,
          "Failed to read MessageFrame at payload/data/MessageFrame", e);
    }
  }

  private static boolean isPayloadDataMessageFrame(FromXmlParser parser) {
    XmlReadContext frameField = parser.getParsingContext().getParent();
    XmlReadContext dataField = frameField == null ? null : frameField.getParent();
    XmlReadContext payloadField = dataField == null ? null : dataField.getParent();
    XmlReadContext documentContext = payloadField == null ? null : payloadField.getParent();
    boolean atDocumentRoot = payloadField != null && (documentContext == null
        || (documentContext.getParent() == null && documentContext.getCurrentName() == null));
    return hasName(frameField, "MessageFrame")
        && hasName(dataField, "data")
        && hasName(payloadField, "payload")
        && atDocumentRoot;
  }

  private static boolean hasName(XmlReadContext context, String name) {
    return context != null && name.equals(context.getCurrentName());
  }

  /**
   * Creates an OdeMessageFrameData object from consumed XML data.
   *
   * @param consumedData The XML data string to be processed
   * @param simpleXmlMapper The XmlMapper for XML operations
   * @return OdeMessageFrameData object containing the processed data
   * @throws JsonProcessingException if there is an error processing the JSON data
   */
  public static OdeMessageFrameData createOdeMessageFrameData(String consumedData, 
      XmlMapper simpleXmlMapper) throws JsonProcessingException {
    // Bind the MessageFrame from the original XML before mapping metadata through a tree.
    final MessageFrame<?> messageFrame = readMessageFrame(consumedData, simpleXmlMapper);

    // Parse the XML into a tree structure for metadata and request normalization.
    JsonNode rootNode = simpleXmlMapper.readTree(consumedData);
    
    // Extract and deserialize metadata separately
    JsonNode metadataNode = rootNode.get("metadata");
    ServiceRequest request = null;
    if (metadataNode instanceof ObjectNode object) {
      if (object.has("request")) {
        JsonNode requestNode = object.get("request");
        // Check if "request" is present and not an empty object
        if (requestNode != null && requestNode.isObject() && requestNode.size() > 0) {
          String xmlBack = simpleXmlMapper.writeValueAsString(requestNode);
          request = simpleXmlMapper.readValue(xmlBack, ServiceRequest.class);
        }
        object.remove("request");
      }
    }
    OdeMessageFrameMetadata metadata = simpleXmlMapper.treeToValue(metadataNode, OdeMessageFrameMetadata.class);
    metadata.setRequest(request);
    // Setting encodings to null as per the original code logic but is technically supportable
    metadata.setEncodings(null);

    // Assign the rxSource if it does not exist due to the schema requiring it
    if (metadata.getReceivedMessageDetails() != null && metadata.getReceivedMessageDetails().getRxSource() == null) {
      metadata.getReceivedMessageDetails().setRxSource(RxSource.NA);
    }

    if (metadata.getSchemaVersion() <= 4) {
      metadata.setReceivedMessageDetails(null);
    }

    OdeMessageFramePayload payload = new OdeMessageFramePayload(messageFrame);
    return new OdeMessageFrameData(metadata, payload);
  }
}

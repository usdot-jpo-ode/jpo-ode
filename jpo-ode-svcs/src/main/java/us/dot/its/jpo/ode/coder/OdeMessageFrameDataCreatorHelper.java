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


@Slf4j
public class OdeMessageFrameDataCreatorHelper {

  public static OdeMessageFrameData createOdeMessageFrameData(String consumedData) throws JsonProcessingException {
    log.debug("createOdeMessageFrameData");
    ObjectNode consumed = XmlUtils.getPlainXmlMapper().readValue(consumedData, ObjectNode.class);
    log.debug("consumed");

    JsonNode metadataNode = consumed.findValue(OdeMsgMetadata.METADATA_STRING);
    log.debug("got metadata node");
    if (metadataNode instanceof ObjectNode object) {
      log.debug("metadata node is object");
      object.remove(OdeMsgMetadata.ENCODINGS_STRING);

      //Spat header file does not have a location and use predefined set required RxSource
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

    OdeMessageFrameMetadata metadata = XmlUtils.getPlainXmlMapper().convertValue(metadataNode, OdeMessageFrameMetadata.class);
    log.debug("got messageframe metadata");

    if(metadataNode.findValue("certPresent") != null) {
      boolean isCertPresent = metadataNode.findValue("certPresent").asBoolean();
      metadata.setCertPresent(isCertPresent);
    }
    log.debug("after cert check");

    if (metadata.getSchemaVersion() <= 4) {
      metadata.setReceivedMessageDetails(null);
    }

    log.debug("after schema version check");


    JsonNode messageFrameNode = consumed.findValue("MessageFrame");
    log.debug("got messageframe node");

    MessageFrame<?> messageFrame = XmlUtils.getPlainXmlMapper().convertValue(messageFrameNode, MessageFrame.class);

    log.debug("got message frame");

    OdeMessageFramePayload payload = new OdeMessageFramePayload(messageFrame);
    log.debug("got payload");
    return new OdeMessageFrameData(metadata, payload);
  }



}
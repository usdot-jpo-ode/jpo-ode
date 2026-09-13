package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import j2735ffm.AsnEncoding;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * In-process J2735 XER→UPER encode service backed by FFMLib.
 *
 * <p>Replaces the former Kafka hop through {@code topic.Asn1EncoderInput} /
 * {@code topic.Asn1EncoderOutput} (and the external asn1_codec AEM process).
 */
@Slf4j
@Service
public class FfmlibEncodeService {

  private static final String MESSAGE_FRAME = "MessageFrame";
  private static final String ADVISORY_SITUATION_DATA = "AdvisorySituationData";
  private static final String BYTES = "bytes";
  private static final String REQUEST = "request";
  private static final String RSUS = "rsus";

  private final ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec;
  private final XmlMapper simpleXmlMapper;

  public FfmlibEncodeService(
      ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec,
      @Qualifier("simpleXmlMapper") XmlMapper simpleXmlMapper) {
    this.ffmlibCodec = ffmlibCodec;
    this.simpleXmlMapper = simpleXmlMapper;
  }

  /**
   * Encodes a typed J2735 MessageFrame. Serializing the generated jpo-asn-pojo first produces
   * canonical J2735 XER, which is the form required by the native ASN1C codec.
   */
  public String encodeMessageFrame(
      MessageFrame<?> messageFrame, OdeMessageFrameMetadata metadata) throws Exception {
    ObjectNode metadataNode = JsonUtils.toObjectNode(JsonUtils.toJson(metadata, false));
    return encodeMessageFrame(messageFrame, metadataNode);
  }

  /**
   * Encodes a legacy encoder-input {@code OdeAsn1Data} XML document containing a MessageFrame.
   * The frame is first mapped to a generated J2735 POJO and reserialized as canonical XER.
   * AdvisorySituationData is encoded through the generic codec after its nested advisory message
   * is converted to UPER or IEEE 1609.2 COER bytes.
   *
   * @param odeAsn1Xml encoder-input style XML ({@code <OdeAsn1Data>...})
   * @return encoder-output style XML with hex UPER bytes in the payload
   */
  public String encodeOdeAsn1Xml(String odeAsn1Xml) throws Exception {
    JSONObject inputObj = XmlUtils.toJSONObject(odeAsn1Xml)
        .getJSONObject(OdeAsn1Data.class.getSimpleName());

    JSONObject metadata = inputObj.getJSONObject(OdeMsgMetadata.METADATA_STRING);
    JSONObject payloadData = inputObj.getJSONObject(OdeMsgPayload.PAYLOAD_STRING)
        .getJSONObject(OdeMsgPayload.DATA_STRING);

    if (payloadData.has(ADVISORY_SITUATION_DATA)) {
      JsonNode asdNode = simpleXmlMapper.readTree(odeAsn1Xml)
          .path(OdeMsgPayload.PAYLOAD_STRING)
          .path(OdeMsgPayload.DATA_STRING)
          .path(ADVISORY_SITUATION_DATA);
      if (!(asdNode instanceof ObjectNode asd)) {
        throw new IllegalArgumentException("AdvisorySituationData XML not found in encode input");
      }
      encodeNestedAdvisoryMessage(asd);
      String asdXer = simpleXmlMapper.writer()
          .withRootName(ADVISORY_SITUATION_DATA)
          .writeValueAsString(asd);
      byte[] uper = codec().encodeFromXer(
          asdXer, ADVISORY_SITUATION_DATA, AsnEncoding.UPER);
      ObjectNode metadataNode = JsonUtils.toObjectNode(metadata.toString());
      wrapRsuArrayForXml(metadataNode);
      return buildEncoderOutputXml(
          metadataNode, ADVISORY_SITUATION_DATA, CodecUtils.toHex(uper));
    } else if (payloadData.has(MESSAGE_FRAME)) {
      var messageFrameNode = simpleXmlMapper.readTree(odeAsn1Xml)
          .path(OdeMsgPayload.PAYLOAD_STRING)
          .path(OdeMsgPayload.DATA_STRING)
          .path(MESSAGE_FRAME);
      if (messageFrameNode.isMissingNode()) {
        throw new IllegalArgumentException("MessageFrame XML not found in encode input");
      }
      MessageFrame<?> messageFrame = simpleXmlMapper.convertValue(messageFrameNode, MessageFrame.class);
      return encodeMessageFrame(messageFrame, JsonUtils.toObjectNode(metadata.toString()));
    } else {
      throw new IllegalArgumentException(
          "Encode input has neither MessageFrame nor AdvisorySituationData");
    }
  }

  private String encodeMessageFrame(MessageFrame<?> messageFrame, ObjectNode metadata)
      throws Exception {
    String canonicalXer = simpleXmlMapper.writeValueAsString(messageFrame);
    byte[] uper = codec().xerToUper(canonicalXer);
    log.debug("FFMLib encoded MessageFrame ({} bytes)", uper.length);
    wrapRsuArrayForXml(metadata);
    return buildEncoderOutputXml(metadata, MESSAGE_FRAME, CodecUtils.toHex(uper));
  }

  private void encodeNestedAdvisoryMessage(ObjectNode asd) throws Exception {
    JsonNode details = asd.path("asdmDetails");
    if (!(details instanceof ObjectNode detailsObject)) {
      throw new IllegalArgumentException("AdvisorySituationData.asdmDetails is required");
    }
    JsonNode advisory = detailsObject.path("advisoryMessage");
    if (advisory.isTextual()) {
      return;
    }
    byte[] encoded;
    if (advisory.has("Ieee1609Dot2Data")) {
      String xer = simpleXmlMapper.writer().withRootName("Ieee1609Dot2Data")
          .writeValueAsString(advisory.path("Ieee1609Dot2Data"));
      encoded = codec().encodeFromXer(xer, "Ieee1609Dot2Data", AsnEncoding.COER);
    } else if (advisory.has(MESSAGE_FRAME)) {
      MessageFrame<?> frame = simpleXmlMapper.convertValue(
          advisory.path(MESSAGE_FRAME), MessageFrame.class);
      encoded = codec().xerToUper(simpleXmlMapper.writeValueAsString(frame));
    } else {
      throw new IllegalArgumentException(
          "advisoryMessage must contain encoded bytes, MessageFrame, or Ieee1609Dot2Data");
    }
    detailsObject.put("advisoryMessage", CodecUtils.toHex(encoded));
  }

  private FfmlibMessageFrameCodec codec() {
    FfmlibMessageFrameCodec codec = ffmlibCodec.getIfAvailable();
    if (codec == null) {
      throw new IllegalStateException("FFM codec is unavailable while ode.asn1.codec-mode=ffm");
    }
    return codec;
  }

  /**
   * Preserves the legacy encoder-output XML shape expected by Asn1EncodedDataRouter. A JSON
   * array with one RSU otherwise becomes a JSON object after XML round-trip and cannot be mapped
   * back to ServiceRequest.rsu[].
   */
  private void wrapRsuArrayForXml(ObjectNode metadata) {
    JsonNode request = metadata.get(REQUEST);
    if (request instanceof ObjectNode requestNode && requestNode.get(RSUS) instanceof ArrayNode rsus) {
      requestNode.set(RSUS, XmlUtils.createEmbeddedJsonArrayForXmlConversion(RSUS, rsus));
    }
  }

  private String buildEncoderOutputXml(ObjectNode metadata, String dataKey, String hexBytes)
      throws XmlUtilsException, JsonUtils.JsonUtilsException {
    ObjectNode bytesNode = JsonUtils.newNode();
    bytesNode.put(BYTES, hexBytes);

    ObjectNode dataKeyNode = JsonUtils.newNode();
    dataKeyNode.set(dataKey, bytesNode);

    ObjectNode payloadNode = JsonUtils.newNode();
    payloadNode.set(OdeMsgPayload.DATA_STRING, dataKeyNode);

    ObjectNode message = JsonUtils.newNode();
    message.set(OdeMsgMetadata.METADATA_STRING, metadata);
    message.set(OdeMsgPayload.PAYLOAD_STRING, payloadNode);

    ObjectNode root = JsonUtils.newNode();
    root.set(OdeAsn1Data.ODE_ASN1_DATA, message);

    String xml = XmlUtils.toXmlStatic(root);
    return xml.replace("<ObjectNode>", "").replace("</ObjectNode>", "");
  }
}

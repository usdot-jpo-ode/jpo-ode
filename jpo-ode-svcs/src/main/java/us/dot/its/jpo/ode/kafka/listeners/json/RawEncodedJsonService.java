package us.dot.its.jpo.ode.kafka.listeners.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.CodecUtils;

/**
 * Service class responsible for processing raw ASN.1 encoded JSON data, applying specific
 * encodings, and mutating the payload bytes to comply with desired formats.
 */
@Service
public class RawEncodedJsonService {

  private final ObjectMapper mapper;

  /**
   * Creates the raw-encoded JSON service.
   *
   * @param mapper JSON mapper used to read message metadata
   */
  public RawEncodedJsonService(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Processes the given JSON string and constructs an OdeAsn1Data object by extracting and encoding
   * metadata and payload information. The metadata is mutated by adding an Asn1Encoding. Converts
   * the payload bytes from hexadecimal string format after stripping IEEE 1609.2 security headers.
   *
   * @param json          the JSON string containing the metadata and payload information
   * @param messageType   the type of message to determine the start flag for processing the
   *                      payload
   * @param metadataClass the class type of OdeLogMetadata to which the JSON metadata should be
   *                      deserialized
   * @return an OdeAsn1Data object containing the processed metadata and payload
   * @throws JsonProcessingException    if there is an error processing the JSON input
   * @throws StartFlagNotFoundException if the specified start flag is not found in the payload
   */
  public OdeAsn1Data addEncodingAndMutateBytes(String json, SupportedMessageType messageType,
      Class<? extends OdeLogMetadata> metadataClass)
      throws JsonProcessingException, StartFlagNotFoundException {
    JSONObject rawJsonObject = new JSONObject(json);

    String jsonStringMetadata = rawJsonObject.get("metadata").toString();
    var metadata = mapper.readValue(jsonStringMetadata, metadataClass);

    Asn1Encoding
        unsecuredDataEncoding =
        new Asn1Encoding("unsecuredData", "MessageFrame", EncodingRule.UPER);
    metadata.addEncoding(unsecuredDataEncoding);

    String payloadHexString =
        ((JSONObject) ((JSONObject) rawJsonObject.get("payload")).get("data")).getString(
            "bytes");
    payloadHexString = UperUtil.stripDot2Header(payloadHexString, messageType.getStartFlag());

    OdeAsn1Payload payload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
    return new OdeAsn1Data(metadata, payload);
  }

  /**
   * Parses the existing raw-topic contract once for in-process decoding, avoiding a JSON parse,
   * hex encode, and second hex parse on the hot path.
   *
   * @param json raw-topic JSON value
   * @param messageType message type used to locate the UPER start flag
   * @return metadata, stripped UPER bytes, and original bytes used for signature detection
   * @throws JsonProcessingException if the raw JSON or metadata is malformed
   * @throws StartFlagNotFoundException if the message start flag cannot be located
   */
  public FfmRawRecord parseFfmRecord(String json, SupportedMessageType messageType)
      throws JsonProcessingException, StartFlagNotFoundException {
    JsonNode root = mapper.readTree(json);
    JsonNode payloadBytes = root.path("payload").path("data").path("bytes");
    if (!payloadBytes.isTextual() || payloadBytes.textValue().isBlank()) {
      throw new IllegalArgumentException("Raw record has no original ASN.1 payload bytes");
    }
    JsonNode metadataNode = root.get("metadata");
    if (metadataNode == null || !metadataNode.isObject()) {
      throw new IllegalArgumentException("Raw record has no metadata object");
    }

    OdeMessageFrameMetadata metadata = mapper.treeToValue(metadataNode,
        OdeMessageFrameMetadata.class);
    metadata.addEncoding(new Asn1Encoding("unsecuredData", "MessageFrame", EncodingRule.UPER));

    byte[] packetBytes = CodecUtils.fromHex(payloadBytes.textValue());
    byte[] originalBytes = metadata.getAsn1() == null || metadata.getAsn1().isBlank()
        ? packetBytes
        : CodecUtils.fromHex(metadata.getAsn1());
    byte[] uperBytes = UperUtil.stripDot2Header(packetBytes, messageType.getStartFlagBytes());
    return new FfmRawRecord(metadata, uperBytes, originalBytes);
  }

  /** Raw UDP input parsed for the FFMLib listener. */
  public record FfmRawRecord(OdeMessageFrameMetadata metadata, byte[] uperBytes,
      byte[] originalBytes) {
  }

  /**
   * Continues a raw encoded message toward a decoded JSON topic.
   *
   * <p>The external codec consumes the forwarded ASN.1 record. FFM mode decodes UDP in process and
   * does not consume this topic.
   *
   * @param data prepared ASN.1 record
   * @param key Kafka record key to preserve
   * @param externalDecoderTemplate producer used only for the external decoder topic
   * @param decoderInputTopic external decoder input topic
   */
  public void publish(
      OdeAsn1Data data,
      String key,
      KafkaTemplate<String, OdeObject> externalDecoderTemplate,
      String decoderInputTopic) {
    externalDecoderTemplate.send(decoderInputTopic, key, data);
  }

}

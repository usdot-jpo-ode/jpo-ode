package us.dot.its.jpo.ode.kafka.listeners.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
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
  private final ObjectReader ffmMetadataReader;

  /**
   * Creates the raw-encoded JSON service.
   *
   * @param mapper JSON mapper used to read message metadata
   */
  public RawEncodedJsonService(ObjectMapper mapper) {
    this.mapper = mapper;
    this.ffmMetadataReader = mapper.readerFor(OdeMessageFrameMetadata.class);
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
   * @throws IOException if the raw JSON or metadata is malformed
   * @throws StartFlagNotFoundException if the message start flag cannot be located
   */
  public FfmRawRecord parseFfmRecord(String json, SupportedMessageType messageType)
      throws IOException, StartFlagNotFoundException {
    JsonNode metadataNode = null;
    String payloadHex = null;
    try (JsonParser parser = mapper.createParser(json)) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        throw new IllegalArgumentException("Raw record must be a JSON object");
      }
      while (parser.nextToken() != JsonToken.END_OBJECT) {
        if (parser.currentToken() != JsonToken.FIELD_NAME) {
          throw new IllegalArgumentException("Raw record has an invalid JSON object");
        }
        String field = parser.currentName();
        JsonToken value = parser.nextToken();
        if ("metadata".equals(field)) {
          metadataNode = parser.readValueAsTree();
        } else if ("payload".equals(field)) {
          payloadHex = readPayloadHex(parser, value);
        } else {
          parser.skipChildren();
        }
      }
    }
    if (payloadHex == null || payloadHex.isBlank()) {
      throw new IllegalArgumentException("Raw record has no original ASN.1 payload bytes");
    }
    if (metadataNode == null || !metadataNode.isObject()) {
      throw new IllegalArgumentException("Raw record has no metadata object");
    }
    OdeMessageFrameMetadata metadata = ffmMetadataReader.readValue(mapper.treeAsTokens(metadataNode));

    metadata.addEncoding(new Asn1Encoding("unsecuredData", "MessageFrame", EncodingRule.UPER));

    byte[] packetBytes = CodecUtils.fromHex(payloadHex);
    byte[] originalBytes = metadata.getAsn1() == null || metadata.getAsn1().isBlank()
        ? packetBytes
        : CodecUtils.fromHex(metadata.getAsn1());
    byte[] uperBytes = UperUtil.stripDot2Header(packetBytes, messageType.getStartFlagBytes());
    return new FfmRawRecord(metadata, uperBytes, originalBytes);
  }

  /**
   * Parses a raw record for in-process decoding without materializing original bytes or adding a
   * synthesized encoding to its metadata on the usual single-metadata path.
   *
   * <p>Repeated metadata fields and metadata mapping failures use the legacy parser to preserve
   * its last-field-wins behavior and error classification. That compatibility fallback retains
   * the legacy allocation and encoding behavior.
   *
   * @param json raw-topic JSON value
   * @param messageType message type used to locate the UPER start flag
   * @return metadata and stripped UPER bytes
   * @throws IOException if the raw JSON or metadata is malformed
   * @throws StartFlagNotFoundException if the message start flag cannot be located
   */
  public FfmDecodeInput parseFfmDecodeInput(String json, SupportedMessageType messageType)
      throws IOException, StartFlagNotFoundException {
    DecodeScan scan = scanFfmDecodeInput(json);
    if (scan.requiresLegacyParse) {
      return toDecodeInput(parseFfmRecord(json, messageType));
    }
    if (scan.payloadHex == null || scan.payloadHex.isBlank()) {
      throw new IllegalArgumentException("Raw record has no original ASN.1 payload bytes");
    }
    if (scan.metadata == null) {
      throw new IllegalArgumentException("Raw record has no metadata object");
    }

    // Keep payload decode and metadata.asn1 validation ahead of UPER header stripping, matching
    // the legacy parser's order while avoiding an unused decoded copy of metadata.asn1.
    byte[] packetBytes = CodecUtils.fromHex(scan.payloadHex);
    validateMetadataAsn1(scan.metadata.getAsn1());
    byte[] uperBytes = UperUtil.stripDot2Header(packetBytes, messageType.getStartFlagBytes());
    return new FfmDecodeInput(scan.metadata, uperBytes);
  }

  private DecodeScan scanFfmDecodeInput(String json) throws IOException {
    DecodeScan scan = new DecodeScan();
    try (JsonParser parser = mapper.createParser(json)) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        throw new IllegalArgumentException("Raw record must be a JSON object");
      }
      JsonToken token;
      while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {
        if (token != JsonToken.FIELD_NAME) {
          throw new IllegalArgumentException("Raw record has an invalid JSON object");
        }
        if (readFfmDecodeField(parser, scan)) {
          break;
        }
      }
    }
    return scan;
  }

  private boolean readFfmDecodeField(JsonParser parser, DecodeScan scan) throws IOException {
    String field = parser.currentName();
    JsonToken value = parser.nextToken();
    if (value == null) {
      throw new IllegalArgumentException("Raw record has an invalid JSON object");
    }
    if ("metadata".equals(field)) {
      return readFfmDecodeMetadata(parser, value, scan);
    }
    if ("payload".equals(field)) {
      scan.payloadHex = readPayloadHex(parser, value);
    } else {
      parser.skipChildren();
    }
    return false;
  }

  private boolean readFfmDecodeMetadata(JsonParser parser, JsonToken value, DecodeScan scan)
      throws IOException {
    if (scan.metadataSeen) {
      // The legacy parser applies last-field-wins semantics to metadata. Stop scanning and
      // let it parse the complete record after this parser has closed.
      scan.requiresLegacyParse = true;
      return true;
    }
    scan.metadataSeen = true;
    if (value != JsonToken.START_OBJECT) {
      // Keep scanning so a later metadata field can replace this one under the legacy
      // last-field-wins behavior. If this is the final value, reject it after the scan.
      parser.skipChildren();
      return false;
    }
    try {
      scan.metadata = ffmMetadataReader.readValue(parser);
    } catch (IOException | RuntimeException error) {
      // The legacy parser deserializes only the final metadata field. Use it when eager
      // mapping could change that result or the legacy parse-error category.
      scan.requiresLegacyParse = true;
      return true;
    }
    return false;
  }

  private static final class DecodeScan {
    private OdeMessageFrameMetadata metadata;
    private String payloadHex;
    private boolean metadataSeen;
    private boolean requiresLegacyParse;
  }

  private static FfmDecodeInput toDecodeInput(FfmRawRecord raw) {
    return new FfmDecodeInput(raw.metadata(), raw.uperBytes());
  }

  private static void validateMetadataAsn1(String asn1) {
    if (asn1 == null || asn1.isBlank()) {
      return;
    }
    if ((asn1.length() & 1) != 0) {
      throw new IllegalArgumentException("Metadata ASN.1 hex must contain complete bytes");
    }
    for (int i = 0; i < asn1.length(); i++) {
      char digit = asn1.charAt(i);
      if (!((digit >= '0' && digit <= '9') || (digit >= 'a' && digit <= 'f')
          || (digit >= 'A' && digit <= 'F'))) {
        throw new IllegalArgumentException("Metadata ASN.1 must contain ASCII hexadecimal data");
      }
    }
  }

  private static String readPayloadHex(JsonParser parser, JsonToken payloadToken)
      throws IOException {
    if (payloadToken != JsonToken.START_OBJECT) {
      parser.skipChildren();
      return null;
    }
    String payloadHex = null;
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      if (parser.currentToken() != JsonToken.FIELD_NAME) {
        throw new IllegalArgumentException("Raw payload has an invalid JSON object");
      }
      String field = parser.currentName();
      JsonToken value = parser.nextToken();
      if ("data".equals(field)) {
        payloadHex = readPayloadBytes(parser, value);
      } else {
        parser.skipChildren();
      }
    }
    return payloadHex;
  }

  private static String readPayloadBytes(JsonParser parser, JsonToken dataToken)
      throws IOException {
    if (dataToken != JsonToken.START_OBJECT) {
      parser.skipChildren();
      return null;
    }
    String payloadHex = null;
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      if (parser.currentToken() != JsonToken.FIELD_NAME) {
        throw new IllegalArgumentException("Raw payload data has an invalid JSON object");
      }
      String field = parser.currentName();
      JsonToken value = parser.nextToken();
      if ("bytes".equals(field)) {
        payloadHex = value == JsonToken.VALUE_STRING ? parser.getText() : null;
      } else {
        parser.skipChildren();
      }
    }
    return payloadHex;
  }

  /** Raw UDP input parsed for the FFMLib listener. */
  public record FfmRawRecord(OdeMessageFrameMetadata metadata, byte[] uperBytes,
      byte[] originalBytes) {
  }

  /** Raw UDP input required by the FFMLib decoder's normal path. */
  public record FfmDecodeInput(OdeMessageFrameMetadata metadata, byte[] uperBytes) {
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

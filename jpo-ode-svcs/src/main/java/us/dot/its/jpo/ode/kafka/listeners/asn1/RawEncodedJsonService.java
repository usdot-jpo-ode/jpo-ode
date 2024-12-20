package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;

/**
 * Service class responsible for processing raw ASN.1 encoded JSON data, applying specific
 * encodings, and mutating the payload bytes to comply with desired formats.
 */
@Service
public class RawEncodedJsonService {

  private final ObjectMapper mapper;

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

}

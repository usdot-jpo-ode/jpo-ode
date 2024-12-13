package us.dot.its.jpo.ode.kafka.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;

/**
 * The Asn1DecodeMAPJSONListener class is a component designed to listen to messages from a
 * specified Kafka topic, decode the ASN.1 encoded data from received JSON, and publish the decoded
 * data to another Kafka topic. This class utilizes the @KafkaListener and @KafkaHandler annotations
 * to process incoming Kafka messages.
 */
@Slf4j
@Component
@KafkaListener(id = "Asn1DecodeMAPJSONListener", topics = "${ode.kafka.topics.raw-encoded-json.map}")
public class Asn1DecodeMAPJSONListener {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final String publishTopic;
  private final KafkaTemplate<String, OdeObject> kafkaTemplate;

  public Asn1DecodeMAPJSONListener(KafkaTemplate<String, OdeObject> kafkaTemplate,
      @Value("${ode.kafka.topics.asn1.decoder-input}") String publishTopic) {
    this.kafkaTemplate = kafkaTemplate;
    this.publishTopic = publishTopic;
  }

  /**
   * Processes consumed Kafka messages that contain ASN.1 encoded data within JSON format.
   * This method decodes the message, extracts metadata and payload, and then publishes
   * the decoded data to a specified Kafka topic.
   *
   * @param consumedData The raw JSON string consumed from the Kafka topic. This string
   *                     is expected to contain ASN.1 encoded data that needs processing
   *                     and further publication.
   * @throws JsonProcessingException If there is an error in processing the JSON string.
   * @throws StartFlagNotFoundException If the start flag is not found in the payload
   *                                    during header stripping.
   */
  @KafkaHandler
  public void listen(String consumedData)
      throws JsonProcessingException, StartFlagNotFoundException {
    log.debug("consumedData: {}", consumedData);
    JSONObject rawMapJsonObject = new JSONObject(consumedData);

    String jsonStringMetadata = rawMapJsonObject.get("metadata").toString();
    OdeMapMetadata metadata = objectMapper.readValue(jsonStringMetadata, OdeMapMetadata.class);

    Asn1Encoding unsecuredDataEncoding =
        new Asn1Encoding("unsecuredData", "MessageFrame", Asn1Encoding.EncodingRule.UPER);
    metadata.addEncoding(unsecuredDataEncoding);

    String payloadHexString =
        ((JSONObject) ((JSONObject) rawMapJsonObject.get("payload")).get("data")).getString(
            "bytes");
    payloadHexString =
        UperUtil.stripDot2Header(payloadHexString, SupportedMessageType.MAP.getStartFlag());

    OdeAsn1Payload payload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));

    OdeAsn1Data data = new OdeAsn1Data(metadata, payload);
    kafkaTemplate.send(publishTopic, data);
  }
}

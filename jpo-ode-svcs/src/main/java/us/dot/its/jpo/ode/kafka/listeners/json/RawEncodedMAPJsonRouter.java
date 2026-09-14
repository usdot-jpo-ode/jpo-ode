package us.dot.its.jpo.ode.kafka.listeners.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * A Kafka listener component that processes ASN.1 encoded MAP JSON messages from a specified Kafka
 * topic. It processes the raw encoded JSON messages and publishes them to be decoded by the ASN.1
 * codec
 */
@Slf4j
@Component
public class RawEncodedMAPJsonRouter {

  private final FfmlibDecodeService decodeService;
  private final RawEncodedJsonService rawEncodedJsonService;

  /**
   * Constructor for the RawEncodedMAPJsonRouter class.
   *
   * @param decodeService         In-process FFMLib decode service that publishes decoded JSON
   *                              or decodes in-process via FFMLib, depending on configuration.
   * @param rawEncodedJsonService A service to transform incoming data into the expected output
   */
  public RawEncodedMAPJsonRouter(FfmlibDecodeService decodeService,
      RawEncodedJsonService rawEncodedJsonService) {
    this.decodeService = decodeService;
    this.rawEncodedJsonService = rawEncodedJsonService;
  }

  /**
   * Consumes and processes Kafka messages containing ASN.1 encoded MAP JSON data. This method
   * extracts metadata and payload from the JSON message sends it for decoding.
   *
   * @param consumerRecord The Kafka consumer record containing the message key and value. The value
   *                       includes the raw ASN.1 encoded JSON MAP data to be processed.
   * @throws StartFlagNotFoundException If the start flag for the MAP message type is not found
   *                                    during payload processing.
   * @throws JsonProcessingException    If there's an error while processing or deserializing JSON
   *                                    data.
   */
  @KafkaListener(id = "RawEncodedMAPJsonRouter", topics = "${ode.kafka.topics.raw-encoded-json.map}")
  public void listen(ConsumerRecord<String, String> consumerRecord)
      throws JsonProcessingException, StartFlagNotFoundException {
    var messageToPublish = rawEncodedJsonService.addEncodingAndMutateBytes(
        consumerRecord.value(),
        SupportedMessageType.MAP, OdeMessageFrameMetadata.class);
    decodeService.decode(messageToPublish, consumerRecord.key());
  }
}

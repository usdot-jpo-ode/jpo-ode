package us.dot.its.jpo.ode.kafka.listeners.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * Kafka listener for raw-encoded BSM JSON messages arriving via the log-file import path.
 * Decodes in-process via {@link FfmlibDecodeService} and publishes to {@code topic.OdeBsmJson}.
 */
@Component
public class RawEncodedBSMJsonRouter {

  private final FfmlibDecodeService decodeService;
  private final RawEncodedJsonService rawEncodedJsonService;

  public RawEncodedBSMJsonRouter(FfmlibDecodeService decodeService,
      RawEncodedJsonService rawEncodedJsonService) {
    this.decodeService = decodeService;
    this.rawEncodedJsonService = rawEncodedJsonService;
  }

  @KafkaListener(id = "RawEncodedBSMJsonRouter", topics = "${ode.kafka.topics.raw-encoded-json.bsm}")
  public void listen(ConsumerRecord<String, String> consumerRecord)
      throws StartFlagNotFoundException, JsonProcessingException {
    var messageToPublish =
        rawEncodedJsonService.addEncodingAndMutateBytes(consumerRecord.value(),
            SupportedMessageType.BSM,
            OdeMessageFrameMetadata.class);
    decodeService.decode(messageToPublish, consumerRecord.key());
  }
}

package us.dot.its.jpo.ode.codec.ffmlib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/**
 * Warms producer metadata for direct UDP routing before UDP sockets open. A missing topic fails
 * startup, so receivers never start publishing to an unprovisioned topic.
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Conditional(FfmlibUdpRoutingConditions.Direct.class)
public class FfmlibDirectStartup implements ApplicationRunner {

  private final RawEncodedJsonTopics rawTopics;
  private final JsonTopics jsonTopics;
  private final KafkaTemplate<String, String> rawProducer;
  private final KafkaTemplate<String, String> outputProducer;
  private final boolean rawPublish;

  /**
   * Creates the direct-routing startup check.
   *
   * @param rawTopics configured raw message topics, which also name the dead-letter topics
   * @param jsonTopics configured decoded JSON topics
   * @param rawProducer UDP raw-topic producer
   * @param outputProducer decoded JSON and quarantine producer
   * @param ffmlibProperties whether raw topics are published
   */
  public FfmlibDirectStartup(RawEncodedJsonTopics rawTopics, JsonTopics jsonTopics,
      @Qualifier("ffmlibRawKafkaTemplate") KafkaTemplate<String, String> rawProducer,
      @Qualifier("ffmlibOutputKafkaTemplate") KafkaTemplate<String, String> outputProducer,
      FfmlibProperties ffmlibProperties) {
    this.rawTopics = rawTopics;
    this.jsonTopics = jsonTopics;
    this.rawProducer = rawProducer;
    this.outputProducer = outputProducer;
    this.rawPublish = ffmlibProperties.isRawPublish();
  }

  @Override
  public void run(ApplicationArguments args) {
    if (rawPublish) {
      FfmlibTopicWarmup.warmProducerMetadata(rawProducer, rawTopics.allTopics());
    }
    FfmlibTopicWarmup.warmProducerMetadata(outputProducer,
        FfmlibTopicWarmup.outputTopics(jsonTopics, rawTopics));
    log.info("FFM direct UDP routing ready; raw-topic publication {}",
        rawPublish ? "enabled" : "disabled");
  }
}

package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/** Startup checks shared by the FFM UDP routes before UDP sockets open. */
final class FfmlibTopicWarmup {

  private FfmlibTopicWarmup() {
  }

  /**
   * Loads producer metadata for each topic so the first UDP send does not block on it.
   *
   * @throws IllegalStateException when a topic has no partitions, usually because it was not
   *     provisioned
   */
  static void warmProducerMetadata(KafkaTemplate<String, String> producer,
      List<String> topicNames) {
    for (String topicName : topicNames) {
      List<PartitionInfo> partitions = producer.partitionsFor(topicName);
      if (partitions == null || partitions.isEmpty()) {
        throw new IllegalStateException("No producer metadata available for FFM topic "
            + topicName);
      }
    }
  }

  /** Decoded JSON topics for every UDP message type, followed by their dead-letter topics. */
  static List<String> outputTopics(JsonTopics jsonTopics, RawEncodedJsonTopics rawTopics) {
    List<String> topics = List.of(jsonTopics.getBsm(), jsonTopics.getMap(), jsonTopics.getPsm(),
        jsonTopics.getSpat(), jsonTopics.getSrm(), jsonTopics.getSsm(), jsonTopics.getTim(),
        jsonTopics.getSdsm(), jsonTopics.getRtcm(), jsonTopics.getRsm());
    List<String> withDeadLetters = new ArrayList<>(topics);
    for (String rawTopic : rawTopics.allTopics()) {
      withDeadLetters.add(FfmlibDeadLetterTopics.forRawTopic(rawTopic));
    }
    return withDeadLetters;
  }

  /**
   * Rejects raw-topic routing when a raw topic it decodes from is disabled.
   *
   * @throws IllegalStateException naming the disabled raw topics
   */
  static void requireRawTopicsEnabled(Collection<String> rawTopics, Set<String> disabledTopics) {
    if (disabledTopics == null || disabledTopics.isEmpty()) {
      return;
    }
    List<String> disabled = rawTopics.stream().filter(disabledTopics::contains).toList();
    if (!disabled.isEmpty()) {
      throw new IllegalStateException("FFM raw-topic routing decodes from " + disabled
          + "; remove them from ode.kafka.disabled-topics or use ode.ffmlib.udp-routing=direct");
    }
  }
}

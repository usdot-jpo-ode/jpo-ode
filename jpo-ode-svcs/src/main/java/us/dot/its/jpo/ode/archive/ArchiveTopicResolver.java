package us.dot.its.jpo.ode.archive;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * Maps the source topic of an archive file to the way its records are imported, using the topic
 * names configured for this ODE.
 */
@Component
public class ArchiveTopicResolver {

  /**
   * How the records of an archived topic are imported.
   */
  public enum TopicKind {
    /** A raw encoded topic, {@code topic.OdeRawEncoded<TYPE>Json}. */
    RAW_ENCODED,
    /** A decoded message topic, {@code topic.Ode<Type>Json}, which also has compact archives. */
    DECODED_MESSAGE,
    /** Another ODE output topic, such as driver alerts or the PPM filtered topics. */
    DECODED_OTHER
  }

  /**
   * The resolved source topic of an archive file.
   *
   * @param kind        how its records are imported
   * @param topic       the topic name
   * @param messageType the message type, or null for {@link TopicKind#DECODED_OTHER}
   */
  public record TopicTarget(TopicKind kind, String topic, SupportedMessageType messageType) {
  }

  private final Map<String, TopicTarget> targets = new HashMap<>();
  private final Map<SupportedMessageType, String> rawEncodedTopics =
      new EnumMap<>(SupportedMessageType.class);
  private final Map<SupportedMessageType, String> decodedTopics =
      new EnumMap<>(SupportedMessageType.class);

  /**
   * Builds the topic map from the configured topic names.
   *
   * @param jsonTopics           the decoded topics
   * @param rawEncodedJsonTopics the raw encoded topics
   */
  public ArchiveTopicResolver(JsonTopics jsonTopics, RawEncodedJsonTopics rawEncodedJsonTopics) {
    rawEncodedTopics.put(SupportedMessageType.BSM, rawEncodedJsonTopics.getBsm());
    rawEncodedTopics.put(SupportedMessageType.MAP, rawEncodedJsonTopics.getMap());
    rawEncodedTopics.put(SupportedMessageType.PSM, rawEncodedJsonTopics.getPsm());
    rawEncodedTopics.put(SupportedMessageType.SPAT, rawEncodedJsonTopics.getSpat());
    rawEncodedTopics.put(SupportedMessageType.SRM, rawEncodedJsonTopics.getSrm());
    rawEncodedTopics.put(SupportedMessageType.SSM, rawEncodedJsonTopics.getSsm());
    rawEncodedTopics.put(SupportedMessageType.TIM, rawEncodedJsonTopics.getTim());
    rawEncodedTopics.put(SupportedMessageType.SDSM, rawEncodedJsonTopics.getSdsm());
    rawEncodedTopics.put(SupportedMessageType.RTCM, rawEncodedJsonTopics.getRtcm());
    rawEncodedTopics.put(SupportedMessageType.RSM, rawEncodedJsonTopics.getRsm());

    decodedTopics.put(SupportedMessageType.BSM, jsonTopics.getBsm());
    decodedTopics.put(SupportedMessageType.MAP, jsonTopics.getMap());
    decodedTopics.put(SupportedMessageType.PSM, jsonTopics.getPsm());
    decodedTopics.put(SupportedMessageType.SPAT, jsonTopics.getSpat());
    decodedTopics.put(SupportedMessageType.SRM, jsonTopics.getSrm());
    decodedTopics.put(SupportedMessageType.SSM, jsonTopics.getSsm());
    decodedTopics.put(SupportedMessageType.TIM, jsonTopics.getTim());
    decodedTopics.put(SupportedMessageType.SDSM, jsonTopics.getSdsm());
    decodedTopics.put(SupportedMessageType.RTCM, jsonTopics.getRtcm());
    decodedTopics.put(SupportedMessageType.RSM, jsonTopics.getRsm());

    for (String topic : new String[] {jsonTopics.getDriverAlert(), jsonTopics.getBsmFiltered(),
        jsonTopics.getSpatFiltered(), jsonTopics.getTimFiltered(), jsonTopics.getTimTmcFiltered(),
        jsonTopics.getTimCertExpiration()}) {
      if (topic != null) {
        targets.put(topic, new TopicTarget(TopicKind.DECODED_OTHER, topic, null));
      }
    }
    rawEncodedTopics.forEach((type, topic) -> {
      if (topic != null) {
        targets.put(topic, new TopicTarget(TopicKind.RAW_ENCODED, topic, type));
      }
    });
    decodedTopics.forEach((type, topic) -> {
      if (topic != null) {
        targets.put(topic, new TopicTarget(TopicKind.DECODED_MESSAGE, topic, type));
      }
    });
  }

  /**
   * Resolves an archived topic name.
   *
   * @param topic the topic name from the archive file name
   * @return the target, or empty if records of this topic cannot be imported
   */
  public Optional<TopicTarget> resolve(String topic) {
    return Optional.ofNullable(targets.get(topic));
  }

  /**
   * Returns the raw encoded topic the ODE decodes messages of the given type from.
   *
   * @param messageType the message type
   * @return the raw encoded topic name
   */
  public String rawEncodedTopic(SupportedMessageType messageType) {
    return rawEncodedTopics.get(messageType);
  }
}

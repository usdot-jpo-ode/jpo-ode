package us.dot.its.jpo.ode.test.utilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

/**
 * The EmbeddedKafkaHolder class is a utility for managing a singleton instance of an embedded Kafka
 * broker for testing purposes. It ensures that the embedded Kafka broker is started only once
 * throughout the application lifecycle.
 *
 * <p>This class initializes an embedded Kafka broker with a specified configuration, which
 * includes a single broker and auto-start enabled. The broker list property is set to
 * "spring.kafka.bootstrap-servers".
 *
 * </p>The embedded Kafka instance is lazily started when the getEmbeddedKafka method is called
 * for the first time. If the broker fails to start, a KafkaException is thrown.
 *
 * </p>The class is designed to be non-instantiable with a private constructor.
 */
@Slf4j
public final class EmbeddedKafkaHolder {

  private static final EmbeddedKafkaBroker embeddedKafka = new EmbeddedKafkaBroker(1,
      true,
      1)
      .brokerListProperty("spring.kafka.bootstrap-servers");

  private static boolean started;

  /**
   * Provides access to a singleton instance of an embedded Kafka broker for testing purposes.
   * Lazily initializes and starts the broker on the first call with a predefined configuration. If
   * the broker fails to start, a KafkaException is thrown.
   *
   * @return the singleton instance of the embedded Kafka broker
   * @throws KafkaException if the embedded broker fails to start
   */
  public static EmbeddedKafkaBroker getEmbeddedKafka() {
    if (!started) {
      try {
        embeddedKafka.kafkaPorts(4242);
        embeddedKafka.afterPropertiesSet();
      } catch (Exception e) {
        throw new KafkaException("Embedded broker failed to start", e);
      }
      started = true;
    }
    return embeddedKafka;
  }

  /**
   * Adds one or more topics to the embedded Kafka broker instance. Each topic will be created
   * with a replication factor of 1 and a single partition. If a topic already exists, no action
   * is taken, and the exception is ignored.
   *
   * @param topics one or more topic names to be added to the embedded Kafka broker
   */
  public static void addTopics(String... topics) {
    var existingTopics = embeddedKafka.getTopics();
    for (String topic : topics) {
      if (existingTopics.contains(topic)) {
        log.debug("topic {} already exists in embedded kafka broker. Skipping creation", topic);
        continue;
      }
      NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
      try {
        embeddedKafka.addTopics(newTopic);
      } catch (Exception e) {
        // Ignore because we only care they are created not that they weren't created prior
        log.debug("exception adding topic {} to embedded kafka broker", topic, e);
      }
    }
  }

  private EmbeddedKafkaHolder() {
    super();
  }
}

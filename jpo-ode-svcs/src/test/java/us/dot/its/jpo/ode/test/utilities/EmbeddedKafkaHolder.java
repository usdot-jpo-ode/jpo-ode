package us.dot.its.jpo.ode.test.utilities;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

/**
 * The EmbeddedKafkaHolder class is a utility for managing a singleton instance of an embedded Kafka
 * broker for testing purposes. It ensures that the embedded Kafka broker is started only once
 * throughout the application lifecycle.
 *
 * <p>This class initializes an embedded Kafka broker with a specified configuration, which
 * includes
 * a single broker and auto-start enabled. The broker list property is set to
 * "spring.kafka.bootstrap-servers".
 *
 * </p>The embedded Kafka instance is lazily started when the getEmbeddedKafka method is called
 * for the first time. If the broker fails to start, a KafkaException is thrown.
 *
 * </p>The class is designed to be non-instantiable with a private constructor.
 */
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

  private EmbeddedKafkaHolder() {
    super();
  }
}

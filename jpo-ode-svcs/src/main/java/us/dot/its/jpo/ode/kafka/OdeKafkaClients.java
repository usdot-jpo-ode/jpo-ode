package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

/**
 * Shared Kafka client settings for ODE producers and consumers.
 *
 * <p>FFM decode uses these maps so its clients follow the same brokers, Confluent credentials,
 * and producer delivery settings as the rest of the application.
 */
public final class OdeKafkaClients {

  private OdeKafkaClients() {
  }

  /** Consumer settings from Spring Kafka plus Confluent credentials when that cluster is selected. */
  public static Map<String, Object> consumerProperties(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties) {
    Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());
    applyConfluent(properties, odeKafkaProperties);
    return properties;
  }

  /** Admin client settings with the same bootstrap and Confluent authentication as ODE clients. */
  public static Map<String, Object> adminProperties(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties) {
    Map<String, Object> properties = new HashMap<>(kafkaProperties.buildAdminProperties());
    String brokers = odeKafkaProperties.getBrokers();
    if (brokers != null && !brokers.isBlank()) {
      properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    }
    applyConfluent(properties, odeKafkaProperties);
    return properties;
  }

  /**
   * Producer settings from Spring Kafka, plus the ODE delivery settings in
   * {@code ode.kafka.producer}.
   *
   * <p>{@code ode.kafka.producer} is the single source for acks, batching, buffering, linger,
   * retries, and compression on every ODE producer. Serializers and partitioners are chosen by
   * each producer factory.
   */
  public static Map<String, Object> producerProperties(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties) {
    Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
    applyConfluent(properties, odeKafkaProperties);
    OdeKafkaProperties.Producer producer = odeKafkaProperties.getProducer();
    if (producer != null) {
      putIfPresent(properties, ProducerConfig.ACKS_CONFIG, producer.getAcks());
      putIfPresent(properties, ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
      putIfPresent(properties, ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());
      putIfPresent(properties, ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
      putIfPresent(properties, ProducerConfig.RETRIES_CONFIG, producer.getRetries());
      putIfPresent(properties, ProducerConfig.COMPRESSION_TYPE_CONFIG,
          producer.getCompressionType());
    }
    return properties;
  }

  private static void putIfPresent(Map<String, Object> properties, String key, Object value) {
    if (value != null && !(value instanceof String text && text.isBlank())) {
      properties.put(key, value);
    }
  }

  private static void applyConfluent(Map<String, Object> properties,
      OdeKafkaProperties odeKafkaProperties) {
    if ("CONFLUENT".equals(odeKafkaProperties.getKafkaType())) {
      properties.putAll(odeKafkaProperties.getConfluent().buildConfluentProperties());
    }
  }
}

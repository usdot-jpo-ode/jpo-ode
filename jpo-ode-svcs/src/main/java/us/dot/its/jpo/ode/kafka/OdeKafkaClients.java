package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

/**
 * Shared Kafka client settings for ODE producers and consumers.
 *
 * <p>FFM decode uses these maps so its clients follow the same brokers, Confluent credentials,
 * linger, and retries as the rest of the application.
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

  /**
   * Producer settings from Spring Kafka, plus ODE linger and retries.
   *
   * <p>Linger is applied here because it is not part of {@link KafkaProperties} and the Kafka
   * default would otherwise wait until the batch fills.
   */
  public static Map<String, Object> producerProperties(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties) {
    Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
    applyConfluent(properties, odeKafkaProperties);
    OdeKafkaProperties.Producer producer = odeKafkaProperties.getProducer();
    if (producer != null) {
      properties.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
      properties.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
    }
    return properties;
  }

  private static void applyConfluent(Map<String, Object> properties,
      OdeKafkaProperties odeKafkaProperties) {
    if ("CONFLUENT".equals(odeKafkaProperties.getKafkaType())) {
      properties.putAll(odeKafkaProperties.getConfluent().buildConfluentProperties());
    }
  }
}

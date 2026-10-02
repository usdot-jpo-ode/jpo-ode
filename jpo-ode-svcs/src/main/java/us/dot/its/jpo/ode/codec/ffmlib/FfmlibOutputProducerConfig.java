package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import us.dot.its.jpo.ode.kafka.OdeKafkaClients;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

/**
 * A separate producer keeps UDP raw sends from filling the FFM output queue. Client settings match
 * the shared ODE producer.
 */
@Configuration
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibOutputProducerConfig {

  /**
   * Creates the FFM output producer from the shared ODE Kafka settings.
   *
   * @param kafkaProperties Spring Kafka producer settings
   * @param odeKafkaProperties ODE linger, retries, and Confluent credentials
   * @return producer factory for Ode JSON strings
   */
  @Bean("ffmlibOutputProducerFactory")
  public ProducerFactory<String, String> ffmlibOutputProducerFactory(
      KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties,
      FfmlibProperties ffmlibProperties) {
    Map<String, Object> config = new HashMap<>(
        OdeKafkaClients.producerProperties(kafkaProperties, odeKafkaProperties));
    config.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configureFfmProducer(config, ffmlibProperties);
    return new DefaultKafkaProducerFactory<>(config);
  }

  /** Creates a dedicated producer for durable raw-topic UDP ingestion. */
  @Bean("ffmlibRawProducerFactory")
  public ProducerFactory<String, String> ffmlibRawProducerFactory(
      KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties,
      FfmlibProperties ffmlibProperties) {
    Map<String, Object> config = new HashMap<>(
        OdeKafkaClients.producerProperties(kafkaProperties, odeKafkaProperties));
    config.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configureFfmProducer(config, ffmlibProperties);
    if ("round_robin".equalsIgnoreCase(ffmlibProperties.getRawPartitionStrategy())) {
      config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, FfmlibRawTopicPartitioner.class);
    }
    return new DefaultKafkaProducerFactory<>(config);
  }

  /** Creates the raw input template with no disabled-topic interception. */
  @Bean("ffmlibRawKafkaTemplate")
  public KafkaTemplate<String, String> ffmlibRawKafkaTemplate(
      @Qualifier("ffmlibRawProducerFactory") ProducerFactory<String, String> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  /**
   * Creates the template used for FFM JSON and dead-letter sends.
   *
   * @param producerFactory FFM output producer
   * @return Kafka template for string payloads
   */
  @Bean("ffmlibOutputKafkaTemplate")
  public KafkaTemplate<String, String> ffmlibOutputKafkaTemplate(
      @Qualifier("ffmlibOutputProducerFactory") ProducerFactory<String, String> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  private static void configureFfmProducer(Map<String, Object> config,
      FfmlibProperties properties) {
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    config.put(ProducerConfig.RETRIES_CONFIG,
        Math.max(2, Integer.parseInt(config.getOrDefault(ProducerConfig.RETRIES_CONFIG, 0)
            .toString())));
    config.put(ProducerConfig.LINGER_MS_CONFIG, properties.getProducerLingerMs());
    config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, properties.getProducerCompressionType());
  }
}

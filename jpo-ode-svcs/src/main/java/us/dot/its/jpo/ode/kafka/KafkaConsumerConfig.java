package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * Configures Kafka consumer settings and provides various consumer factories and listener container
 * factories for handling different types of data in a Kafka messaging system. This class sets up
 * the consumer properties, including any security configurations for Confluent deployments, and
 * provides mechanisms to filter messages based on specific criteria.
 */
@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;
  private final OdeKafkaProperties odeKafkaProperties;

  /**
   * Constructs a new instance of KafkaConsumerConfig with the specified Kafka properties.
   *
   * @param kafkaProperties    The general Kafka properties used for configuring consumer settings.
   * @param odeKafkaProperties The specific Ode Kafka properties which may include custom
   *                           configurations relevant to the Ode system, possibly including
   *                           brokers and other kafka-specific settings.
   */
  public KafkaConsumerConfig(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties) {
    this.kafkaProperties = kafkaProperties;
    this.odeKafkaProperties = odeKafkaProperties;
  }

  /**
   * Creates and configures a {@link ConsumerFactory} for Kafka consumers with String key and
   * value deserialization. The factory is configured using Kafka consumer properties defined
   * in the application configuration.
   *
   * @return a {@link ConsumerFactory} instance configured to produce Kafka consumers with
   *         String key and value serialization.
   */
  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(getKafkaConsumerProperties());
  }

  /**
   * Creates and configures a ConcurrentKafkaListenerContainerFactory for Kafka listeners. This
   * factory is responsible for creating listener containers, which are used to receive messages
   * from Kafka topics.
   *
   * @return a ConcurrentKafkaListenerContainerFactory setup with a defined consumer factory that
   *      determines how Kafka consumers are created and configured.
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  private Map<String, Object> getKafkaConsumerProperties() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    if ("CONFLUENT".equals(this.odeKafkaProperties.getKafkaType())) {
      props.putAll(this.odeKafkaProperties.getConfluent().buildConfluentProperties());
    }
    return props;
  }
}
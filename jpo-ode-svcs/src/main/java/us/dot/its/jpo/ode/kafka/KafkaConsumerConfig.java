package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import us.dot.its.jpo.ode.model.OdeMapData;

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

  /**
   * Creates a Kafka {@link ConsumerFactory} for consuming messages with keys of type {@link String}
   * and values of type {@link OdeMapData}. This method utilizes a {@link StringDeserializer} for
   * deserializing the key and a {@link JsonDeserializer} for deserializing values of type
   * {@link OdeMapData}.
   *
   * <p>The consumer factory is configured using Kafka properties, which are retrieved from the
   * application's configuration settings.
   *
   * @return a configured {@link ConsumerFactory} for {@link String} keys and {@link OdeMapData}
   *      values.
   */
  @Bean
  public ConsumerFactory<String, OdeMapData> odeMapDataConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(getKafkaConsumerProperties(), new StringDeserializer(),
        new JsonDeserializer<>(OdeMapData.class));
  }

  /**
   * Creates and configures a ConcurrentKafkaListenerContainerFactory for consuming Kafka messages
   * with keys of type String and values of type OdeMapData. The factory is configured with a
   * consumer factory provided by the odeMapDataConsumerFactory method.
   *
   * @return a configured ConcurrentKafkaListenerContainerFactory instance for processing Kafka
   *      messages with keys of type String and values of type OdeMapData.
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, OdeMapData> odeMapDataConsumerListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, OdeMapData> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(odeMapDataConsumerFactory());
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

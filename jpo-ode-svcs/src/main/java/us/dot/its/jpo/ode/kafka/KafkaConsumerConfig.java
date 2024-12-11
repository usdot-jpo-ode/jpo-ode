package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.util.XmlUtils;

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

  /**
   * Creates a {@link ConcurrentKafkaListenerContainerFactory} for processing Kafka messages. This
   * factory is configured to filter out certain messages based on a defined strategy.
   *
   * @return factory A listener factory that supports filtering out messages that don't match a
   *      specific pattern
   * @deprecated This method is intended to be short-lived. It exists to allow consumption via the
   *      Asn1DecodedDataRouter & the Asn1DecodedDataListener while we are migrating from hand-rolled
   *      Kafka implementation to Spring's Kafka implementation
   */
  @Bean
  @Deprecated(forRemoval = true)
  public ConcurrentKafkaListenerContainerFactory<String, String> tempFilteringKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordFilterStrategy(getFilterStrategySpringKafkaSupportedMessageTypesOnly());

    return factory;
  }

  /**
   * While migrating to Spring Kafka the consumers provided from this factory will only consume (and
   * ack) messages we support via the Spring Kafka implementation. All other messages will be
   * handled by the Asn1DecodedDataRouter
   *
   * @return filter
   */
  private static RecordFilterStrategy<String, String> getFilterStrategySpringKafkaSupportedMessageTypesOnly() {
    return consumerRecord -> {
      try {
        JSONObject consumed = XmlUtils.toJSONObject(consumerRecord.value())
            .getJSONObject(OdeAsn1Data.class.getSimpleName());

        J2735DSRCmsgID messageId = J2735DSRCmsgID.valueOf(
            consumed.getJSONObject(AppContext.PAYLOAD_STRING).getJSONObject(AppContext.DATA_STRING)
                .getJSONObject("MessageFrame").getInt("messageId"));

        // Filter out all messages EXCEPT for MAP messages
        return !J2735DSRCmsgID.MAPMessage.equals(messageId);
      } catch (XmlUtils.XmlUtilsException e) {
        log.warn("Unable to parse JSON object", e);
        return false;
      } catch (Exception e) {
        log.warn("Failed to detect message ID", e);
        return false;
      }
    };
  }

  private Map<String, Object> getKafkaConsumerProperties() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    if ("CONFLUENT".equals(this.odeKafkaProperties.getKafkaType())) {
      props.putAll(this.odeKafkaProperties.getConfluent().buildConfluentProperties());
    }
    return props;
  }
}

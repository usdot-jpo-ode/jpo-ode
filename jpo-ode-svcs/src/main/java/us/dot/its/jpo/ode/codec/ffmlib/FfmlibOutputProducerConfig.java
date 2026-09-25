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
      KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties) {
    Map<String, Object> config = new HashMap<>(
        OdeKafkaClients.producerProperties(kafkaProperties, odeKafkaProperties));
    config.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(config);
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
}

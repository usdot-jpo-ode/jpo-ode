package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

class FfmlibOutputProducerConfigTest {

  @Test
  void appliesDurableLowLatencySettingsToRawAndOutputProducers() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    FfmlibProperties ffmlibProperties = new FfmlibProperties();
    FfmlibOutputProducerConfig configuration = new FfmlibOutputProducerConfig();

    Map<String, Object> output = producerSettings(configuration.ffmlibOutputProducerFactory(
        kafkaProperties, odeKafkaProperties, ffmlibProperties));
    Map<String, Object> raw = producerSettings(configuration.ffmlibRawProducerFactory(
        kafkaProperties, odeKafkaProperties, ffmlibProperties));

    for (Map<String, Object> producer : List.of(output, raw)) {
      assertEquals("all", producer.get(ProducerConfig.ACKS_CONFIG));
      assertEquals(true, producer.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
      assertEquals(0, producer.get(ProducerConfig.LINGER_MS_CONFIG));
      assertEquals("none", producer.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
      assertEquals(2, producer.get(ProducerConfig.RETRIES_CONFIG));
    }
    assertEquals(FfmlibRawTopicPartitioner.class,
        raw.get(ProducerConfig.PARTITIONER_CLASS_CONFIG));
  }

  private static Map<String, Object> producerSettings(
      org.springframework.kafka.core.ProducerFactory<String, String> factory) {
    return ((DefaultKafkaProducerFactory<String, String>) factory).getConfigurationProperties();
  }
}

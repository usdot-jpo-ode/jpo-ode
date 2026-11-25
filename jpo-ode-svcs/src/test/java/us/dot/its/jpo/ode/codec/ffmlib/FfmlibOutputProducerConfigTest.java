package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibProperties.UdpRouting;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.InterceptingKafkaTemplate;

class FfmlibOutputProducerConfigTest {

  private final FfmlibOutputProducerConfig configuration = new FfmlibOutputProducerConfig();

  @Test
  void rawTopicRoutingInheritsSharedSettingsAndForcesDurableDelivery() {
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    odeKafkaProperties.getProducer().setAcks("1");
    FfmlibProperties ffmlibProperties = new FfmlibProperties();

    for (Map<String, Object> producer : producers(odeKafkaProperties, ffmlibProperties)) {
      assertEquals("all", producer.get(ProducerConfig.ACKS_CONFIG));
      assertEquals(true, producer.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
      assertEquals(2, producer.get(ProducerConfig.RETRIES_CONFIG));
      assertEquals(1, producer.get(ProducerConfig.LINGER_MS_CONFIG));
      assertEquals("zstd", producer.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
      assertEquals(16384, producer.get(ProducerConfig.BATCH_SIZE_CONFIG));
    }
  }

  @Test
  void directRoutingUsesTheSharedSettingsUnchanged() {
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    odeKafkaProperties.getProducer().setAcks("1");
    odeKafkaProperties.getProducer().setLingerMs(5);
    odeKafkaProperties.getProducer().setCompressionType("lz4");
    FfmlibProperties ffmlibProperties = new FfmlibProperties();
    ffmlibProperties.setUdpRouting(UdpRouting.DIRECT);

    for (Map<String, Object> producer : producers(odeKafkaProperties, ffmlibProperties)) {
      assertEquals("1", producer.get(ProducerConfig.ACKS_CONFIG));
      assertFalse(producer.containsKey(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
      assertEquals(0, producer.get(ProducerConfig.RETRIES_CONFIG));
      assertEquals(5, producer.get(ProducerConfig.LINGER_MS_CONFIG));
      assertEquals("lz4", producer.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
    }
  }

  @Test
  void rawProducerUsesRoundRobinPartitionerAndInterceptingTemplate() {
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    ProducerFactory<String, String> rawFactory = configuration.ffmlibRawProducerFactory(
        kafkaProperties(), odeKafkaProperties, new FfmlibProperties());

    assertEquals(FfmlibRawTopicPartitioner.class,
        settings(rawFactory).get(ProducerConfig.PARTITIONER_CLASS_CONFIG));
    assertInstanceOf(InterceptingKafkaTemplate.class, configuration.ffmlibRawKafkaTemplate(
        rawFactory, odeKafkaProperties, new SimpleMeterRegistry(), new ObjectMapper()));
  }

  private List<Map<String, Object>> producers(OdeKafkaProperties odeKafkaProperties,
      FfmlibProperties ffmlibProperties) {
    return List.of(
        settings(configuration.ffmlibOutputProducerFactory(kafkaProperties(), odeKafkaProperties,
            ffmlibProperties)),
        settings(configuration.ffmlibRawProducerFactory(kafkaProperties(), odeKafkaProperties,
            ffmlibProperties)));
  }

  private static KafkaProperties kafkaProperties() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    return kafkaProperties;
  }

  private static Map<String, Object> settings(ProducerFactory<String, String> factory) {
    return ((DefaultKafkaProducerFactory<String, String>) factory).getConfigurationProperties();
  }
}

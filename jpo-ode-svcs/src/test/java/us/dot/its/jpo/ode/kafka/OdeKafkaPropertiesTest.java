package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = OdeKafkaProperties.class)
class OdeKafkaPropertiesTest {

  @Autowired
  private OdeKafkaProperties odeKafkaProperties;

  @Test
  void testGetBrokers() {
    assertEquals("localhost:4242", odeKafkaProperties.getBrokers());
  }

  @Test
  void testGetKafkaTopicsDisabled() {
    Set<String> kafkaTopicsDisabled = odeKafkaProperties.getDisabledTopics();
    assertEquals(1, kafkaTopicsDisabled.size());
    assertTrue(kafkaTopicsDisabled.contains(""));
  }

  @Test
  void testGetProducerAcks() {
    assertEquals("all", odeKafkaProperties.getProducer().getAcks());
  }

  @Test
  void testGetProducerBatchSize() {
    assertEquals(1638, odeKafkaProperties.getProducer().getBatchSize());
  }

  @Test
  void testGetProducerBufferMemory() {
    assertEquals(33554433, odeKafkaProperties.getProducer().getBufferMemory());
  }

  @Test
  void testGetProducerKeySerializer() {
    assertEquals("org.apache.kafka.common.serialization.StringSerializer",
        odeKafkaProperties.getProducer().getKeySerializer());
  }

  @Test
  void testGetProducerLingerMs() {
    assertEquals(2, odeKafkaProperties.getProducer().getLingerMs());
  }

  @Test
  void testGetProducerPartitionerClass() {
    assertEquals("org.apache.kafka.clients.producer.internals.DefaultPartitioner",
        odeKafkaProperties.getProducer().getPartitionerClass());
  }

  @Test
  void testGetProducerRetries() {
    assertEquals(1, odeKafkaProperties.getProducer().getRetries());
  }

  @Test
  void testGetProducerType() {
    assertEquals("async", odeKafkaProperties.getProducer().getType());
  }

  @Test
  void testGetProducerValueSerializer() {
    assertEquals("org.apache.kafka.common.serialization.ByteArraySerializer",
        odeKafkaProperties.getProducer().getValueSerializer());
  }

  @Test
  void testGetProducerCompressionType() {
    assertEquals("zstd", odeKafkaProperties.getProducer().getCompressionType());
  }
}

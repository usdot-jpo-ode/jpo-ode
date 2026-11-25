package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

class OdeKafkaClientsTest {

  @Test
  void adminClientUsesTheConfiguredOdeBroker() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    odeKafkaProperties.setBrokers("kafka.internal:9092,kafka-backup.internal:9092");

    var properties = OdeKafkaClients.adminProperties(kafkaProperties, odeKafkaProperties);

    assertEquals("kafka.internal:9092,kafka-backup.internal:9092",
        properties.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
  }

  @Test
  void adminClientRetainsSpringBootstrapServersWhenOdeBrokerIsNotConfigured() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();

    var properties = OdeKafkaClients.adminProperties(kafkaProperties, odeKafkaProperties);

    assertEquals(List.of("localhost:9092"),
        properties.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
  }

  @Test
  void producerAppliesEveryOdeDeliverySettingButNotSerializersOrPartitioner() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
    OdeKafkaProperties.Producer producer = odeKafkaProperties.getProducer();
    producer.setAcks("1");
    producer.setBatchSize(32768);
    producer.setBufferMemory(1048576);
    producer.setLingerMs(5);
    producer.setRetries(3);
    producer.setCompressionType("lz4");
    producer.setValueSerializer("example.UnappliedSerializer");
    producer.setPartitionerClass("example.UnappliedPartitioner");

    var properties = OdeKafkaClients.producerProperties(kafkaProperties, odeKafkaProperties);

    assertEquals("1", properties.get(ProducerConfig.ACKS_CONFIG));
    assertEquals(32768, properties.get(ProducerConfig.BATCH_SIZE_CONFIG));
    assertEquals(1048576, properties.get(ProducerConfig.BUFFER_MEMORY_CONFIG));
    assertEquals(5, properties.get(ProducerConfig.LINGER_MS_CONFIG));
    assertEquals(3, properties.get(ProducerConfig.RETRIES_CONFIG));
    assertEquals("lz4", properties.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
    assertNotEquals("example.UnappliedSerializer",
        properties.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    assertFalse(properties.containsKey(ProducerConfig.PARTITIONER_CLASS_CONFIG));
  }

  @Test
  void producerDefaultsMatchTheSharedOdeDefaults() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));

    var properties = OdeKafkaClients.producerProperties(kafkaProperties,
        new OdeKafkaProperties());

    assertEquals("all", properties.get(ProducerConfig.ACKS_CONFIG));
    assertEquals(1, properties.get(ProducerConfig.LINGER_MS_CONFIG));
    assertEquals("zstd", properties.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
  }
}

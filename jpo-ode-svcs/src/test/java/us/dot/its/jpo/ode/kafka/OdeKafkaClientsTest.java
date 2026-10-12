package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.apache.kafka.clients.admin.AdminClientConfig;
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
}

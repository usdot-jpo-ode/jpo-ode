package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

class FfmlibKafkaListenerConfigurationTest {

  @Test
  void defaultsToAsyncCommitsAndAllowsSynchronousComparison() {
    FfmlibKafkaListenerConfiguration configuration = new FfmlibKafkaListenerConfiguration();
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    OdeKafkaProperties odeProperties = new OdeKafkaProperties();
    FfmlibProperties ffmlibProperties = new FfmlibProperties();
    ffmlibProperties.setListenerConcurrency(4);
    assertEquals(Duration.ofSeconds(120), ffmlibProperties.getStartupTimeout());

    var asyncFactory = configuration.ffmlibKafkaListenerContainerFactory(kafkaProperties,
        odeProperties, ffmlibProperties);
    assertEquals(AckMode.MANUAL_IMMEDIATE, asyncFactory.getContainerProperties().getAckMode());
    assertFalse(asyncFactory.getContainerProperties().isSyncCommits());

    ffmlibProperties.setSyncCommits(true);
    var syncFactory = configuration.ffmlibKafkaListenerContainerFactory(kafkaProperties,
        odeProperties, ffmlibProperties);
    assertTrue(syncFactory.getContainerProperties().isSyncCommits());
  }

  @Test
  void partitionsWithoutCommittedOffsetsStartAtTheOldestRecord() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of("localhost:9092"));
    kafkaProperties.getConsumer().setAutoOffsetReset("latest");

    var factory = new FfmlibKafkaListenerConfiguration().ffmlibKafkaListenerContainerFactory(
        kafkaProperties, new OdeKafkaProperties(), new FfmlibProperties());

    assertEquals("earliest", factory.getConsumerFactory().getConfigurationProperties()
        .get(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
  }
}

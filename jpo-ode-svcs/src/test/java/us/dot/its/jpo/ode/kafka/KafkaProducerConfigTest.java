package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties.Producer;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;

@Slf4j
@ExtendWith(SpringExtension.class)
@DirtiesContext
@EnableConfigurationProperties({KafkaProperties.class})
@Import({KafkaProducerConfigTest.KafkaProducerConfigTestConfig.class})
class KafkaProducerConfigTest {

  @Autowired
  @Qualifier("testKafkaProducerConfig")
  KafkaProducerConfig kafkaProducerConfig;
  @Autowired
  @Qualifier("testOdeKafkaProperties")
  OdeKafkaProperties odeKafkaProperties;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Test
  void odeDataProducerFactory_shouldReturnNonNull() {
    ProducerFactory<String, OdeObject> producerFactory =
        kafkaProducerConfig.odeDataProducerFactory();
    assertNotNull(producerFactory);
  }

  @Test
  void odeDataProducerFactory_shouldReturnDefaultKafkaProducerFactory() {
    ProducerFactory<String, OdeObject> producerFactory =
        kafkaProducerConfig.odeDataProducerFactory();
    assertNotNull(producerFactory);
    assertInstanceOf(DefaultKafkaProducerFactory.class, producerFactory);
  }

  @Test
  void kafkaTemplateInterceptorPreventsSendingToDisabledTopics() {
    EmbeddedKafkaHolder.addTopics(odeKafkaProperties.getDisabledTopics().toArray(new String[0]));
    var consumerProps =
        KafkaTestUtils.consumerProps("interceptor-disabled",
            "false",
            embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<>(consumerProps,
        new StringDeserializer(), new StringDeserializer());
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(consumer,
        odeKafkaProperties.getDisabledTopics().toArray(new String[0]));
    KafkaTemplate<String, String> stringKafkaTemplate = kafkaProducerConfig.kafkaTemplate(
        kafkaProducerConfig.producerFactory());
    // Attempting to send to a disabled topic
    for (String topic : odeKafkaProperties.getDisabledTopics()) {
      stringKafkaTemplate.send(topic, "key", "value");

      var records = KafkaTestUtils.getEndOffsets(consumer, topic, 0);
      // Assert that the message we attempted to send to the disabled topic was intercepted
      // and not sent
      assertTrue(records
          .entrySet()
          .stream()
          .allMatch(e -> e.getValue() == 0L)
      );
    }
  }

  @Test
  void kafkaTemplateInterceptorAllowsSendingToTopicsNotInDisabledSet() {
    String enabledTopic = "topic.enabled" + this.getClass().getSimpleName();
    EmbeddedKafkaHolder.addTopics(enabledTopic);

    var consumerProps =
        KafkaTestUtils.consumerProps("interceptor-enabled", "false", embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<>(consumerProps,
        new StringDeserializer(), new StringDeserializer());
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, enabledTopic);

    // Attempting to send to a topic not in the disabledTopics set with the string template
    KafkaTemplate<String, String> stringKafkaTemplate = kafkaProducerConfig.kafkaTemplate(
        kafkaProducerConfig.producerFactory());
    stringKafkaTemplate.send(enabledTopic, "key", "value");

    var records = KafkaTestUtils.getRecords(consumer);
    var produced = records.records(enabledTopic).iterator().next();
    assertEquals("key", produced.key());
    assertEquals("value", produced.value());
  }

  @Test
  void kafkaTemplateInterceptorCanSendAfterAttemptToSendToDisabledTopic() {
    String enabledTopic = "topic.enabled" + this.getClass().getSimpleName();
    EmbeddedKafkaHolder.addTopics(enabledTopic);

    var consumerProps =
        KafkaTestUtils.consumerProps("send-after", "false", embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<>(consumerProps,
        new StringDeserializer(), new StringDeserializer());
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, enabledTopic);

    KafkaTemplate<String, String> stringKafkaTemplate = kafkaProducerConfig.kafkaTemplate(
        kafkaProducerConfig.producerFactory());
    var blockedTopic = odeKafkaProperties.getDisabledTopics().iterator().next();
    stringKafkaTemplate.send(blockedTopic, "blocked", "not sent");
    stringKafkaTemplate.send(enabledTopic, "key", "value");

    var records = KafkaTestUtils.getRecords(consumer);
    assertFalse(records.records(blockedTopic).iterator().hasNext());

    var produced = records.records(enabledTopic).iterator().next();
    assertEquals("key", produced.key());
    assertEquals("value", produced.value());
  }

  @TestConfiguration
  static class KafkaProducerConfigTestConfig {

    @Bean
    public OdeKafkaProperties testOdeKafkaProperties() {
      OdeKafkaProperties odeKafkaProperties = new OdeKafkaProperties();
      odeKafkaProperties.setBrokers("localhost:4242");
      odeKafkaProperties.setProducer(new Producer());
      var uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
      odeKafkaProperties.setDisabledTopics(Set.of(
          "topic.OdeBsmRxPojo" + uniqueSuffix,
          "topic.OdeBsmTxPojo" + uniqueSuffix,
          "topic.OdeBsmDuringEventPojo" + uniqueSuffix,
          "topic.OdeTimBroadcastPojo" + uniqueSuffix
      ));

      return odeKafkaProperties;
    }

    @Bean
    public KafkaProducerConfig testKafkaProducerConfig(KafkaProperties kafkaProperties, OdeKafkaProperties testOdeKafkaProperties) {
      return new KafkaProducerConfig(kafkaProperties, testOdeKafkaProperties);
    }
  }
}
package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import us.dot.its.jpo.ode.kafka.producer.DisabledTopicException;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;

@Slf4j
@SpringBootTest(
    classes = {
        KafkaProducerConfig.class,
        OdeKafkaProperties.class,
        KafkaProperties.class
    }
)
@EnableConfigurationProperties
class KafkaProducerConfigTest {

  @Autowired
  KafkaProducerConfig kafkaProducerConfig;
  @Autowired
  OdeKafkaProperties odeKafkaProperties;

  EmbeddedKafkaBroker embeddedKafka;
  KafkaTemplate<String, String> stringKafkaTemplate;
  KafkaTemplate<String, OdeObject> odeObjectKafkaTemplate;

  @BeforeEach
  public void beforeClass() {
    embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();
    var topics = embeddedKafka.getTopics();
    for (String topic : odeKafkaProperties.getDisabledTopics()) {
      if (!topics.contains(topic)) {
        embeddedKafka.addTopics(new NewTopic(topic, 1, (short) 1));
      }
    }
    stringKafkaTemplate =
        kafkaProducerConfig.kafkaTemplate(kafkaProducerConfig.producerFactory());
    odeObjectKafkaTemplate =
        kafkaProducerConfig.odeDataKafkaTemplate(kafkaProducerConfig.odeDataProducerFactory());
  }

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
    var consumerProps =
        KafkaTestUtils.consumerProps("interceptor-disabled",
            "false",
            embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<String, String>(consumerProps);
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAllEmbeddedTopics(consumer);

    // Attempting to send to a disabled topic
    for (String topic : odeKafkaProperties.getDisabledTopics()) {
      assertThrows(DisabledTopicException.class,
          () -> stringKafkaTemplate.send(topic, "key", "value"));

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
    embeddedKafka.addTopics(new NewTopic(enabledTopic, 1, (short) 1));

    var consumerProps =
        KafkaTestUtils.consumerProps("interceptor-enabled", "false", embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<String, String>(consumerProps);
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, enabledTopic);

    // Attempting to send to a topic not in the disabledTopics set with the string template
    var stringCompletableFuture = stringKafkaTemplate.send(enabledTopic, "key", "value");
    Awaitility.await().until(stringCompletableFuture::isDone);

    var records = KafkaTestUtils.getEndOffsets(consumer, enabledTopic, 0);
    assertTrue(records.entrySet().stream().allMatch(e -> e.getValue() > 0L));
  }

}
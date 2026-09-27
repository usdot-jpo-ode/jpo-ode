package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@SpringBootTest(classes = FfmlibRawJsonListenerOffsetTest.TestContext.class,
    properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(partitions = 1, topics = {FfmlibRawJsonListenerOffsetTest.RAW_TOPIC})
class FfmlibRawJsonListenerOffsetTest {

  static final String RAW_TOPIC = "topic.FfmOffsetRecoveryRaw";
  private static final String GROUP_ID = "RawEncodedBSMJsonRouter";
  private static final String RAW_JSON = "{\"payload\":{\"data\":{\"bytes\":\"0014\"}}}";

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Configuration
  @EnableAutoConfiguration
  static class TestContext {
  }

  @Test
  @SuppressWarnings("unchecked")
  void failedOutputDoesNotCommitAndRestartReplaysTheSameRawRecord() throws Exception {
    var rawService = mock(RawEncodedJsonService.class);
    var decoder = mock(FfmlibDecodeService.class);
    var output = mock(FfmlibOutputPublisher.class);
    var topics = mock(RawEncodedJsonTopics.class);
    var quarantine = mock(KafkaTemplate.class);
    when(topics.getBsm()).thenReturn(RAW_TOPIC);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    when(rawService.parseFfmRecord(eq(RAW_JSON), eq(SupportedMessageType.BSM)))
        .thenReturn(new RawEncodedJsonService.FfmRawRecord(metadata, new byte[] {0, 20},
            new byte[] {0, 20}));
    PreparedDecodedMessage prepared = new PreparedDecodedMessage("topic.OdeBsmJson", "key", "{}",
        metadata, "BSM", "udp", 1L, 1L);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("key"),
        eq(SupportedMessageType.BSM), any(byte[].class))).thenReturn(prepared);
    AtomicInteger publicationAttempt = new AtomicInteger();
    when(output.publish(any())).thenAnswer(invocation -> {
      if (publicationAttempt.incrementAndGet() <= 3) {
        return CompletableFuture.failedFuture(new IllegalStateException("broker unavailable"));
      }
      return CompletableFuture.completedFuture(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
    });

    FfmlibRawJsonListener listener = new FfmlibRawJsonListener(rawService, decoder, output,
        quarantine, topics);
    TopicPartition inputPartition = new TopicPartition(RAW_TOPIC, 0);
    try (AdminClient admin = AdminClient.create(Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString()))) {
      KafkaTemplate<String, String> producer = producer(embeddedKafka);
      try {
        KafkaMessageListenerContainer<String, String> failedContainer =
            container(embeddedKafka, listener);
        failedContainer.start();
        ContainerTestUtils.waitForAssignment(failedContainer, 1);
        try {
          producer.send(RAW_TOPIC, "key", RAW_JSON).get(10, TimeUnit.SECONDS);
          awaitStopped(failedContainer);
          assertNull(committedOffset(admin, inputPartition));
          assertEquals(3, publicationAttempt.get());
        } finally {
          failedContainer.stop();
        }

        KafkaMessageListenerContainer<String, String> recoveryContainer =
            container(embeddedKafka, listener);
        recoveryContainer.start();
        ContainerTestUtils.waitForAssignment(recoveryContainer, 1);
        try {
          awaitCommittedOffset(admin, inputPartition, 1L);
          assertEquals(4, publicationAttempt.get());
          assertEquals(1L, committedOffset(admin, inputPartition).offset());
        } finally {
          recoveryContainer.stop();
        }
      } finally {
        producer.destroy();
      }
    }
  }

  private static KafkaMessageListenerContainer<String, String> container(
      EmbeddedKafkaBroker broker, FfmlibRawJsonListener listener) {
    Map<String, Object> consumerProperties = new HashMap<>(
        KafkaTestUtils.consumerProps(broker, GROUP_ID, false));
    consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties,
        new StringDeserializer(), new StringDeserializer());
    ContainerProperties properties = new ContainerProperties(RAW_TOPIC);
    properties.setGroupId(GROUP_ID);
    properties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    properties.setSyncCommits(true);
    KafkaMessageListenerContainer<String, String> container =
        new KafkaMessageListenerContainer<>(consumerFactory, properties);
    container.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());
    container.setupMessageListener((AcknowledgingMessageListener<String, String>)
        (ConsumerRecord<String, String> record, Acknowledgment acknowledgment) ->
            listener.bsm(record, acknowledgment));
    return container;
  }

  private static KafkaTemplate<String, String> producer(EmbeddedKafkaBroker broker) {
    Map<String, Object> properties = new HashMap<>(KafkaTestUtils.producerProps(broker));
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties));
  }

  private static OffsetAndMetadata committedOffset(AdminClient admin, TopicPartition partition)
      throws Exception {
    return admin.listConsumerGroupOffsets(GROUP_ID).partitionsToOffsetAndMetadata()
        .get(10, TimeUnit.SECONDS).get(partition);
  }

  private static void awaitStopped(KafkaMessageListenerContainer<?, ?> container)
      throws InterruptedException {
    long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
    while (container.isRunning() && System.nanoTime() < deadline) {
      Thread.sleep(25);
    }
    assertTrue(!container.isRunning(), "failed listener container should stop");
  }

  private static void awaitCommittedOffset(AdminClient admin, TopicPartition partition,
      long expected) throws Exception {
    long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
    OffsetAndMetadata committed = null;
    while (System.nanoTime() < deadline) {
      committed = committedOffset(admin, partition);
      if (committed != null && committed.offset() == expected) {
        return;
      }
      Thread.sleep(25);
    }
    assertEquals(expected, committed == null ? -1L : committed.offset());
  }
}

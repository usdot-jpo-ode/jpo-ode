package us.dot.its.jpo.ode.udp.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

/** Exercises generic UDP isolation against a delayed raw-topic consumer using real sockets. */
@EnableConfigurationProperties
@SpringBootTest(classes = {OdeKafkaProperties.class, UDPReceiverProperties.class,
    KafkaProducerConfig.class, SerializationConfig.class, TestMetricsConfig.class,
    RawEncodedJsonTopics.class, KafkaProperties.class},
    properties = {"ode.receivers.generic.receiver-port=15461",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "ode.kafka.brokers=${spring.embedded.kafka.brokers}",
        "ode.kafka.topics.raw-encoded-json.bsm=topic.GenericIsolationBSM",
        "ode.kafka.topics.raw-encoded-json.map=topic.GenericIsolationMAP",
        "ode.kafka.topics.raw-encoded-json.spat=topic.GenericIsolationSPAT"})
@DirtiesContext
@EmbeddedKafka(partitions = 4, topics = {"topic.GenericIsolationBSM",
    "topic.GenericIsolationMAP", "topic.GenericIsolationSPAT"})
class GenericReceiverIsolationTest {

  private static final String BSM_TOPIC = "topic.GenericIsolationBSM";
  private static final String MAP_TOPIC = "topic.GenericIsolationMAP";
  private static final String SPAT_TOPIC = "topic.GenericIsolationSPAT";

  @Autowired
  private UDPReceiverProperties receiverProperties;

  @Autowired
  private RawEncodedJsonTopics topics;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Test
  void genericSocketContinuesOtherTypesWhileBsmConsumerIsStalled() throws Exception {
    GenericReceiver receiver = new GenericReceiver(receiverProperties.getGeneric(), kafkaTemplate,
        topics);
    Thread receiverThread = new Thread(receiver, "generic-udp-isolation-test");
    receiverThread.start();
    try (Consumer<String, String> bsmConsumer = consumer("RawEncodedBSMJsonRouter");
        Consumer<String, String> spatConsumer = consumer("RawEncodedSPATJsonRouter");
        Consumer<String, String> mapConsumer = consumer("RawEncodedMAPJsonRouter")) {
      assignFromBeginning(bsmConsumer, BSM_TOPIC);
      assignFromBeginning(spatConsumer, SPAT_TOPIC);
      assignFromBeginning(mapConsumer, MAP_TOPIC);

      sendFixture("src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM.txt",
          receiverProperties.getGeneric().getReceiverPort());
      ConsumerRecord<String, String> stalledBsm = pollOne(bsmConsumer, BSM_TOPIC);
      TopicPartition bsmPartition = new TopicPartition(BSM_TOPIC, stalledBsm.partition());
      assertNull(bsmConsumer.committed(Set.of(bsmPartition)).get(bsmPartition));

      // Leave the first BSM uncommitted to represent a native decode that has not completed.
      sendFixture("src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM.txt",
          receiverProperties.getGeneric().getReceiverPort());
      sendFixture("src/test/resources/us/dot/its/jpo/ode/udp/spat/SpatReceiverTest_ValidSPAT.txt",
          receiverProperties.getGeneric().getReceiverPort());
      sendFixture("src/test/resources/us/dot/its/jpo/ode/udp/map/MapReceiverTest_ValidMAP.txt",
          receiverProperties.getGeneric().getReceiverPort());

      assertTrue(pollOne(spatConsumer, SPAT_TOPIC).value().contains("payload"));
      assertTrue(pollOne(mapConsumer, MAP_TOPIC).value().contains("payload"));

      // Releasing the delayed BSM path lets the same group drain the outstanding record.
      bsmConsumer.commitSync();
      ConsumerRecord<String, String> nextBsm = pollOne(bsmConsumer, BSM_TOPIC);
      assertEquals(BSM_TOPIC, nextBsm.topic());
      bsmConsumer.commitSync();
      TopicPartition nextPartition = new TopicPartition(BSM_TOPIC, nextBsm.partition());
      OffsetAndMetadata committed = bsmConsumer.committed(Set.of(nextPartition))
          .get(nextPartition);
      assertEquals(nextBsm.offset() + 1, committed.offset());
    } finally {
      receiver.setStopped(true);
      sendPacket(new byte[0], receiverProperties.getGeneric().getReceiverPort());
      receiverThread.join(2000);
      assertFalse(receiverThread.isAlive());
    }
  }

  private Consumer<String, String> consumer(String groupId) {
    var properties = KafkaTestUtils.consumerProps(embeddedKafka, groupId, false);
    properties.put("auto.offset.reset", "earliest");
    return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(),
        new StringDeserializer()).createConsumer();
  }

  private void assignFromBeginning(Consumer<String, String> consumer, String topic) {
    List<TopicPartition> partitions = new ArrayList<>();
    for (int partition = 0; partition < 4; partition++) {
      partitions.add(new TopicPartition(topic, partition));
    }
    consumer.assign(partitions);
    consumer.seekToBeginning(partitions);
  }

  private static ConsumerRecord<String, String> pollOne(Consumer<String, String> consumer,
      String topic) {
    long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
    while (System.nanoTime() < deadline) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
      for (ConsumerRecord<String, String> record : records.records(topic)) {
        return record;
      }
    }
    throw new AssertionError("Timed out awaiting raw record on " + topic);
  }

  private static void sendFixture(String fixturePath, int port) throws IOException {
    String fixture = Files.readString(Path.of(fixturePath)).trim();
    sendPacket(HexUtils.fromHexString(fixture), port);
  }

  private static void sendPacket(byte[] payload, int port) throws IOException {
    try (DatagramSocket socket = new DatagramSocket()) {
      DatagramPacket packet = new DatagramPacket(payload, payload.length,
          InetAddress.getLoopbackAddress(), port);
      socket.send(packet);
    }
  }
}

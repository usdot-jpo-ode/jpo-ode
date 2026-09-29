package us.dot.its.jpo.ode.udp.bsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;

@EnableConfigurationProperties
@SpringBootTest(
    classes = { OdeKafkaProperties.class, UDPReceiverProperties.class, KafkaProducerConfig.class,
       KafkaConsumerConfig.class, SerializationConfig.class, TestMetricsConfig.class,
       RawEncodedJsonTopics.class, KafkaProperties.class},
    properties = {"ode.receivers.bsm.receiver-port=15352",
        "ode.kafka.topics.raw-encoded-json.bsm=topic.BsmReceiverTest"})
@EmbeddedKafka
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext
class BsmReceiverTest {

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  private CompletableFuture<String> future;

  @Test
  void testRun() throws Exception {
    future = new CompletableFuture<>();
    final Clock prevClock = DateTimeUtils
        .setClock(Clock.fixed(Instant.parse("2024-11-26T23:53:21.120Z"), ZoneId.of("UTC")));
    // create the BsmReceiver and submit it to a runner
    BsmReceiver bsmReceiver = new BsmReceiver(udpReceiverProperties.getBsm(), kafkaTemplate,
        rawEncodedJsonTopics.getBsm());
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit(bsmReceiver);

    String fileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM.txt"));
    String expected = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM_expected.json"));

    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getBsm().getReceiverPort());
    udpClient.send(fileContent);

    String actualPayload = future.get(3, TimeUnit.SECONDS);

    // confirm the stream-id is different, then remove it from both so that we can test equality
    // of all other fields
    assertNotEquals(expected, actualPayload);
    JSONObject producedJson = new JSONObject(actualPayload);
    JSONObject expectedJson = new JSONObject(expected);

    // assert that the UUIDs are different, then remove them so that the rest of the JSON can be
    // compared
    assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
        producedJson.getJSONObject("metadata").get("serialId"));
    expectedJson.getJSONObject("metadata").remove("serialId");
    producedJson.getJSONObject("metadata").remove("serialId");

    assertEquals(expectedJson.toString(2), producedJson.toString(2));

    DateTimeUtils.setClock(prevClock);
  }

  @KafkaListener(topics = "topic.BsmReceiverTest")
  public void receive(String payload) {
    future.complete(payload);
  }
}
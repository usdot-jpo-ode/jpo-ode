package us.dot.its.jpo.ode.udp.sdsm;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
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
import us.dot.its.jpo.ode.util.JsonUtils;

@EnableConfigurationProperties
@SpringBootTest(
    classes = {KafkaConsumerConfig.class,  KafkaProducerConfig.class,
        SerializationConfig.class, TestMetricsConfig.class, UDPReceiverProperties.class, RawEncodedJsonTopics.class, KafkaProperties.class, OdeKafkaProperties.class},
    properties = {"ode.receivers.sdsm.receiver-port=12413",
        "ode.kafka.topics.raw-encoded-json.sdsm=topic.SdsmReceiverTest"})
@EmbeddedKafka
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext
class SdsmReceiverTest {

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  private ExecutorService executorService;
  private SdsmReceiver sdsmReceiver;

  private CompletableFuture<String> future;

  @AfterEach
  void cleanup() {
    if (executorService != null) {
      executorService.shutdown();
    }
    if (sdsmReceiver != null) {
      sdsmReceiver.setStopped(true);
    }
  }

  @Test
  void testRun() throws Exception {
    future = new CompletableFuture<>();

    final Clock prevClock = DateTimeUtils
        .setClock(Clock.fixed(Instant.parse("2024-11-26T23:53:21.120Z"), ZoneOffset.UTC));

    try {
      SdsmReceiver sdsmReceiver = new SdsmReceiver(udpReceiverProperties.getSdsm(), kafkaTemplate,
          rawEncodedJsonTopics.getSdsm());
      ExecutorService executorService = Executors.newCachedThreadPool();
      executorService.submit(sdsmReceiver);

      String fileContent = Files.readString(Paths
          .get("src/test/resources/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiverTest_ValidSDSM.txt"));
      String expected = Files.readString(Paths.get(
          "src/test/resources/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiverTest_ValidSDSM_expected.json"));

      TestUDPClient udpClient =
          new TestUDPClient(udpReceiverProperties.getSdsm().getReceiverPort());
      udpClient.send(fileContent);

      String actualPayload = future.get(3, TimeUnit.SECONDS);

      assertNotEquals(expected, actualPayload);

      JSONObject producedJson = new JSONObject(actualPayload);
      JSONObject expectedJson = new JSONObject(expected);

      assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
          producedJson.getJSONObject("metadata").get("serialId"));
      expectedJson.getJSONObject("metadata").remove("serialId");
      producedJson.getJSONObject("metadata").remove("serialId");

      assertThat(JsonUtils.toJson(producedJson, false),
          jsonEquals(JsonUtils.toJson(expectedJson, false)));
    } finally {
      DateTimeUtils.setClock(prevClock);
    }
  }

  @KafkaListener(topics = "topic.SdsmReceiverTest")
  public void receive(String payload) {
    future.complete(payload);
  }
}
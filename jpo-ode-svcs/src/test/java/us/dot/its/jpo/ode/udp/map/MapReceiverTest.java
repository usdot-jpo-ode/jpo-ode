package us.dot.its.jpo.ode.udp.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static us.dot.its.jpo.ode.test.utilities.ApprovalTestCase.deserializeTestCases;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
import us.dot.its.jpo.ode.test.utilities.ApprovalTestCase;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;

@SpringBootTest(
    classes = {
        KafkaConsumerConfig.class,
        KafkaProducerConfig.class,
        SerializationConfig.class,
        TestMetricsConfig.class,
        UDPReceiverProperties.class,
        OdeKafkaProperties.class,
        RawEncodedJsonTopics.class,
        KafkaProperties.class
    },
    properties = {"ode.kafka.topics.raw-encoded-json.map=topic.MapReceiverTestMAPJSON",
        "ode.receivers.map.receiver-port=12412"}
)
@EnableConfigurationProperties
@EmbeddedKafka
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext
class MapReceiverTest {

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  private final BlockingQueue<String> receivedMessages = new LinkedBlockingQueue<>();

  @Test
  void testMapReceiver() throws IOException, InterruptedException {
    // Set the clock to a fixed time so that the MapReceiver will produce the same output every time
    final Clock prevClock = DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), Clock.systemUTC().getZone()));

    MapReceiver mapReceiver = new MapReceiver(udpReceiverProperties.getMap(), kafkaTemplate,
        rawEncodedJsonTopics.getMap());
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit(mapReceiver);

    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getMap().getReceiverPort());

    String path =
        "src/test/resources/us.dot.its.jpo.ode.udp.map/UDPMAP_To_EncodedJSON_Validation.json";
    List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path);

    for (ApprovalTestCase approvalTestCase : approvalTestCases) {
      receivedMessages.clear();
      udpClient.send(approvalTestCase.getInput());

      String actualPayload = receivedMessages.poll(3, TimeUnit.SECONDS);

      JSONObject producedJson = new JSONObject(actualPayload);
      JSONObject expectedJson = new JSONObject(approvalTestCase.getExpected());

      // assert that the UUIDs are different, then remove them so that the rest of the JSON can be compared
      assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
          producedJson.getJSONObject("metadata").get("serialId"));
      expectedJson.getJSONObject("metadata").remove("serialId");
      producedJson.getJSONObject("metadata").remove("serialId");

      assertEquals(expectedJson.toString(), producedJson.toString().trim(),
          approvalTestCase.getDescription());
    }

    DateTimeUtils.setClock(prevClock);
  }

  @KafkaListener(topics = "topic.MapReceiverTestMAPJSON")
  public void receive(String payload) {
    if (!receivedMessages.offer(payload)) {
      throw new RuntimeException("MapReceiverTest timed out waiting for Kafka message for: "
              + payload);
    }
  }
}

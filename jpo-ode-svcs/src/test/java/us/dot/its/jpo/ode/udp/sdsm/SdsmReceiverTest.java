package us.dot.its.jpo.ode.udp.sdsm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static us.dot.its.jpo.ode.test.utilities.ApprovalTestCase.deserializeTestCases;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.ApprovalTestCase;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

import org.apache.kafka.clients.admin.NewTopic;

@Slf4j
@SpringBootTest(
    classes = {
        OdeKafkaProperties.class,
        UDPReceiverProperties.class,
        KafkaProducerConfig.class,
        SerializationConfig.class,
        TestMetricsConfig.class,
    },
    properties = {"ode.kafka.topics.raw-encoded-json.sdsm=topic.SdsmReceiverTest",
        "ode.receivers.sdsm.receiver-port=12412"}
)
@EnableConfigurationProperties
@ContextConfiguration(classes = {
    UDPReceiverProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class
})
@DirtiesContext
class SdsmReceiverTest {

  private final EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  @Test
  void testSdsmReceiver() throws Exception {
    try {
      embeddedKafka.addTopics(new NewTopic(rawEncodedJsonTopics.getSdsm(), 1, (short) 1));
    } catch (Exception e) {
      // Ignore as we're only ensuring topics exist
    }

    // Set the clock to a fixed time so that the SdsmReceiver will produce the same output every time
    final Clock prevClock = DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), Clock.systemUTC().getZone()));

    SdsmReceiver sdsmReceiver = new SdsmReceiver(udpReceiverProperties.getSdsm(), kafkaTemplate,
        rawEncodedJsonTopics.getSdsm());
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit(sdsmReceiver);

    String fileContent = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiverTest_ValidSdsm.txt"));
    String expected = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiverTest_ValidSdsm_expected.json"));

    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getSdsm().getReceiverPort());
    udpClient.send(fileContent);

    var consumerProps = KafkaTestUtils.consumerProps(
        "SdsmReceiverTest", "true", embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<Integer, String>(consumerProps);
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, rawEncodedJsonTopics.getSdsm());

    var singleRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getSdsm());
    assertNotEquals(expected, singleRecord.value());

    JSONObject producedJson = new JSONObject(singleRecord.value());
    JSONObject expectedJson = new JSONObject(expected);

    assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
        producedJson.getJSONObject("metadata").get("serialId"));
    expectedJson.getJSONObject("metadata").remove("serialId");
    producedJson.getJSONObject("metadata").remove("serialId");

    assertThat(JsonUtils.toJson(producedJson, false), jsonEquals(JsonUtils.toJson(expectedJson, false)));

    DateTimeUtils.setClock(prevClock);
  }
}


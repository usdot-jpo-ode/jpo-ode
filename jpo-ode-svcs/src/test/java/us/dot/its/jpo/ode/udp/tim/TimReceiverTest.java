package us.dot.its.jpo.ode.udp.tim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@SpringBootTest(
    classes = {OdeKafkaProperties.class, UDPReceiverProperties.class, KafkaProducerConfig.class},
    properties = {
        "ode.receivers.tim.receiver-port=15353",
        "ode.kafka.topics.raw-encoded-json.tim=topic.TimReceiverTest"
    }
)
@ContextConfiguration(classes = {
    UDPReceiverProperties.class, OdeKafkaProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class
})
class TimReceiverTest {

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Test
  void testRun() throws Exception {
    try {

      embeddedKafka.addTopics(new NewTopic(rawEncodedJsonTopics.getTim(), 1, (short) 1));
    } catch (Exception e) {
      // ignore because we only care that the topics exist not that they're unique
    }

    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2024-11-26T23:53:21.120Z"), ZoneId.of("UTC")));

    TimReceiver timReceiver = new TimReceiver(udpReceiverProperties.getTim(),
        kafkaTemplate, rawEncodedJsonTopics.getTim());
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit(timReceiver);

    String fileContent =
        Files.readString(Paths.get(
            "src/test/resources/us/dot/its/jpo/ode/udp/tim/TimReceiverTest_ValidTIM.txt"));
    String expected = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/tim/TimReceiverTest_ValidTIM_expected.json"));

    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getTim().getReceiverPort());
    udpClient.send(fileContent);

    var consumerProps = KafkaTestUtils.consumerProps(
        "TimReceiverTest", "true", embeddedKafka);
    DefaultKafkaConsumerFactory<Integer, String> cf =
        new DefaultKafkaConsumerFactory<>(consumerProps);
    Consumer<Integer, String> consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, rawEncodedJsonTopics.getTim());

    var singleRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getTim());
    // confirm the stream-id is different, then remove it from both so that we can test equality of all other fields
    assertNotEquals(expected, singleRecord.value());
    JSONObject producedJson = new JSONObject(singleRecord.value());
    JSONObject expectedJson = new JSONObject(expected);

    // assert that the UUIDs are different, then remove them so that the rest of the JSON can be compared
    assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
        producedJson.getJSONObject("metadata").get("serialId"));
    expectedJson.getJSONObject("metadata").remove("serialId");
    producedJson.getJSONObject("metadata").remove("serialId");

    assertEquals(
        expectedJson.toString(2),
        producedJson.toString(2));
  }
}
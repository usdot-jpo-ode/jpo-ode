package us.dot.its.jpo.ode.udp.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;

@EnableConfigurationProperties
@SpringBootTest(
    classes = {
        OdeKafkaProperties.class,
        UDPReceiverProperties.class,
        KafkaProducerConfig.class,
        SerializationConfig.class
    },
    properties = {
        "ode.receivers.generic.receiver-port=15460",
        "ode.kafka.topics.raw-encoded-json.bsm=topic.GenericReceiverTestBSM",
        "ode.kafka.topics.raw-encoded-json.map=topic.GenericReceiverTestMAP",
        "ode.kafka.topics.raw-encoded-json.psm=topic.GenericReceiverTestPSM",
        "ode.kafka.topics.raw-encoded-json.spat=topic.GenericReceiverTestSPAT",
        "ode.kafka.topics.raw-encoded-json.ssm=topic.GenericReceiverTestSSM",
        "ode.kafka.topics.raw-encoded-json.tim=topic.GenericReceiverTestTIM",
        "ode.kafka.topics.raw-encoded-json.srm=topic.GenericReceiverTestSRM"
    }
)
@ContextConfiguration(classes = {
    UDPReceiverProperties.class, OdeKafkaProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class
})
@DirtiesContext
class GenericReceiverTest {

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Test
  void testRun() throws Exception {
    String[] topics = {
        rawEncodedJsonTopics.getBsm(),
        rawEncodedJsonTopics.getMap(),
        rawEncodedJsonTopics.getPsm(),
        rawEncodedJsonTopics.getSpat(),
        rawEncodedJsonTopics.getSsm(),
        rawEncodedJsonTopics.getTim(),
        rawEncodedJsonTopics.getSrm()
    };
    EmbeddedKafkaHolder.addTopics(topics);

    GenericReceiver genericReceiver = new GenericReceiver(
        udpReceiverProperties.getGeneric(),
        kafkaTemplate, rawEncodedJsonTopics
    );

    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit(genericReceiver);

    TestUDPClient udpClient = new TestUDPClient(
        udpReceiverProperties.getGeneric().getReceiverPort());

    var consumerProps = KafkaTestUtils.consumerProps("GenericReceiverTest", "true", embeddedKafka);
    var cf = new DefaultKafkaConsumerFactory<String, String>(consumerProps);
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(consumer, topics);

    DateTimeUtils.setClock(Clock.fixed(Instant.parse("2024-11-26T23:53:21.120Z"), ZoneOffset.UTC));

    // Test the PSM path
    String psmFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/psm/PsmReceiverTest_ValidPSM.txt"));
    String expectedPsm = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/psm/PsmReceiverTest_ValidPSM_expected.json"));

    udpClient.send(psmFileContent);
    var psmRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getPsm());
    assertExpected("Produced PSM message does not match expected", psmRecord.value(), expectedPsm);

    // Test the BSM path
    String bsmFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM.txt"));
    String expectedBsm = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/bsm/BsmReceiverTest_ValidBSM_expected.json"));
    udpClient.send(bsmFileContent);

    var bsmRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getBsm());
    assertExpected("Produced BSM message does not match expected", bsmRecord.value(), expectedBsm);

    // Test the MAP path
    String mapFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/map/MapReceiverTest_ValidMAP.txt"));
    String expectedMap = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/map/MapReceiverTest_ValidMAP_expected.json"));
    udpClient.send(mapFileContent);

    var mapRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getMap());
    assertExpected("Produced MAP message does not match expected", mapRecord.value(), expectedMap);

    // Test the SPAT path
    String spatFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/spat/SpatReceiverTest_ValidSPAT.txt"));
    String expectedSpat = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/spat/SpatReceiverTest_ValidSPAT_expected.json"));
    udpClient.send(spatFileContent);

    var spatRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getSpat());
    assertExpected("Produced SPAT message does not match expected", spatRecord.value(),
        expectedSpat);

    // Test the SSM path
    String ssmFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/ssm/SsmReceiverTest_ValidSSM.txt"));
    String expectedSsm = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/ssm/SsmReceiverTest_ValidSSM_expected.json"));
    udpClient.send(ssmFileContent);

    var ssmRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getSsm());
    assertExpected("Produced SSM message does not match expected", ssmRecord.value(), expectedSsm);

    // Test the TIM path
    String timFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/tim/TimReceiverTest_ValidTIM.txt"));
    String expectedTim = Files.readString(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/udp/tim/TimReceiverTest_ValidTIM_expected.json"));
    udpClient.send(timFileContent);

    var timRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getTim());
    assertExpected("Produced TIM message does not match expected", timRecord.value(), expectedTim);

    String srmFileContent = Files.readString(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/udp/srm/SrmReceiverTest_ValidData.txt")
    );
    String expectedSrm = Files.readString(
        Paths.get(
            "src/test/resources/us/dot/its/jpo/ode/udp/srm/SrmReceiverTest_ExpectedOutput.json")
    );
    udpClient.send(srmFileContent);

    var srmRecord = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getSrm());
    assertExpected("Produced SRM message does not match expected", srmRecord.value(), expectedSrm);
  }

  private static void assertExpected(String failureMsg, String actual, String expected) {
    JSONObject producedJson = new JSONObject(actual);
    JSONObject expectedJson = new JSONObject(expected);

    assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"),
        producedJson.getJSONObject("metadata").get("serialId"));
    expectedJson.getJSONObject("metadata").remove("serialId");
    producedJson.getJSONObject("metadata").remove("serialId");

    assertEquals(expectedJson.toString(2), producedJson.toString(2), failureMsg);
  }
}
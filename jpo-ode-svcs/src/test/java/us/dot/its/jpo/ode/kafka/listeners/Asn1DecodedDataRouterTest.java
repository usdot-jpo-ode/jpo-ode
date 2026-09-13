package us.dot.its.jpo.ode.kafka.listeners;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.kafka.listeners.asn1.Asn1DecodedDataRouter;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.JsonUtils;

@Slf4j
@SpringBootTest(
    classes = {KafkaProperties.class, JsonTopics.class, Asn1CoderTopics.class,
        KafkaConsumerConfig.class, KafkaProducerConfig.class, RawEncodedJsonTopics.class,
        Asn1CoderTopics.class, OdeKafkaProperties.class, Asn1DecodedDataRouter.class,
        SerializationConfig.class, TestMetricsConfig.class, UDPReceiverProperties.class},
    properties = {"ode.kafka.disabled-topics="})
@EnableConfigurationProperties
@DirtiesContext
@EmbeddedKafka
@TestPropertySource(properties = "logging.level.org.springframework.kafka=DEBUG")
class Asn1DecodedDataRouterTest {

  @Autowired
  EmbeddedKafkaBroker embeddedKafka;
  @Autowired
  KafkaTemplate<String, String> kafkaStringTemplate;
  @Autowired
  JsonTopics jsonTopics;
  @Autowired
  Asn1CoderTopics asn1CoderTopics;
  @Autowired
  private XmlMapper simpleXmlMapper;

  ObjectMapper mapper = new ObjectMapper();

  @Test
  void testAsn1DecodedDataRouterBSMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getBsm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-bsm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "bsmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedBsm = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-bsm.json");
    for (String recordType : new String[] {"bsmTx", "rxMsg", "bsmLogDuringEvent"}) {
      String inputData = replaceRecordType(baseTestData, "bsmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedBsm = replaceJSONRecordType(baseExpectedBsm, "bsmTx", recordType);

      OdeMessageFrameData expectedBsmMFrameData =
          mapper.readValue(expectedBsm, OdeMessageFrameData.class);
      switch (recordType) {
        case "bsmTx" -> {
          expectedBsmMFrameData.getMetadata().setRecordType(RecordType.bsmTx);
        }
        case "rxMsg" -> {
          expectedBsmMFrameData.getMetadata().setRecordType(RecordType.rxMsg);
        }
        case "bsmLogDuringEvent" -> {
          expectedBsmMFrameData.getMetadata().setRecordType(RecordType.bsmLogDuringEvent);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedBsm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getBsm());
      OdeMessageFrameData consumedBsmMFrameData =
          mapper.readValue(consumedBsm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedBsmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedBsmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Disabled("466943")
  @Test
  void testAsn1DecodedDataRouterTIMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getTim());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-tim.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "timDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedTim = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-tim.json");
    for (String recordType : new String[] {"timMsg", "rxMsg"}) {
      String inputData = replaceRecordType(baseTestData, "timMsg", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedTim = replaceJSONRecordType(baseExpectedTim, "timMsg", recordType);

      OdeMessageFrameData expectedTimMFrameData =
          mapper.readValue(expectedTim, OdeMessageFrameData.class);
      switch (recordType) {
        case "timMsg" -> {
          expectedTimMFrameData.getMetadata().setRecordType(RecordType.timMsg);
        }
        case "rxMsg" -> {
          expectedTimMFrameData.getMetadata().setRecordType(RecordType.rxMsg);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedTim = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getTim());
      OdeMessageFrameData consumedTimMFrameData =
          mapper.readValue(consumedTim.value(), OdeMessageFrameData.class);

      String actualMF = JsonUtils.toJson(consumedTimMFrameData, false);
      String expectedMF = JsonUtils.toJson(expectedTimMFrameData, false);

      assertThat(actualMF, jsonEquals(expectedMF).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_SPaTDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getSpat());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-spat.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "spatDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedSpat =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-spat.json");
    for (String recordType : new String[] {"spatTx", "rxMsg"}) {
      String inputData = replaceRecordType(baseTestData, "spatTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedSpat = replaceJSONRecordType(baseExpectedSpat, "spatTx", recordType);

      OdeMessageFrameData expectedSpatMFrameData =
          mapper.readValue(expectedSpat, OdeMessageFrameData.class);
      switch (recordType) {
        case "spatTx" -> {
          expectedSpatMFrameData.getMetadata().setRecordType(RecordType.spatTx);
        }
        case "rxMsg" -> {
          expectedSpatMFrameData.getMetadata().setRecordType(RecordType.rxMsg);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedSpat = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getSpat());
      OdeMessageFrameData consumedSpatMFrameData =
          mapper.readValue(consumedSpat.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedSpatMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedSpatMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_SSMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getSsm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-ssm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "ssmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedSsm = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-ssm.json");
    for (String recordType : new String[] {"ssmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "ssmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedSsm = replaceJSONRecordType(baseExpectedSsm, "ssmTx", recordType);

      OdeMessageFrameData expectedSsmMFrameData =
          mapper.readValue(expectedSsm, OdeMessageFrameData.class);
      switch (recordType) {
        case "ssmTx" -> {
          expectedSsmMFrameData.getMetadata().setRecordType(RecordType.ssmTx);
        }
        case "unsupported" -> {
          expectedSsmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedSsm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getSsm());
      OdeMessageFrameData consumedSsmMFrameData =
          mapper.readValue(consumedSsm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedSsmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedSsmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_SRMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getSrm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-srm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "srmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedSrm = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-srm.json");
    for (String recordType : new String[] {"srmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "srmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedSrm = replaceJSONRecordType(baseExpectedSrm, "srmTx", recordType);

      OdeMessageFrameData expectedSrmMFrameData =
          mapper.readValue(expectedSrm, OdeMessageFrameData.class);
      switch (recordType) {
        case "srmTx" -> {
          expectedSrmMFrameData.getMetadata().setRecordType(RecordType.srmTx);
        }
        case "unsupported" -> {
          expectedSrmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedSrm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getSrm());
      OdeMessageFrameData consumedSrmMFrameData =
          mapper.readValue(consumedSrm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedSrmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedSrmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_PSMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getPsm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-psm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "psmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedPsm = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-psm.json");
    for (String recordType : new String[] {"psmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "psmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedPsm = replaceJSONRecordType(baseExpectedPsm, "psmTx", recordType);

      OdeMessageFrameData expectedPsmMFrameData =
          mapper.readValue(expectedPsm, OdeMessageFrameData.class);
      switch (recordType) {
        case "psmTx" -> {
          expectedPsmMFrameData.getMetadata().setRecordType(RecordType.psmTx);
        }
        case "unsupported" -> {
          expectedPsmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedPsm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getPsm());
      OdeMessageFrameData consumedPsmMFrameData =
          mapper.readValue(consumedPsm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedPsmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedPsmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_MAPDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getMap());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-map.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "mapDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedMap = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-map.json");
    for (String recordType : new String[] {"mapTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "mapTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedMap = replaceJSONRecordType(baseExpectedMap, "mapTx", recordType);

      OdeMessageFrameData expectedMapMFrameData =
          mapper.readValue(expectedMap, OdeMessageFrameData.class);
      switch (recordType) {
        case "mapTx" -> {
          expectedMapMFrameData.getMetadata().setRecordType(RecordType.mapTx);
        }
        case "unsupported" -> {
          expectedMapMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedMap = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getMap());
      OdeMessageFrameData consumedMapMFrameData =
          mapper.readValue(consumedMap.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedMapMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedMapMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_SDSMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getSdsm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-sdsm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "sdsmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedSdsm =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-sdsm.json");
    for (String recordType : new String[] {"sdsmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "sdsmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedSdsm = replaceJSONRecordType(baseExpectedSdsm, "sdsmTx", recordType);

      OdeMessageFrameData expectedSdsmMFrameData =
          mapper.readValue(expectedSdsm, OdeMessageFrameData.class);
      switch (recordType) {
        case "sdsmTx" -> {
          expectedSdsmMFrameData.getMetadata().setRecordType(RecordType.sdsmTx);
        }
        case "unsupported" -> {
          expectedSdsmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedSdsm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getSdsm());
      OdeMessageFrameData consumedSdsmMFrameData =
          mapper.readValue(consumedSdsm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedSdsmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedSdsmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_RTCMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getRtcm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-rtcm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "rtcmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedRtcm =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-rtcm.json");
    for (String recordType : new String[] {"rtcmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "rtcmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedRtcm = replaceJSONRecordType(baseExpectedRtcm, "rtcmTx", recordType);

      OdeMessageFrameData expectedRtcmMFrameData =
          mapper.readValue(expectedRtcm, OdeMessageFrameData.class);
      switch (recordType) {
        case "rtcmTx" -> {
          expectedRtcmMFrameData.getMetadata().setRecordType(RecordType.rtcmTx);
        }
        case "unsupported" -> {
          expectedRtcmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedRtcm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getRtcm());
      OdeMessageFrameData consumedRtcmMFrameData =
          mapper.readValue(consumedRtcm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedRtcmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedRtcmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouter_RSMDataFlow() throws IOException {
    String[] topics = Arrays.array(jsonTopics.getRsm());
    embeddedKafka.addTopics(topics);

    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-rsm.xml");

    var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "rsmDecoderTest", false);
    var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromEmbeddedTopics(testConsumer, topics);

    String baseExpectedRsm = loadFromResource("us/dot/its/jpo/ode/services/asn1/expected-rsm.json");
    for (String recordType : new String[] {"rsmTx", "unsupported"}) {
      String inputData = replaceRecordType(baseTestData, "rsmTx", recordType);
      var uniqueKey = UUID.randomUUID().toString();
      kafkaStringTemplate.send(asn1CoderTopics.getDecoderOutput(), uniqueKey, inputData);

      var expectedRsm = replaceJSONRecordType(baseExpectedRsm, "rsmTx", recordType);

      OdeMessageFrameData expectedRsmMFrameData =
          mapper.readValue(expectedRsm, OdeMessageFrameData.class);
      switch (recordType) {
        case "rsmTx" -> {
          expectedRsmMFrameData.getMetadata().setRecordType(RecordType.rsmTx);
        }
        case "unsupported" -> {
          expectedRsmMFrameData.getMetadata().setRecordType(RecordType.unsupported);
        }
        default -> throw new IllegalStateException("Unexpected value: " + recordType);
      }

      var consumedRsm = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getRsm());
      OdeMessageFrameData consumedRsmMFrameData =
          mapper.readValue(consumedRsm.value(), OdeMessageFrameData.class);

      assertThat(JsonUtils.toJson(consumedRsmMFrameData, false),
          jsonEquals(JsonUtils.toJson(expectedRsmMFrameData, false)).withTolerance(0.0001));
    }
    testConsumer.close();
  }

  @Test
  void testAsn1DecodedDataRouterException() {
    String baseTestData =
        loadFromResource("us/dot/its/jpo/ode/services/asn1/decoder-output-failed-encoding.xml");

    var uniqueKey = UUID.randomUUID().toString();
    ConsumerRecord<String, String> consumedRecord =
        new ConsumerRecord<>(asn1CoderTopics.getDecoderOutput(), 0, 0L, uniqueKey, baseTestData);

    Asn1DecodedDataRouter router =
        new Asn1DecodedDataRouter(kafkaStringTemplate, jsonTopics, simpleXmlMapper);

    Exception exception =
        assertThrows(Asn1DecodedDataRouter.Asn1DecodedDataRouterException.class, () -> {
          router.listen(consumedRecord);
        });

    assertEquals("Error processing decoded message with code INVALID_DATA_TYPE_ERROR and message "
        + "failed ASN.1 binary decoding of element MessageFrame: more data expected. Successfully decoded 0 bytes.",
        exception.getMessage());
  }

  private String loadFromResource(String resourcePath) {
    String baseTestData;
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new FileNotFoundException("Resource not found: " + resourcePath);
      }
      baseTestData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load test data", e);
    }
    return baseTestData;
  }

  private String replaceRecordType(String testData, String curRecordType, String recordType) {
    return testData.replace("<recordType>" + curRecordType + "</recordType>",
        "<recordType>" + recordType + "</recordType>");
  }

  private String replaceJSONRecordType(String testData, String curRecordType, String recordType) {
    return testData.replace("\"recordType\":\"" + curRecordType + "\"",
        "\"recordType\":\"" + recordType + "\"");
  }
}

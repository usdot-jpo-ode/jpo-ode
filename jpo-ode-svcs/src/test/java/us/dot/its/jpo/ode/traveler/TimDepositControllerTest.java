/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.traveler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import mockit.Capturing;
import mockit.Expectations;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;


@EnableConfigurationProperties
@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaConsumerConfig.class,
    OdeKafkaProperties.class, Asn1CoderTopics.class, PojoTopics.class, JsonTopics.class,
    SecurityServicesProperties.class, KafkaProperties.class, TimIngestTrackerProperties.class,
    XmlMapper.class}, properties = {"ode.kafka.brokers=localhost:4242"})
@ContextConfiguration(classes = {TimDepositController.class, Asn1CoderTopics.class,
    PojoTopics.class, JsonTopics.class, TimIngestTrackerProperties.class,
    SecurityServicesProperties.class, OdeKafkaProperties.class})
@DirtiesContext
class TimDepositControllerTest {

  @Autowired
  OdeKafkaProperties odeKafkaProperties;

  @Autowired
  Asn1CoderTopics asn1CoderTopics;

  @Autowired
  PojoTopics pojoTopics;

  @Autowired
  JsonTopics jsonTopics;

  @Autowired
  TimIngestTrackerProperties timIngestTrackerProperties;

  @Autowired
  SecurityServicesProperties securityServicesProperties;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  KafkaTemplate<String, OdeObject> timDataKafkaTemplate;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  int consumerCount = 0;

  @Test
  void nullRequestShouldReturnEmptyError() {
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(null);
    Assertions.assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
  }

  @Test
  void emptyRequestShouldReturnEmptyError() {
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    ResponseEntity<String> actualResponse = testTimDepositController.postTim("");
    Assertions.assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
  }

  @Test
  void invalidJsonSyntaxShouldReturnJsonSyntaxError() {
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"in\"va}}}on\"}}");
    Assertions.assertEquals("{\"error\":\"Malformed or non-compliant JSON syntax.\"}",
        actualResponse.getBody());
  }

  @Test
  void missingRequestElementShouldReturnMissingRequestError() {
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"tim\":{}}");
    Assertions.assertEquals(
        "{\"error\":\"Missing or invalid argument: Request element is required as of version 3.\"}",
        actualResponse.getBody());
  }

  @Test
  void invalidTimestampShouldReturnInvalidTimestampError() {
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(
        "{\"request\":{},\"tim\":{\"timeStamp\":\"201-03-13T01:07:11-05:00\"}}");
    Assertions.assertEquals(
        "{\"error\":\"Invalid timestamp in tim record: 201-03-13T01:07:11-05:00\"}",
        actualResponse.getBody());
  }

  @Test
  void messageWithNoRSUsOrSDWShouldReturnWarning() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast("test.messageWithNoRSUsOrSDWShouldReturnWarning.timBroadcast.pojo");
    jsonTopics.setTimBroadcast("test.messageWithNoRSUsOrSDWShouldReturnWarning.timBroadcast.json");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody = "{\"request\":{},\"tim\":{\"timeStamp\":\"2018-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody =
        "{\"warning\":\"Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to broadcast streams.\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    var pojoConsumer = createStr2OdeObjConsumer();
    var stringConsumer = createStr2StrConsumer();

    // verify POJO TimBroadcast message
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoConsumer, pojoTopics.getTimBroadcast());
    var singlePojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(singlePojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    embeddedKafka.consumeFromAnEmbeddedTopic(stringConsumer, jsonTopics.getTimBroadcast());
    var singleJsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(stringConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(singleJsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(
        loadTestResource("messageWithNoRSUsOrSDWShouldReturnWarning_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // cleanup
    stringConsumer.close();
    pojoConsumer.close();
  }

  @Test
  void failedObjectNodeConversionShouldReturnConvertingError(@Capturing
                                                             TravelerMessageFromHumanToAsnConverter capturingTravelerMessageFromHumanToAsnConverter)
      throws JsonUtilsException, TravelerMessageFromHumanToAsnConverter.NoncompliantFieldsException,
      IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast(
        "test.failedObjectNodeConversionShouldReturnConvertingError.timBroadcast.pojo");
    jsonTopics.setTimBroadcast(
        "test.failedObjectNodeConversionShouldReturnConvertingError.timBroadcast.json");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    new Expectations() {

      {
        TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(
            (JsonNode) any);
        result = new JsonUtilsException("testException123", null);
      }
    };
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody =
        "{\"error\":\"Error converting to encodable TravelerInputData.\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoConsumer, pojoTopics.getTimBroadcast());
    var singlePojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(singlePojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var stringConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(stringConsumer, jsonTopics.getTimBroadcast());
    var singleJsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(stringConsumer, jsonTopics.getTimBroadcast());
    Assertions.assertNotNull(singleJsonTimBroadcastRecord.value());
    var actualJson = new JSONObject(singleJsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(loadTestResource(
        "failedObjectNodeConversionShouldReturnConvertingError_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // cleanup
    stringConsumer.close();
    pojoConsumer.close();
  }

  @Test
  void failedXmlConversionShouldReturnConversionError(
      @Capturing TimTransmogrifier capturingTimTransmogrifier)
      throws XmlUtils.XmlUtilsException, JsonUtilsException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast(
        "test.failedXmlConversionShouldReturnConversionError.timBroadcast.pojo");
    jsonTopics.setTimBroadcast(
        "test.failedXmlConversionShouldReturnConversionError.timBroadcast.json");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);

    new Expectations() {
      {
        TimTransmogrifier.obfuscateRsuPassword((String) any);
        result = "timWithObfuscatedPassword";
      }

      {
        TimTransmogrifier.convertToXml((DdsAdvisorySituationData) any, (ObjectNode) any,
            (OdeMsgMetadata) any, (SerialId) any);
        result = new XmlUtils.XmlUtilsException("testException123", null);
      }
    };
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody =
        "{\"error\":\"Error sending data to ASN.1 Encoder module: testException123\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoConsumer, pojoTopics.getTimBroadcast());
    var singlePojoTimBroadcastMessage =
        KafkaTestUtils.getSingleRecord(pojoConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(singlePojoTimBroadcastMessage.value());

    // verify JSON tim broadcast message
    var stringConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(stringConsumer, jsonTopics.getTimBroadcast());
    var singleJsonTimBroadcastMessage =
        KafkaTestUtils.getSingleRecord(stringConsumer, jsonTopics.getTimBroadcast());
    Assertions.assertEquals("timWithObfuscatedPassword", singleJsonTimBroadcastMessage.value());

    // cleanup
    stringConsumer.close();
    pojoConsumer.close();
  }

  @Test
  void testSuccessfulMessageReturnsSuccessMessagePost() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast("test.successfulMessageReturnsSuccessMessagePost.timBroadcast.pojo");
    jsonTopics.setTimBroadcast("test.successfulMessageReturnsSuccessMessagePost.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast(
        "test.successfulMessageReturnsSuccessMessagePost.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePost.tim.json");
    asn1CoderTopics.setEncoderInput("test.successfulMessageReturnsSuccessMessagePost.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(
        loadTestResource("successfulMessageReturnsSuccessMessagePost_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(loadTestResource(
        "successfulMessageReturnsSuccessMessagePost_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson = new JSONObject(
        loadTestResource("successfulMessageReturnsSuccessMessagePost_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml =
        loadTestResource("successfulMessageReturnsSuccessMessagePost_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  @Test
  void testSuccessfulSdwRequestMessageReturnsSuccessMessagePost() throws Exception {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast(
        "test.successfulSdwRequestMessageReturnsSuccessMessagePost.timBroadcast.pojo");
    jsonTopics.setTimBroadcast(
        "test.successfulSdwRequestMessageReturnsSuccessMessagePost.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast(
        "test.successfulSdwRequestMessageReturnsSuccessMessagePost.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulSdwRequestMessageReturnsSuccessMessagePost.tim.json");
    asn1CoderTopics.setEncoderInput(
        "test.successfulSdwRequestMessageReturnsSuccessMessagePost.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String file = "/sdwRequest.json";
    String requestBody =
        IOUtils.toString(TimDepositControllerTest.class.getResourceAsStream(file), "UTF-8");

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(loadTestResource(
        "successfulSdwRequestMessageReturnsSuccessMessagePost_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(loadTestResource(
        "successfulSdwRequestMessageReturnsSuccessMessagePost_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson = new JSONObject(
        loadTestResource("successfulSdwRequestMessageReturnsSuccessMessagePost_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml = loadTestResource(
        "successfulSdwRequestMessageReturnsSuccessMessagePost_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  @Test
  void testSuccessfulMessageReturnsSuccessMessagePostWithOde() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast(
        "test.successfulMessageReturnsSuccessMessagePostWithOde.timBroadcast.pojo");
    jsonTopics.setTimBroadcast(
        "test.successfulMessageReturnsSuccessMessagePostWithOde.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast(
        "test.successfulMessageReturnsSuccessMessagePostWithOde.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePostWithOde.tim.json");
    asn1CoderTopics.setEncoderInput(
        "test.successfulMessageReturnsSuccessMessagePostWithOde.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\":{\"ode\":{},\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(loadTestResource(
        "successfulMessageReturnsSuccessMessagePostWithOde_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(loadTestResource(
        "successfulMessageReturnsSuccessMessagePostWithOde_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson = new JSONObject(
        loadTestResource("successfulMessageReturnsSuccessMessagePostWithOde_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml = loadTestResource(
        "successfulMessageReturnsSuccessMessagePostWithOde_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  @Test
  void testSuccessfulMessageReturnsSuccessMessagePut() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast("test.successfulMessageReturnsSuccessMessagePut.timBroadcast.pojo");
    jsonTopics.setTimBroadcast("test.successfulMessageReturnsSuccessMessagePut.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast(
        "test.successfulMessageReturnsSuccessMessagePut.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePut.tim.json");
    asn1CoderTopics.setEncoderInput("test.successfulMessageReturnsSuccessMessagePut.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.putTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(
        loadTestResource("successfulMessageReturnsSuccessMessagePut_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(loadTestResource(
        "successfulMessageReturnsSuccessMessagePut_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson = new JSONObject(
        loadTestResource("successfulMessageReturnsSuccessMessagePut_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml =
        loadTestResource("successfulMessageReturnsSuccessMessagePut_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  @Test
  void testDepositingTimWithExtraProperties() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast("test.depositingTimWithExtraProperties.timBroadcast.pojo");
    jsonTopics.setTimBroadcast("test.depositingTimWithExtraProperties.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast("test.depositingTimWithExtraProperties.j2735TimBroadcast.json");
    jsonTopics.setTim("test.depositingTimWithExtraProperties.tim.json");
    asn1CoderTopics.setEncoderInput("test.depositingTimWithExtraProperties.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{},\"randomProp1\":true,\"randomProp2\":\"hello world\"},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"randomProp3\":123,\"randomProp4\":{\"nestedProp1\":\"foo\",\"nestedProp2\":\"bar\"}}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());


    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(
        loadTestResource("depositingTimWithExtraProperties_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(
        loadTestResource("depositingTimWithExtraProperties_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson =
        new JSONObject(loadTestResource("depositingTimWithExtraProperties_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml =
        loadTestResource("depositingTimWithExtraProperties_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  @Test
  void testSuccessfulTimIngestIsTracked() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast("test.successfulTimIngestIsTracked.timBroadcast.pojo");
    jsonTopics.setTimBroadcast("test.successfulTimIngestIsTracked.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast("test.successfulTimIngestIsTracked.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulTimIngestIsTracked.tim.json");
    asn1CoderTopics.setEncoderInput("test.successfulTimIngestIsTracked.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\":{\"rsus\":[],\"snmp\":{},\"randomProp1\":true,\"randomProp2\":\"hello world\"},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"randomProp3\":123,\"randomProp4\":{\"nestedProp1\":\"foo\",\"nestedProp2\":\"bar\"}}}";
    long priorIngestCount = TimIngestTracker.getInstance().getTotalMessagesReceived();

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
    Assertions.assertEquals(priorIngestCount + 1,
        TimIngestTracker.getInstance().getTotalMessagesReceived());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson =
        new JSONObject(loadTestResource("successfulTimIngestIsTracked_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(
        loadTestResource("successfulTimIngestIsTracked_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson =
        new JSONObject(loadTestResource("successfulTimIngestIsTracked_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml = loadTestResource("successfulTimIngestIsTracked_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  // This serves as an integration test without mocking the TimTransmogrifier and XmlUtils
  @Test
  void testSuccessfulRsuMessageReturnsSuccessMessagePost() throws IOException {
    // prepare
    odeKafkaProperties.setDisabledTopics(Set.of());
    pojoTopics.setTimBroadcast(
        "test.successfulRsuMessageReturnsSuccessMessagePost.timBroadcast.pojo");
    jsonTopics.setTimBroadcast(
        "test.successfulRsuMessageReturnsSuccessMessagePost.timBroadcast.json");
    jsonTopics.setJ2735TimBroadcast(
        "test.successfulRsuMessageReturnsSuccessMessagePost.j2735TimBroadcast.json");
    jsonTopics.setTim("test.successfulRsuMessageReturnsSuccessMessagePost.tim.json");
    asn1CoderTopics.setEncoderInput(
        "test.successfulRsuMessageReturnsSuccessMessagePost.encoderInput");
    EmbeddedKafkaHolder.addTopics(pojoTopics.getTimBroadcast(), jsonTopics.getTimBroadcast(),
        jsonTopics.getJ2735TimBroadcast(), jsonTopics.getTim(), asn1CoderTopics.getEncoderInput());
    DateTimeUtils.setClock(
        Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
    TimDepositController testTimDepositController =
        new TimDepositController(asn1CoderTopics, pojoTopics, jsonTopics,
            timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
            timDataKafkaTemplate);
    String requestBody =
        "{\"request\": {\"rsus\": [{\"latitude\": 30.123456, \"longitude\": -100.12345, \"rsuId\": 123, \"route\": \"myroute\", \"milepost\": 10, \"rsuTarget\": \"172.0.0.1\", \"rsuRetries\": 3, \"rsuTimeout\": 5000, \"rsuIndex\": 7, \"rsuUsername\": \"myusername\", \"rsuPassword\": \"mypassword\"}], \"snmp\": {\"rsuid\": \"83\", \"msgid\": 31, \"mode\": 1, \"channel\": 183, \"interval\": 2000, \"deliverystart\": \"2024-05-13T14:30:00Z\", \"deliverystop\": \"2024-05-13T22:30:00Z\", \"enable\": 1, \"status\": 4}}, \"tim\": {\"msgCnt\": \"1\", \"timeStamp\": \"2024-05-10T19:01:22Z\", \"packetID\": \"123451234512345123\", \"urlB\": \"null\", \"dataframes\": [{\"startDateTime\": \"2024-05-13T20:30:05.014Z\", \"durationTime\": \"30\", \"doNotUse1\": 0, \"frameType\": \"advisory\", \"msgId\": {\"roadSignID\": {\"mutcdCode\": \"warning\", \"viewAngle\": \"1111111111111111\", \"position\": {\"latitude\": 30.123456, \"longitude\": -100.12345}}}, \"priority\": \"5\", \"doNotUse2\": 0, \"regions\": [{\"name\": \"I_myroute_RSU_172.0.0.1\", \"anchorPosition\": {\"latitude\": 30.123456, \"longitude\": -100.12345}, \"laneWidth\": \"50\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": {\"scale\": 0, \"nodes\": [{\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}, {\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}], \"type\": \"ll\"}, \"direction\": \"0000000000010000\"}], \"doNotUse4\": 0, \"doNotUse3\": 0, \"content\": \"workZone\", \"items\": [\"771\"], \"url\": \"null\"}]}}";

    // execute
    ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

    // verify
    String expectedResponseBody = "{\"success\":\"true\"}";
    Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());

    // verify POJO tim broadcast message
    var pojoTimBroadcastConsumer = createStr2OdeObjConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(pojoTimBroadcastConsumer,
        pojoTopics.getTimBroadcast());
    var pojoTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(pojoTimBroadcastConsumer, pojoTopics.getTimBroadcast());
    Assertions.assertNotNull(pojoTimBroadcastRecord.value());

    // verify JSON tim broadcast message
    var jsonTimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimBroadcastConsumer,
        jsonTopics.getTimBroadcast());
    var jsonTimBroadcastRecord =
        KafkaTestUtils.getSingleRecord(jsonTimBroadcastConsumer, jsonTopics.getTimBroadcast());
    var actualJson = new JSONObject(jsonTimBroadcastRecord.value());
    var expectedJson = new JSONObject(loadTestResource(
        "successfulRsuMessageReturnsSuccessMessagePost_timBroadcast_expected.json"));
    String actualStreamId = getStreamId(actualJson);
    String expectedStreamId = getStreamId(expectedJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJson);
    removeStreamId(expectedJson);
    Assertions.assertEquals(expectedJson.toString(2), actualJson.toString(2));

    // verify JSON J2735 tim broadcast message
    var jsonJ2735TimBroadcastConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var jsonJ2735TimBroadcastRecord = KafkaTestUtils.getSingleRecord(jsonJ2735TimBroadcastConsumer,
        jsonTopics.getJ2735TimBroadcast());
    var actualJ2735Json = new JSONObject(jsonJ2735TimBroadcastRecord.value());
    var expectedJ2735Json = new JSONObject(loadTestResource(
        "successfulRsuMessageReturnsSuccessMessagePost_j2735TimBroadcast_expected.json"));
    actualStreamId = getStreamId(actualJ2735Json);
    expectedStreamId = getStreamId(expectedJ2735Json);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualJ2735Json);
    removeStreamId(expectedJ2735Json);
    Assertions.assertEquals(expectedJ2735Json.toString(2), actualJ2735Json.toString(2));

    // verify JSON tim message
    var jsonTimConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
    var actualTimJson = new JSONObject(jsonTimRecord.value());
    var expectedTimJson = new JSONObject(
        loadTestResource("successfulRsuMessageReturnsSuccessMessagePost_tim_expected.json"));
    actualStreamId = getStreamId(actualTimJson);
    expectedStreamId = getStreamId(expectedTimJson);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    removeStreamId(actualTimJson);
    removeStreamId(expectedTimJson);
    Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));

    // verify ASN.1 coder encoder input message
    var asn1CoderEncoderInputConsumer = createStr2StrConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
        asn1CoderTopics.getEncoderInput());
    var actualXml = asn1CoderEncoderInputRecord.value();
    var expectedXml =
        loadTestResource("successfulRsuMessageReturnsSuccessMessagePost_encoderInput_expected.xml");
    actualStreamId = getStreamId(actualXml);
    expectedStreamId = getStreamId(expectedXml);
    Assertions.assertNotEquals(expectedStreamId, actualStreamId);
    actualXml = removeStreamId(actualXml, actualStreamId);
    expectedXml = removeStreamId(expectedXml, expectedStreamId);
    Assertions.assertEquals(expectedXml, actualXml);

    // cleanup
    pojoTimBroadcastConsumer.close();
    jsonTimBroadcastConsumer.close();
    jsonJ2735TimBroadcastConsumer.close();
    jsonTimConsumer.close();
    asn1CoderEncoderInputConsumer.close();
  }

  /**
   * Helper method to create a consumer for OdeObject messages with String keys.
   *
   * @return a consumer for OdeObject messages
   */
  private Consumer<String, OdeObject> createStr2OdeObjConsumer() {
    consumerCount++;
    var consumerProps =
        KafkaTestUtils.consumerProps("TimDepositControllerTest", "true", embeddedKafka);
    DefaultKafkaConsumerFactory<String, OdeObject> pojoConsumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerProps);
    pojoConsumerFactory.setKeyDeserializer(new StringDeserializer());
    return pojoConsumerFactory.createConsumer(String.format("groupid%d", consumerCount),
        String.format("clientidsuffix%d", consumerCount));
  }

  /**
   * Helper method to create a consumer for String messages with String keys.
   */
  private Consumer<String, String> createStr2StrConsumer() {
    consumerCount++;
    var consumerProps =
        KafkaTestUtils.consumerProps("TimDepositControllerTest", "true", embeddedKafka);
    DefaultKafkaConsumerFactory<String, String> stringConsumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
            new StringDeserializer());
    return stringConsumerFactory.createConsumer(String.format("groupid%d", consumerCount),
        String.format("clientidsuffix%d", consumerCount));
  }

  /**
   * Helper method to load the contents of a test resource file as a string.
   *
   * @param resourceName the name of the test resource file
   * @return the contents of the test resource file as a string
   * @throws IOException if an I/O error occurs
   */
  private String loadTestResource(String resourceName) throws IOException {
    String baseDirectory = "src/test/resources/us/dot/its/jpo/ode/traveler/";
    return new String(Files.readAllBytes(Paths.get(baseDirectory + resourceName)));
  }

  /**
   * Helper method to retrieve the stream id from a JSON object.
   *
   * @param jsonObject the JSON object
   * @return the stream id
   */
  private static String getStreamId(JSONObject jsonObject) {
    return jsonObject.getJSONObject("metadata").getJSONObject("serialId").getString("streamId");
  }

  /**
   * Helper method to retrieve the stream id from an XML string.
   *
   * @param xmlString the XML string
   * @return the stream id
   */
  private static String getStreamId(String xmlString) {
    return xmlString.substring(xmlString.indexOf("<streamId>"),
        xmlString.indexOf("</streamId>") + "</streamId>".length());
  }

  /**
   * Helper method to remove the stream id from a JSON object.
   *
   * @param jsonObject the JSON object
   */
  private static void removeStreamId(JSONObject jsonObject) {
    jsonObject.getJSONObject("metadata").getJSONObject("serialId").remove("streamId");
  }

  /**
   * Helper method to remove the stream id from an XML string.
   *
   * @param xmlString the XML string
   * @return the XML string with the stream id removed
   */
  private static String removeStreamId(String xmlString, String streamId) {
    return xmlString.replace(streamId, "");
  }

}
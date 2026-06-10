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
 import org.apache.commons.io.IOUtils;
 import org.apache.kafka.clients.consumer.Consumer;
 import org.apache.kafka.common.serialization.StringDeserializer;
 import org.json.JSONObject;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.Test;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.mockStatic;
 import org.mockito.MockedStatic;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
 import org.springframework.boot.context.properties.EnableConfigurationProperties;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.http.ResponseEntity;
 import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.kafka.test.EmbeddedKafkaBroker;
 import org.springframework.kafka.test.context.EmbeddedKafka;
 import org.springframework.kafka.test.utils.KafkaTestUtils;
 import org.springframework.test.annotation.DirtiesContext;
 import org.springframework.test.context.ContextConfiguration;
 import org.springframework.test.context.TestPropertySource;
 import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
 import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
 import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
 import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
 import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
 import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
 import us.dot.its.jpo.ode.model.OdeMsgMetadata;
 import us.dot.its.jpo.ode.model.SerialId;
 import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
 import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
 import us.dot.its.jpo.ode.security.SecurityServicesProperties;
 import us.dot.its.jpo.ode.util.DateTimeUtils;
 import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
 import us.dot.its.jpo.ode.util.XmlUtils;

 @EnableConfigurationProperties
 @SpringBootTest(classes = {KafkaProducerConfig.class, KafkaConsumerConfig.class,
     OdeKafkaProperties.class, Asn1CoderTopics.class, JsonTopics.class,
     SecurityServicesProperties.class, KafkaProperties.class, TimIngestTrackerProperties.class,
     XmlMapper.class, TestMetricsConfig.class, TimDepositController.class}, properties = {"ode.kafka.brokers=localhost:4242"})
@EmbeddedKafka
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "ode.kafka.brokers=${spring.embedded.kafka.brokers}"})
 @DirtiesContext
 class TimDepositControllerTest {
 
   @Autowired
   OdeKafkaProperties odeKafkaProperties;
 
   @Autowired
   Asn1CoderTopics asn1CoderTopics;
 
   @Autowired
   JsonTopics jsonTopics;
 
   @Autowired
   TimIngestTrackerProperties timIngestTrackerProperties;
 
   @Autowired
   SecurityServicesProperties securityServicesProperties;
 
   @Autowired
   KafkaTemplate<String, String> kafkaTemplate;

   @Autowired
   private XmlMapper simpleXmlMapper;

   @Autowired
   EmbeddedKafkaBroker embeddedKafka;

   @Test
   void nullRequestShouldReturnEmptyError() {
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(null);
     Assertions.assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
   }
 
   @Test
   void emptyRequestShouldReturnEmptyError() {
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     ResponseEntity<String> actualResponse = testTimDepositController.postTim("");
     Assertions.assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
   }
 
   @Test
   void invalidJsonSyntaxShouldReturnJsonSyntaxError() {
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"in\"va}}}on\"}}");
     Assertions.assertEquals("{\"error\":\"Malformed or non-compliant JSON syntax.\"}",
         actualResponse.getBody());
   }
 
   @Test
   void missingRequestElementShouldReturnMissingRequestError() {
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"tim\":{}}");
     Assertions.assertEquals(
         "{\"error\":\"Missing or invalid argument: Request element is required as of version 3.\"}",
         actualResponse.getBody());
   }
 
   @Test
   void invalidTimestampShouldReturnInvalidTimestampError() {
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(
         "{\"request\":{\"ode\":{},\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"201-03-13T01:07:11-05:00\"}}");
     // verify
     Assertions.assertEquals(
         "{\"error\":\"Invalid timestamp in tim record: 201-03-13T01:07:11-05:00\"}",
         actualResponse.getBody());
   }
 
   @Test
   void messageWithNoRSUsOrSDWShouldReturnWarning() {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody = "{\"request\":{},\"tim\":{\"timeStamp\":\"2018-03-13T01:07:11-05:00\"}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody =
         "{\"warning\":\"Warning: TIM contains no RSU, SNMP, or SDW fields.\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
   }
 
   @Test
   void failedObjectNodeConversionShouldReturnConvertingError()
       throws JsonUtilsException, TravelerMessageFromHumanToAsnConverter.NoncompliantFieldsException,
       IOException, TravelerMessageFromHumanToAsnConverter.InvalidNodeLatLonOffsetException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     try (MockedStatic<TravelerMessageFromHumanToAsnConverter> converterStatic =
              mockStatic(TravelerMessageFromHumanToAsnConverter.class)) {
       converterStatic.when(() ->
           TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(any(JsonNode.class)))
           .thenThrow(new JsonUtilsException("testException123", null));

       String requestBody =
           "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

       // execute
       ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

       // verify
       String expectedResponseBody =
           "{\"error\":\"Error converting to encodable TravelerInputData.\"}";
       Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
     } finally {
       DateTimeUtils.setClock(prevClock);
     }
   }
 
   @Test
   void failedXmlConversionShouldReturnConversionError()
       throws XmlUtils.XmlUtilsException, JsonUtilsException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);

     try (MockedStatic<TimTransmogrifier> transmogrifierStatic =
              mockStatic(TimTransmogrifier.class, org.mockito.Mockito.CALLS_REAL_METHODS)) {
       transmogrifierStatic.when(() ->
           TimTransmogrifier.convertToXml(any(), any(), any(), any()))
           .thenThrow(new XmlUtils.XmlUtilsException("testException123", null));

       String requestBody =
           "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";

       // execute
       ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);

       // verify
       String expectedResponseBody =
           "{\"error\":\"Error sending data to ASN.1 Encoder module: testException123\"}";
       Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
     } finally {
       DateTimeUtils.setClock(prevClock);
     }
   }
 
   @Test
   void testSuccessfulMessageReturnsSuccessMessagePost() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePost.tim.json");
     asn1CoderTopics.setEncoderInput("test.successfulMessageReturnsSuccessMessagePost.encoderInput");
     String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
     embeddedKafka.addTopics(topics);

     var jsonTimConsumer = createConsumer("postSuccessJsonTimGroup");
     var asn1CoderEncoderInputConsumer = createConsumer("postSuccessEncoderInputGroup");
     embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
     embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody =
         "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson = new JSONObject(
         loadTestResource("successfulMessageReturnsSuccessMessagePost_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   @Test
   void testSuccessfulSdwRequestMessageReturnsSuccessMessagePost() throws Exception {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulSdwRequestMessageReturnsSuccessMessagePost.tim.json");
     asn1CoderTopics.setEncoderInput(
         "test.successfulSdwRequestMessageReturnsSuccessMessagePost.encoderInput");
    String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
    embeddedKafka.addTopics(topics);

    var jsonTimConsumer = createConsumer("sdwPostSuccessJsonTimGroup");
    var asn1CoderEncoderInputConsumer = createConsumer("sdwPostSuccessEncoderInputGroup");
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String file = "/sdwRequest.json";
     String requestBody =
         IOUtils.toString(TimDepositControllerTest.class.getResourceAsStream(file), "UTF-8");
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson = new JSONObject(
         loadTestResource("successfulSdwRequestMessageReturnsSuccessMessagePost_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   @Test
   void testSuccessfulMessageReturnsSuccessMessagePostWithOde() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePostWithOde.tim.json");
     asn1CoderTopics.setEncoderInput(
         "test.successfulMessageReturnsSuccessMessagePostWithOde.encoderInput");
    String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
    embeddedKafka.addTopics(topics);

    var jsonTimConsumer = createConsumer("postWithOdeJsonTimGroup");
    var asn1CoderEncoderInputConsumer = createConsumer("postWithOdeEncoderInputGroup");
    embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
    embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody =
         "{\"request\":{\"ode\":{},\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson = new JSONObject(
         loadTestResource("successfulMessageReturnsSuccessMessagePostWithOde_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   @Test
   void testSuccessfulMessageReturnsSuccessMessagePut() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulMessageReturnsSuccessMessagePut.tim.json");
     asn1CoderTopics.setEncoderInput("test.successfulMessageReturnsSuccessMessagePut.encoderInput");
     String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
     embeddedKafka.addTopics(topics);

     var jsonTimConsumer = createConsumer("putSuccessJsonTimGroup");
     var asn1CoderEncoderInputConsumer = createConsumer("putSuccessEncoderInputGroup");
     embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
     embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody =
         "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.putTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson = new JSONObject(
         loadTestResource("successfulMessageReturnsSuccessMessagePut_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   @Test
   void testDepositingTimWithExtraProperties() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.depositingTimWithExtraProperties.tim.json");
     asn1CoderTopics.setEncoderInput("test.depositingTimWithExtraProperties.encoderInput");
     String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
     embeddedKafka.addTopics(topics);

     var jsonTimConsumer = createConsumer("extraPropsJsonTimGroup");
     var asn1CoderEncoderInputConsumer = createConsumer("extraPropsEncoderInputGroup");
     embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
     embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody =
         "{\"request\":{\"rsus\":[],\"snmp\":{},\"randomProp1\":true,\"randomProp2\":\"hello world\"},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"randomProp3\":123,\"randomProp4\":{\"nestedProp1\":\"foo\",\"nestedProp2\":\"bar\"}}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson =
         new JSONObject(loadTestResource("depositingTimWithExtraProperties_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   @Test
   void testSuccessfulTimIngestIsTracked() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulTimIngestIsTracked.tim.json");
     asn1CoderTopics.setEncoderInput("test.successfulTimIngestIsTracked.encoderInput");
     String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
     embeddedKafka.addTopics(topics);

     var jsonTimConsumer = createConsumer("ingestTrackedJsonTimGroup");
     var asn1CoderEncoderInputConsumer = createConsumer("ingestTrackedEncoderInputGroup");
     embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
     embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
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
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson =
         new JSONObject(loadTestResource("successfulTimIngestIsTracked_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
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
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }
 
   // This serves as an integration test without mocking the TimTransmogrifier and XmlUtils
   @Test
   void testSuccessfulRsuMessageReturnsSuccessMessagePost() throws IOException {
     // prepare
     odeKafkaProperties.setDisabledTopics(Set.of());
     jsonTopics.setTim("test.successfulRsuMessageReturnsSuccessMessagePost.tim.json");
     asn1CoderTopics.setEncoderInput(
         "test.successfulRsuMessageReturnsSuccessMessagePost.encoderInput");
     String[] topics = {jsonTopics.getTim(), asn1CoderTopics.getEncoderInput()};
     embeddedKafka.addTopics(topics);

     var jsonTimConsumer = createConsumer("rsuPostSuccessJsonTimGroup");
     var asn1CoderEncoderInputConsumer = createConsumer("rsuPostSuccessEncoderInputGroup");
     embeddedKafka.consumeFromAnEmbeddedTopic(jsonTimConsumer, jsonTopics.getTim());
     embeddedKafka.consumeFromAnEmbeddedTopic(asn1CoderEncoderInputConsumer, asn1CoderTopics.getEncoderInput());

     final Clock prevClock = DateTimeUtils.setClock(
         Clock.fixed(Instant.parse("2018-03-13T01:07:11.120Z"), ZoneId.of("UTC")));
     TimDepositController testTimDepositController =
         new TimDepositController(asn1CoderTopics, jsonTopics,
             timIngestTrackerProperties, securityServicesProperties, kafkaTemplate,
             simpleXmlMapper);
     String requestBody =
         "{\"request\": {\"rsus\": [{\"latitude\": 30.123456, \"longitude\": -100.12345, \"rsuId\": 123, \"route\": \"myroute\", \"milepost\": 10, \"rsuTarget\": \"172.0.0.1\", \"rsuRetries\": 3, \"rsuTimeout\": 5000, \"rsuIndex\": 7, \"rsuUsername\": \"myusername\", \"rsuPassword\": \"mypassword\"}], \"snmp\": {\"rsuid\": \"83\", \"msgid\": 31, \"mode\": 1, \"channel\": 183, \"interval\": 2000, \"deliverystart\": \"2024-05-13T14:30:00Z\", \"deliverystop\": \"2024-05-13T22:30:00Z\", \"enable\": 1, \"status\": 4}}, \"tim\": {\"msgCnt\": \"1\", \"timeStamp\": \"2024-05-10T19:01:22Z\", \"packetID\": \"123451234512345123\", \"urlB\": \"null\", \"dataframes\": [{\"startDateTime\": \"2024-05-13T20:30:05.014Z\", \"durationTime\": \"30\", \"doNotUse1\": 0, \"frameType\": \"advisory\", \"msgId\": {\"roadSignID\": {\"mutcdCode\": \"warning\", \"viewAngle\": \"1111111111111111\", \"position\": {\"latitude\": 30.123456, \"longitude\": -100.12345}}}, \"priority\": \"5\", \"doNotUse2\": 0, \"regions\": [{\"name\": \"I_myroute_RSU_172.0.0.1\", \"anchorPosition\": {\"latitude\": 30.123456, \"longitude\": -100.12345}, \"laneWidth\": \"50\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": {\"scale\": 0, \"nodes\": [{\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}, {\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}], \"type\": \"ll\"}, \"direction\": \"0000000000010000\"}], \"doNotUse4\": 0, \"doNotUse3\": 0, \"content\": \"workZone\", \"items\": [\"771\"], \"url\": \"null\"}]}}";
 
     // execute
     ResponseEntity<String> actualResponse = testTimDepositController.postTim(requestBody);
 
     // verify
     String expectedResponseBody = "{\"success\":\"true\"}";
     Assertions.assertEquals(expectedResponseBody, actualResponse.getBody());
 
     // verify JSON tim message
     var jsonTimRecord = KafkaTestUtils.getSingleRecord(jsonTimConsumer, jsonTopics.getTim());
     var actualTimJson = new JSONObject(jsonTimRecord.value());
     var expectedTimJson = new JSONObject(
         loadTestResource("successfulRsuMessageReturnsSuccessMessagePost_tim_expected.json"));
     String actualStreamId = getStreamId(actualTimJson);
     String expectedStreamId = getStreamId(expectedTimJson);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     removeStreamId(actualTimJson);
     removeStreamId(expectedTimJson);
     Assertions.assertEquals(expectedTimJson.toString(2), actualTimJson.toString(2));
 
     // verify ASN.1 coder encoder input message
     var asn1CoderEncoderInputRecord = KafkaTestUtils.getSingleRecord(asn1CoderEncoderInputConsumer,
         asn1CoderTopics.getEncoderInput());
     var actualXml = asn1CoderEncoderInputRecord.value();
     actualStreamId = getStreamId(actualXml);
     Assertions.assertNotEquals(expectedStreamId, actualStreamId);
     actualXml = removeStreamId(actualXml, actualStreamId);
     var expectedXml =
         loadTestResource("successfulRsuMessageReturnsSuccessMessagePost_encoderInput_expected.xml");
     Assertions.assertEquals(expectedXml, actualXml);
 
     // cleanup
     jsonTimConsumer.close();
     asn1CoderEncoderInputConsumer.close();
     DateTimeUtils.setClock(prevClock);
   }

  /**
   * Helper method to create a consumer with an explicit, stable group ID for String messages with String keys.
   */
  private Consumer<String, String> createConsumer(String groupId) {
    java.util.Map<String, Object> consumerProps =
         KafkaTestUtils.consumerProps(embeddedKafka, groupId, true);
    return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer()).createConsumer();
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
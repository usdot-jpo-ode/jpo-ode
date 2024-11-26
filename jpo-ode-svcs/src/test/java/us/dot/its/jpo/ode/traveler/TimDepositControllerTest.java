/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.traveler;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;


public class TimDepositControllerTest {

   @Tested
   TimDepositController testTimDepositController;

   @Injectable
   OdeKafkaProperties injectableOdeKafkaProperties;

   @Injectable
   Asn1CoderTopics injectableAsn1CoderTopics;

   @Injectable
   PojoTopics injectablePojoTopics;

   @Injectable
   JsonTopics injectableJsonTopics;

   @Injectable
   TimIngestTrackerProperties injectableTimIngestTrackerProperties;

   @Injectable
   SecurityServicesProperties injectableSecurityServicesProperties;

   @Capturing
   MessageProducer<?, ?> capturingMessageProducer;

   @Test
   public void nullRequestShouldReturnEmptyError() {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(null);
      assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
   }

   @Test
   public void emptyRequestShouldReturnEmptyError() {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim("");
      assertEquals("{\"error\":\"Empty request.\"}", actualResponse.getBody());
   }

   @Test
   public void invalidJsonSyntaxShouldReturnJsonSyntaxError() {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"in\"va}}}on\"}}");
      assertEquals("{\"error\":\"Malformed or non-compliant JSON syntax.\"}", actualResponse.getBody());
   }

   @Test
   public void missingRequestElementShouldReturnMissingRequestError() {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"tim\":{}}");
      assertEquals("{\"error\":\"Missing or invalid argument: Request element is required as of version 3.\"}",
            actualResponse.getBody());
   }

   @Test
   public void invalidTimestampShouldReturnInvalidTimestampError() {
      ResponseEntity<String> actualResponse = testTimDepositController
            .postTim("{\"request\":{},\"tim\":{\"timeStamp\":\"201-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"error\":\"Invalid timestamp in tim record: 201-03-13T01:07:11-05:00\"}",
            actualResponse.getBody());
   }

   @Test
   public void messageWithNoRSUsOrSDWShouldReturnWarning() {
      ResponseEntity<String> actualResponse = testTimDepositController
            .postTim("{\"request\":{},\"tim\":{\"timeStamp\":\"2018-03-13T01:07:11-05:00\"}}");
      assertEquals(
            "{\"warning\":\"Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to broadcast streams.\"}",
            actualResponse.getBody());
   }

   @Test
   public void failedObjectNodeConversionShouldReturnConvertingError(
         @Capturing TravelerMessageFromHumanToAsnConverter capturingTravelerMessageFromHumanToAsnConverter)
         throws JsonUtilsException {

      new Expectations() {

         {
            TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim((JsonNode) any);
            result = new JsonUtilsException("testException123", null);
         }
      };

      ResponseEntity<String> actualResponse = testTimDepositController.postTim(
            "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"error\":\"Error converting to encodable TravelerInputData.\"}", actualResponse.getBody());
   }

   @Test
   public void failedXmlConversionShouldReturnConversionError(@Capturing TimTransmogrifier capturingTimTransmogrifier)
         throws XmlUtilsException, JsonUtilsException {

      new Expectations() {
         {
            TimTransmogrifier.convertToXml((DdsAdvisorySituationData) any, (ObjectNode) any, (OdeMsgMetadata) any,
                  (SerialId) any);
            result = new XmlUtilsException("testException123", null);
         }
      };

      ResponseEntity<String> actualResponse = testTimDepositController.postTim(
            "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"error\":\"Error sending data to ASN.1 Encoder module: testException123\"}",
            actualResponse.getBody());

   }

   @Test
   public void testSuccessfulMessageReturnsSuccessMessagePost(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(
            "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

   @Test
   public void testSuccessfullSdwRequestMessageReturnsSuccessMessagePost()
         throws Exception {
      String file = "/sdwRequest.json";
      String json = IOUtils.toString(
            TimDepositControllerTest.class.getResourceAsStream(file),
            "UTF-8");
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(json);
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

   @Test
   public void testSuccessfulMessageReturnsSuccessMessagePostWithOde(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(
            "{\"request\":{\"ode\":{},\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

   @Test
   public void testSuccessfulMessageReturnsSuccessMessagePut(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      ResponseEntity<String> actualResponse = testTimDepositController.putTim(
            "{\"request\":{\"rsus\":[],\"snmp\":{}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\"}}");
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

   @Test
   public void testDepositingTimWithExtraProperties(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      String timToSubmit = "{\"request\":{\"rsus\":[],\"snmp\":{},\"randomProp1\":true,\"randomProp2\":\"hello world\"},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"randomProp3\":123,\"randomProp4\":{\"nestedProp1\":\"foo\",\"nestedProp2\":\"bar\"}}}";
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(timToSubmit);
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

   @Test
   public void testSuccessfulTimIngestIsTracked(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      String timToSubmit = "{\"request\":{\"rsus\":[],\"snmp\":{},\"randomProp1\":true,\"randomProp2\":\"hello world\"},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"randomProp3\":123,\"randomProp4\":{\"nestedProp1\":\"foo\",\"nestedProp2\":\"bar\"}}}";
      long priorIngestCount = TimIngestTracker.getInstance().getTotalMessagesReceived();
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(timToSubmit);
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
      assertEquals(priorIngestCount + 1, TimIngestTracker.getInstance().getTotalMessagesReceived());
   }

   @Test
   public void testSuccessfulRsuMessageReturnsSuccessMessagePost(@Capturing TimTransmogrifier capturingTimTransmogrifier, @Capturing XmlUtils capturingXmlUtils) {
      String timToSubmit = "{\"request\": {\"rsus\": [{\"latitude\": 30.123456, \"longitude\": -100.12345, \"rsuId\": 123, \"route\": \"myroute\", \"milepost\": 10, \"rsuTarget\": \"172.0.0.1\", \"rsuRetries\": 3, \"rsuTimeout\": 5000, \"rsuIndex\": 7, \"rsuUsername\": \"myusername\", \"rsuPassword\": \"mypassword\"}], \"snmp\": {\"rsuid\": \"83\", \"msgid\": 31, \"mode\": 1, \"channel\": 183, \"interval\": 2000, \"deliverystart\": \"2024-05-13T14:30:00Z\", \"deliverystop\": \"2024-05-13T22:30:00Z\", \"enable\": 1, \"status\": 4}}, \"tim\": {\"msgCnt\": \"1\", \"timeStamp\": \"2024-05-10T19:01:22Z\", \"packetID\": \"123451234512345123\", \"urlB\": \"null\", \"dataframes\": [{\"startDateTime\": \"2024-05-13T20:30:05.014Z\", \"durationTime\": \"30\", \"sspTimRights\": \"1\", \"frameType\": \"advisory\", \"msgId\": {\"roadSignID\": {\"mutcdCode\": \"warning\", \"viewAngle\": \"1111111111111111\", \"position\": {\"latitude\": 30.123456, \"longitude\": -100.12345}}}, \"priority\": \"5\", \"sspLocationRights\": \"1\", \"regions\": [{\"name\": \"I_myroute_RSU_172.0.0.1\", \"anchorPosition\": {\"latitude\": 30.123456, \"longitude\": -100.12345}, \"laneWidth\": \"50\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": {\"scale\": 0, \"nodes\": [{\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}, {\"delta\": \"node-LL\", \"nodeLat\": 0.0, \"nodeLong\": 0.0}], \"type\": \"ll\"}, \"direction\": \"0000000000010000\"}], \"sspMsgTypes\": \"1\", \"sspMsgContent\": \"1\", \"content\": \"workZone\", \"items\": [\"771\"], \"url\": \"null\"}]}}";
      ResponseEntity<String> actualResponse = testTimDepositController.postTim(timToSubmit);
      assertEquals("{\"success\":\"true\"}", actualResponse.getBody());
   }

}

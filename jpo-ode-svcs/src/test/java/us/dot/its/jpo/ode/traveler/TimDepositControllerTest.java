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

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimDepositControllerTest {

   @Tested
   TimDepositController testTimDepositController;

   @Injectable
   OdeProperties injectableOdeProperties;

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
      ResponseEntity<String> actualResponse = testTimDepositController.postTim("{\"invalid\":\"json\"}}");
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

}

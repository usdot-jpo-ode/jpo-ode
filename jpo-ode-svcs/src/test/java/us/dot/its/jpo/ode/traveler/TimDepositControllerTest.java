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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.http.InternalServerErrorException;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInformation;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimDepositControllerTest {

   @Tested
   TimDepositController testTimDepositController;
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DdsDepositor<DdsStatusMessage> mockDepositor;

   @Mocked
   OdeTravelerInputData mockOdeTravelerInputData;
   @Mocked
   OdeTravelerInformationMessage mockTim;
   @Mocked
   MessageProducer<String, OdeObject> mockProducer;
   @Mocked
   MessageProducer<String, String> mockStringMsgProducer;
   @Mocked
   ObjectNode mockEncodableTid;
   @Mocked
   TravelerInformation mockTravelerInfo;
   @Mocked
   RSU mockRsu;
   @Mocked
   SNMP mockSnmp;
   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockPdu;
   @Mocked
   ScopedPDU mockScopedPdu;
   
   @Capturing
   MessageProducer<?,?> capturingMessageProducer;
   
   @Capturing
   Executors capturingExecutors;

   @Test
   public void emptyRequestShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim(null);
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("");
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void badRequestShouldThrowException() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("test123");
         assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }
   
   /**
    * Verify null and "" empty response are immediately caught
    */
   @Test
   public void testEmptyRequestsShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim(null);
         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("");
         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }
   }

   /**
    * Throw and catch an exception in parsing the JSON.
    */
   @Test
   public void testMalformedJSONShouldReturnError() {
      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
   }

   @Ignore
   @Test
   public void encodingErrorShouldThrowException() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = anyString;

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = new Exception("Encoding error.");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("test123");
         assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Ignore
   @Test
   public void snmpExceptionShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = anyString;

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = anyString;

               mockOdeTravelerInputData.getRequest().getRsus();
               result = new RSU[] { mockRsu };

               mockOdeTravelerInputData.getRequest().getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "snmpException";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = new Exception("SNMP Error");

               mockOdeTravelerInputData.getRequest().getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"snmpException\",\"success\":\"false\",\"error\":\"java.lang.Exception\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Ignore
   @Test
   public void nullResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = "mockTim";

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = anyString;

               mockOdeTravelerInputData.getRequest().getRsus();
               result = new RSU[] { mockRsu };

               mockOdeTravelerInputData.getRequest().getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "nullResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = null;

               mockOdeTravelerInputData.getRequest().getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"nullResponse\",\"success\":\"false\",\"error\":\"Timeout.\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());
   }

   @Ignore
   @Test
   public void badResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = "mockTim";

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = anyString;

               mockOdeTravelerInputData.getRequest().getRsus();
               result = new RSU[] { mockRsu };

               mockOdeTravelerInputData.getRequest().getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "badResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = -1;

               mockOdeTravelerInputData.getRequest().getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"badResponse\",\"success\":\"false\",\"error\":\"Error code -1 null\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Ignore
   @Test
   public void ddsFailureShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = "mockTim";

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = anyString;

               mockOdeTravelerInputData.getRequest().getRsus();
               result = new RSU[] { mockRsu };

               mockOdeTravelerInputData.getRequest().getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "nonexistentialRsu";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = 0;

               mockOdeTravelerInputData.getRequest().getSdw();
               result = new InternalServerErrorException("Deposit to SDW Failed");

            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"nonexistentialRsu\",\"success\":\"true\",\"message\":\"Success.\"}],\"dds_deposit\":{\"success\":\"false\"}}",
            response.getBody());

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Ignore
   @Test
   public void goodResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimDepositController.class) {
            {
               mockOdeTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockOdeTravelerInputData;
               mockOdeTravelerInputData.toJson(true);
               result = "mockTim";

             //TODO open-ode   
//               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
//               result = mockTravelerInfo;
//
//               mockBuilder.encodeTravelerInformationToHex();
//               result = anyString;

               mockOdeTravelerInputData.getRequest().getRsus();
               result = new RSU[] { mockRsu };

               mockOdeTravelerInputData.getRequest().getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "goodResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = 0;

               mockOdeTravelerInputData.getRequest().getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"goodResponse\",\"success\":\"true\",\"message\":\"Success.\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());
   }

   @Test 
   public void testObfuscateRsuPassword() {
     String actual = TimDepositController.obfuscateRsuPassword("{\"metadata\":{\"request\":{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"sdw\":null,\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMsgPayload\",\"serialId\":{\"streamId\":\"59651ecc-240c-4440-9011-4a43c926817b\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2018-11-16T19:21:22.568Z\",\"schemaVersion\":6,\"recordGeneratedAt\":\"2017-03-13T06:07:11Z\",\"recordGeneratedBy\":\"TMC\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage\",\"data\":{\"msgCnt\":13,\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":0,\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000000000000000\"},\"furtherInfoID\":null},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":22,\"priority\":0,\"sspLocationRights\":3,\"regions\":[{\"name\":\"bob\",\"regulatorID\":23,\"segmentID\":33,\"anchorPosition\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"laneWidth\":7,\"directionality\":3,\"closedPath\":false,\"direction\":\"1010101010101010\",\"description\":\"geometry\",\"path\":null,\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":1,\"laneWidth\":33,\"circle\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"radius\":15,\"units\":7}},\"oldRegion\":null}],\"sspMsgTypes\":2,\"sspMsgContent\":3,\"content\":\"Advisory\",\"items\":[\"125\",\"some text\",\"250\",\"\\u002798765\"],\"url\":\"null\"}],\"asnDataFrames\":null}}}");
     assertEquals("{\"metadata\":{\"request\":{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"sdw\":null,\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMsgPayload\",\"serialId\":{\"streamId\":\"59651ecc-240c-4440-9011-4a43c926817b\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2018-11-16T19:21:22.568Z\",\"schemaVersion\":6,\"recordGeneratedAt\":\"2017-03-13T06:07:11Z\",\"recordGeneratedBy\":\"TMC\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage\",\"data\":{\"msgCnt\":13,\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":0,\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000000000000000\"},\"furtherInfoID\":null},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":22,\"priority\":0,\"sspLocationRights\":3,\"regions\":[{\"name\":\"bob\",\"regulatorID\":23,\"segmentID\":33,\"anchorPosition\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"laneWidth\":7,\"directionality\":3,\"closedPath\":false,\"direction\":\"1010101010101010\",\"description\":\"geometry\",\"path\":null,\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":1,\"laneWidth\":33,\"circle\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"radius\":15,\"units\":7}},\"oldRegion\":null}],\"sspMsgTypes\":2,\"sspMsgContent\":3,\"content\":\"Advisory\",\"items\":[\"125\",\"some text\",\"250\",\"\\u002798765\"],\"url\":\"null\"}],\"asnDataFrames\":null}}}", actual);
   }
   
   public void assertConvertArray(String array, String arrayKey, String elementKey, Object expectedXml) throws JsonUtilsException, XmlUtilsException {
     JsonNode obj = JsonUtils.toObjectNode(array);
     JsonNode oldObj =  obj.get(arrayKey);

     JsonNode newObj = XmlUtils.createEmbeddedJsonArrayForXmlConversion(elementKey, (ArrayNode)oldObj);
     String actualXml = XmlUtils.toXmlStatic(newObj);

     assertEquals(expectedXml, actualXml);
   }

//   @Test @Ignore
//   public void testConvertRsusArray() throws JsonUtilsException, XmlUtilsException {
//     String single = "{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"rsus\":{\"rsu_\":[{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}]},\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}}";
//     String singleXmlExpected = "";
//     assertConvertArray(single, TimDepositController.RSUS_STRING, TimDepositController.RSUS_STRING, singleXmlExpected);
//
//     String multi = "{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"rsus\":{\"rsu_\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}]},\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}}";
//     String multiXmlExpected = "";
//     assertConvertArray(multi, TimDepositController.RSUS_STRING, TimDepositController.RSUS_STRING, multiXmlExpected);
//   }

}

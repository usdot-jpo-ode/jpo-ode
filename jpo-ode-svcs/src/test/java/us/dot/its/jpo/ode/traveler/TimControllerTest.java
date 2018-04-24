package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerTest {

   @Tested
   TimController testTimController;
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DdsDepositor<DdsStatusMessage> mockDepositor;

   @Mocked
   OdeTravelerInputData mockTravelerInputData;
   @Mocked
   J2735TravelerInformationMessage mockTim;
   @Mocked
   MessageProducer<String, OdeObject> mockProducer;
   @Mocked
   MessageProducer<String, String> mockStringMsgProducer;
   @Mocked
   ObjectNode mockEncodableTid;
   @Mocked
   TravelerInformation mockTravelerInfo;
   @Mocked
   OssTravelerMessageBuilder mockBuilder;
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
   @Mocked
   DdsAdvisorySituationData mockAsdMsg;
   
   @Capturing
   MessageProducer<?,?> capturingMessageProducer;
   
   @Capturing
   Executors capturingExecutors;

   @Test
   public void emptyRequestShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimController.postTim(null);
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimController.postTim("");
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void badRequestShouldThrowException() {

      try {
         ResponseEntity<String> response = testTimController.postTim("test123");
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
   public void testHappyPath(@Mocked ObjectNode mockTid) {

      
      String tid = "{\"ode\":{\"version\":2},\"tim\":{\"index\":\"10\",\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000000000000000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"geometry\",\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"laneWidth\":\"33\",\"circle\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"radius\":\"15\",\"units\":\"7\"}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"Advisory\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]},\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"0\",\"rsuTimeout\":\"2000\"},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}}";
//      String encodableTim = "{\"tim\":{\"msgCnt\":13,\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":[{\"TravelerDataFrame\":{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":0,\"sspLocationRights\":3,\"regions\":[{\"GeographicalPath\":{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":1,\"laneWidth\":3300,\"circle\":{\"radius\":15,\"units\":7,\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}],\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482027,\"tcontent\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}}]},\"ode\":{\"version\":2,\"index\":10,\"verb\":0},\"sdw\":{\"serviceRegion\":{\"nwCorner\":{\"latitude\":44.998459,\"longitude\":-111.040817},\"seCorner\":{\"latitude\":41.104674,\"longitude\":-104.111312}},\"ttl\":\"oneweek\"},\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":0,\"rsuTimeout\":2000},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000}]}";
      try {
         new Expectations() {
            {
               mockProducer.send(anyString, anyString, (OdeTimData)any);
               mockStringMsgProducer.send(anyString, anyString, anyString);
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         ResponseEntity<String> response = testTimController.postTim(tid);
         assertTrue(response.getBody().contains("Success"));
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
   public void encodingErrorShouldThrowException() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = anyString;

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = new Exception("Encoding error.");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         ResponseEntity<String> response = testTimController.postTim("test123");
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
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = anyString;

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTravelerInputData.getRsus();
               result = new RSU[] { mockRsu };

               mockTravelerInputData.getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "snmpException";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = new Exception("SNMP Error");

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimController.postTim("test123");
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
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = "mockTim";

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTravelerInputData.getRsus();
               result = new RSU[] { mockRsu };

               mockTravelerInputData.getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "nullResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = null;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"nullResponse\",\"success\":\"false\",\"error\":\"Timeout.\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());
   }

   @Ignore
   @Test
   public void badResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = "mockTim";

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTravelerInputData.getRsus();
               result = new RSU[] { mockRsu };

               mockTravelerInputData.getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "badResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = -1;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimController.postTim("test123");
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
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = "mockTim";

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTravelerInputData.getRsus();
               result = new RSU[] { mockRsu };

               mockTravelerInputData.getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "nonexistentialRsu";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = 0;

               mockTravelerInputData.getSdw();
               result = new InternalServerErrorException("Deposit to SDW Failed");

            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimController.postTim("test123");
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
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TimController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, OdeTravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = "mockTim";

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTravelerInputData.getRsus();
               result = new RSU[] { mockRsu };

               mockTravelerInputData.getSnmp();
               result = mockSnmp;

               mockRsu.getRsuTarget();
               result = "goodResponse";

               //Asn1EncodedDataRouter.createAndSend(mockSnmp, mockRsu, anyInt, anyString, anyInt);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = mockPdu;
               mockPdu.getErrorStatus();
               result = 0;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(
            "{\"rsu_responses\":[{\"target\":\"goodResponse\",\"success\":\"true\",\"message\":\"Success.\"}],\"dds_deposit\":{\"success\":\"true\"}}",
            response.getBody());
   }

   @Test
   public void deleteShouldReturnBadRequestWhenNull() {
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim(null, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException() {
      try {
         new Expectations(SnmpSession.class, JsonUtils.class) {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
               
               JsonUtils.fromJson(anyString, (Class<?>) any);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException() {
      try {
         new Expectations(SnmpSession.class, JsonUtils.class) {
            {
               new SnmpSession((RSU) any);
               result = new NullPointerException("testException123");
               
               JsonUtils.fromJson(anyString, (Class<?>) any);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }

}

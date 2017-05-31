package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.http.InternalServerErrorException;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.snmp.SNMP;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@RunWith(JMockit.class)
public class TIMControllerTest {

   @Tested TIMController timController;
   @Injectable OdeProperties mockOdeProperties;
   @Injectable DdsDepositor<DdsStatusMessage> mockDepositor;

   @Mocked TravelerInputData mockTravelerInputData;
   @Mocked TravelerInformation mockTravelerInfo;
   @Mocked OssTravelerMessageBuilder mockBuilder;
   @Mocked RSU mockRsu;
   @Mocked SNMP mockSnmp;
   @Mocked ResponseEvent mockResponseEvent;
   @Mocked PDU mockPdu;
   @Mocked ScopedPDU mockScopedPdu;
   @Mocked AsdMessage mockAsdMsg;
   
   @Test
   public void emptyRequestShouldThrowException() {

      try {
         timController.timMessage(null);
         fail("Expected timException");
      } catch (Exception e) {
         assertEquals(BadRequestException.class, e.getClass());
         assertEquals("{success: false, message: \"Endpoint received null request\"}", e.getMessage());
      }

      try {
         timController.timMessage("");
         fail("Expected timException");
      } catch (BadRequestException bre) {
         assertEquals("{success: false, message: \"Endpoint received null request\"}", bre.getMessage());
      } catch (Exception e) {
         fail("Unexxpected Exception");
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void badRequestShouldThrowException() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = new Exception("Bad JSON");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      
      try {
         timController.timMessage("test123");
         fail("Expected timException");
      } catch (BadRequestException e) {
         assertEquals("{success: false, message: \"Invalid Request Body\"}", e.getMessage());
      } catch (Exception ue) {
         fail("Unexxpected Exception");
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void builderErrorShouldThrowException() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = "mockTim";

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = new Exception("Builder Error");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      try {
         timController.timMessage("test123");
         fail("Expected Exception");
      } catch (BadRequestException e) {
         assertEquals("{success: false, message: \"Invalid Traveler Information Message data value in the request\"}", e.getMessage());
      } catch (Exception ue) {
         fail("Unexxpected Exception");
      }


      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void encodingErrorShouldThrowException() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTravelerInputData;
               mockTravelerInputData.toJson(true);
               result = anyString;

               mockBuilder.buildTravelerInformation(mockTravelerInputData.getTim());
               result = mockTravelerInfo;
               
               mockBuilder.encodeTravelerInformationToHex();
               result = new Exception("Encoding Error");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      try {
         timController.timMessage("test123");
         fail("Expected Exception");
      } catch (BadRequestException e) {
         assertEquals("{success: false, message: \"Failed to encode Traveler Information Message\"}", e.getMessage());
      } catch (Exception ue) {
         fail("Unexxpected Exception");
      }


      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void snmpExceptionShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
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
               result = "snmpExeption";
               
               TIMController.createAndSend(mockSnmp, mockRsu, anyString);
               result = new Exception("SNMP Error");

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = timController.timMessage("test123");
      assertEquals("{SDW={success: true, message: \"Deposit to SDW was successful\"}, snmpExeption={success: false, message: \"Exception while sending message to RSU\"}}", response);


      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void nullResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
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
               
               TIMController.createAndSend(mockSnmp, mockRsu, anyString);
               result = null;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      
      String response = timController.timMessage("test123");
      assertEquals("{SDW={success: true, message: \"Deposit to SDW was successful\"}, nullResponse={success: false, message: \"No response from RSU IP=nullResponse\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void badResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
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
               
               TIMController.createAndSend(mockSnmp, mockRsu, anyString);
               result = mockResponseEvent;
               mockResponseEvent.getResponse(); result = mockPdu;
               mockPdu.getErrorStatus(); result = -1;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = timController.timMessage("test123");
      assertEquals("{SDW={success: true, message: \"Deposit to SDW was successful\"}, badResponse={success: false, message: \"SNMP deposit failed for RSU IP badResponse, error code=-1(null)\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void ddsFailureShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
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
               result = "ddsFailure";
               
               TIMController.createAndSend(mockSnmp, mockRsu, anyString);
               result = mockResponseEvent;
               mockResponseEvent.getResponse(); result = mockPdu;
               mockPdu.getErrorStatus(); result = 0;

               mockTravelerInputData.getSdw();
               result = new InternalServerErrorException("Deposit to SDW Failed");
               
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = timController.timMessage("test123");
      assertEquals("{ddsFailure={success: true, message: \"SNMP deposit successful. RSU IP = ddsFailure, Status Code: 0\"}, SDW={success: false, message: \"Error depositing to SDW\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void goodResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, DateTimeUtils.class, EventLogger.class, TIMController.class) {
            {
               mockTravelerInputData.toString();
               result = "something";
               minTimes = 0;

               JsonUtils.fromJson(anyString, TravelerInputData.class);
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
               
               TIMController.createAndSend(mockSnmp, mockRsu, anyString);
               result = mockResponseEvent;
               mockResponseEvent.getResponse(); result = mockPdu;
               mockPdu.getErrorStatus(); result = 0;

               mockTravelerInputData.getSdw();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = timController.timMessage("test123");
      assertEquals("{SDW={success: true, message: \"Deposit to SDW was successful\"}, goodResponse={success: true, message: \"SNMP deposit successful. RSU IP = goodResponse, Status Code: 0\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

}

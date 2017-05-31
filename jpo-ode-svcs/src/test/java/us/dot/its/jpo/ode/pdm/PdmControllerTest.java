package us.dot.its.jpo.ode.pdm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.snmp.PdmManagerService;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

@RunWith(JMockit.class)
public class PdmControllerTest {

   @Tested
   PdmController pdmController;

   @Mocked
   J2735PdmRequest mockPdmRequest;
   @Mocked
   J2735ProbeDataManagment mockPdm;

   @Mocked
   RSU mockRsu;
   @Mocked
   PDU mockPdu;
   @Mocked
   ScopedPDU mockScopedPdu;
   @Mocked
   ResponseEvent mockResponseEvent;

   @Test
   public void nullRequestShouldLogAndThrowException() {

      try {
         pdmController.pdmMessage(null);
         fail("Expected PdmException");
      } catch (BadRequestException e) {
         assertEquals("Endpoint received null request", e.getMessage());
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
   public void nullResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, EventLogger.class, PdmController.class, PdmManagerService.class, EventLogger.class) {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockPdmRequest;
               mockPdmRequest.toJson(true);
               result = "mockPdm";

               mockPdmRequest.getRsuList();
               result = new RSU[] { mockRsu };

               PdmManagerService.createPDU(mockPdm);
               result = mockScopedPdu;
               
               mockRsu.getRsuTarget();
               result = "nullResponse";
               
               PdmController.createAndSend(mockScopedPdu, mockRsu);
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      
      String response = pdmController.pdmMessage("test123");
      assertEquals("{nullResponse={success: false, message: \"No response from RSU IP=nullResponse\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void badResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, EventLogger.class, PdmController.class, PdmManagerService.class, EventLogger.class) {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockPdmRequest;
               mockPdmRequest.toJson(true);
               result = "mockPdm";

               mockPdmRequest.getRsuList();
               result = new RSU[] { mockRsu };

               PdmManagerService.createPDU(mockPdm);
               result = mockScopedPdu;
               
               mockRsu.getRsuTarget();
               result = "badResponse";
               
               PdmController.createAndSend(mockScopedPdu, mockRsu);
               result = mockResponseEvent;
               mockResponseEvent.getResponse(); result = mockPdu;
               mockPdu.getErrorStatus(); result = -1;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = pdmController.pdmMessage("test123");
      assertEquals("{badResponse={success: false, message: \"SNMP deposit failed for RSU IP badResponse, error code=-1(null)\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void goodResponseShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, EventLogger.class, PdmController.class, PdmManagerService.class, EventLogger.class) {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockPdmRequest;
               mockPdmRequest.toJson(true);
               result = "mockPdm";

               mockPdmRequest.getRsuList();
               result = new RSU[] { mockRsu };

               PdmManagerService.createPDU(mockPdm);
               result = mockScopedPdu;
               
               mockRsu.getRsuTarget();
               result = "goodResponse";
               
               PdmController.createAndSend(mockScopedPdu, mockRsu);
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = pdmController.pdmMessage("test123");
      assertEquals("{goodResponse={success: false, message: \"No response from RSU IP=goodResponse\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void nullGetResponseShouldLogAndReturn() throws IOException {

      new Expectations(JsonUtils.class, PdmController.class, PdmManagerService.class, EventLogger.class) {
         {
            JsonUtils.fromJson(anyString, J2735PdmRequest.class);
            result = mockPdmRequest;
            
            mockPdmRequest.toJson(true);
            result = "mockPdm";

            mockPdmRequest.getRsuList();
            result = new RSU[] { mockRsu };
            
            PdmManagerService.createPDU(mockPdm);
            result = mockScopedPdu;

            mockRsu.getRsuTarget();
            result = "nullGetResponse";
            
            PdmController.createAndSend(mockScopedPdu, mockRsu);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = null;
         }
      };

      String response = pdmController.pdmMessage("test123");
      assertEquals("{nullGetResponse={success: false, message: \"No response from RSU IP=nullGetResponse\"}}", response);

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void snmpExceptionShouldLogAndReturn() {

      try {
         new Expectations(JsonUtils.class, PdmController.class, PdmManagerService.class, EventLogger.class) {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockPdmRequest;
               
               mockPdmRequest.toJson(true);
               result = "mockPdm";

               mockPdmRequest.getRsuList();
               result = new RSU[] { mockRsu };
               
               PdmManagerService.createPDU(mockPdm);
               result = mockScopedPdu;

               mockRsu.getRsuTarget();
               result = "snmpExeption";
               
               PdmController.createAndSend(mockScopedPdu, mockRsu);
               result = new Exception("SNMP Error");
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      String response = pdmController.pdmMessage("test123");
      assertEquals("{snmpExeption={success: false, message: \"Exception while sending message to RSU\"}}", response);


      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void testCreateAndSend(@Mocked SnmpSession mockSnmpSession) throws IOException {

      try {
         new Expectations(SnmpSession.class) {
            {
               new SnmpSession((RSU) any); result = mockSnmpSession;
               
               mockSnmpSession.set((PDU) any, (Snmp) any, 
                     (TransportMapping) any, (UserTarget) any); result = mockResponseEvent;
               mockResponseEvent.getResponse(); result = mockPdu;
               mockPdu.getErrorStatus(); result = 0;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      

      ResponseEvent response = PdmController.createAndSend(mockScopedPdu, mockRsu);
      assertEquals(0, response.getResponse().getErrorStatus());


      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

}

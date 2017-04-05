package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.plugin.GenericSnmp.SNMP;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimParameters;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@RunWith(JMockit.class)
public class TravelerMessageControllerTest {

   @Tested
   TravelerMessageController tmc;
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DdsDepositor<DdsStatusMessage> mockDepositor;
   @Mocked
   J2735TravelerInputData mockTim;
   @Mocked
   TravelerInformation mockInfo;
   @Mocked
   OssTravelerMessageBuilder mockBuilder;

   @Injectable
   RSU mockRsu;
   @Injectable
   SNMP mockSnmp;
   @Injectable
   SnmpProperties mockProps;
   @Injectable
   TimParameters mockParams;

   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockPdu;

   @Before
   public void setup() {
      new Expectations() {
         {
            mockTim.toString();
            result = "something";
            minTimes = 0;
         }
      };
   }

   @Test
   public void nullRequestShouldLogAndThrowException(@Mocked final EventLogger eventLogger) {

      try {
         tmc.timMessage(null);
         fail("Expected timException");
      } catch (Exception e) {
         assertEquals(TimMessageException.class, e.getClass());
         assertEquals("TIM CONTROLLER - Endpoint received null request", e.getMessage());
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void nullResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

      new Expectations() {
         {
            JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
            result = new TimMessageException("");
         }
      };

      try {
         tmc.timMessage("");
      } catch (Exception e) {
         assertEquals(TimMessageException.class + ": ", "class " + e.getMessage());
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void responseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim);
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         tmc.timMessage("");
         fail("Expected exception");
      } catch (Exception e) {
         assertEquals(TimMessageException.class, e.getClass());
         assertEquals(TimMessageException.class + ": TIM Builder returned null", "class " + e.getMessage());
         e.printStackTrace();
      }
   }

   @Test
   public void badResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim);
               result = mockInfo;

               mockBuilder.getHexTravelerInformation();
               result = anyString;

               mockTim.getRsus();
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         tmc.timMessage("testMessage123");
      } catch (Exception e) {
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void goodResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim);
               result = mockInfo;

               mockBuilder.getHexTravelerInformation();
               result = anyString;

               mockTim.getRsus();
               result = new RSU[] { mockRsu };

               mockTim.getSnmp();
               result = mockSnmp;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         tmc.timMessage("testMessage123");
      } catch (Exception e) {
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void checkResponseEvent(@Mocked final JsonUtils jsonUtils,
         @Mocked ManagerAndControllerServices mockTimManagerService, @Mocked final DateTimeUtils mockDateTimeUtils,
         @Mocked final DdsDepositor<DdsStatusMessage> mockDepositor) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim);
               result = mockInfo;

               mockBuilder.getHexTravelerInformation();
               result = anyString;

               mockTim.getRsus();
               result = new RSU[] { mockRsu };

               mockTim.getSnmp();
               result = mockSnmp;

               ManagerAndControllerServices.createAndSend((TimParameters) any, (SnmpProperties) any);
               result = mockResponseEvent;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         tmc.timMessage("testMessage123");
      } catch (Exception e) {
         fail("Unexpected exception" + e);
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

}

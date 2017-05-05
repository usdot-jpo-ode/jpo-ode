package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
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
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.snmp.SNMP;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@RunWith(JMockit.class)
public class TIMControllerTest {

   @Tested
   TIMController tmc;
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DdsDepositor<DdsStatusMessage> mockDepositor;
   @Mocked
   TravelerInputData mockTim;
   @Mocked
   TravelerInformation mockInfo;
   @Mocked
   OssTravelerMessageBuilder mockBuilder;

   @Injectable
   RSU mockRsu;
   @Injectable
   SNMP mockSnmp;

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
   public void checkNullDepositor(@Mocked final DdsDepositor<?> mockDdsDepositor, @Mocked final LoggerFactory mockLoggerFactory) {
      new Expectations(){ {
         new DdsDepositor<>(mockOdeProperties);
         result = new Exception();
      }};
      new TIMController(mockOdeProperties);
   }

   @Test
   public void nullRequestShouldLogAndThrowException(@Mocked final EventLogger eventLogger) {

      try {
         tmc.timMessage(null);
         fail("Expected timException");
      } catch (Exception e) {
         assertEquals(BadRequestException.class, e.getClass());
         assertEquals("TIM CONTROLLER - Endpoint received null request", e.getMessage());
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void nullResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils, @Mocked final EventLogger mockLoggerFactory) {

      new Expectations() {
         {
            JsonUtils.fromJson(anyString, TravelerInputData.class);
            result = new BadRequestException("");
         }
      };

      try {
         tmc.timMessage("");
         fail("Expected BadRequestException");
      } catch (Exception e) {
         assertEquals(BadRequestException.class + ": ", "class " + e.getMessage());
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void responseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils, @Mocked final EventLogger eventLogger) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim.getTim());
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
         assertEquals(BadRequestException.class, e.getClass());
         assertEquals(BadRequestException.class + ": TIM Builder returned null", "class " + e.getMessage());
      }
   }

   @Test
   public void badResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils, @Mocked final EventLogger eventLogger) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim.getTim());
               result = mockInfo;

               mockBuilder.encodeTravelerInformationToHex();
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
          fail("Unexpected exception: " + e);
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

   @Test
   public void goodResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils, @Mocked final EventLogger eventLogger) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim.getTim());
               result = mockInfo;

               mockBuilder.encodeTravelerInformationToHex();
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
         @Mocked final ManagerAndControllerServices mockTimManagerService, @Mocked final DateTimeUtils mockDateTimeUtils,
         @Mocked final DdsDepositor<DdsStatusMessage> mockDepositor, @Mocked final EventLogger eventLogger) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim.getTim());
               result = mockInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTim.getRsus();
               result = new RSU[] { mockRsu };

               mockTim.getSnmp();
               result = mockSnmp;

               TIMController.createAndSend(
                     (SNMP) any, (RSU) any, anyString);
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
   
   @Test
   public void checkNullResponseEvent(@Mocked final JsonUtils jsonUtils,
         @Mocked final ManagerAndControllerServices mockTimManagerService, @Mocked final DateTimeUtils mockDateTimeUtils,
         @Mocked final DdsDepositor<DdsStatusMessage> mockDepositor, @Mocked final EventLogger eventLogger) {

      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, TravelerInputData.class);
               result = mockTim;

               mockBuilder.buildTravelerInformation(mockTim.getTim());
               result = mockInfo;

               mockBuilder.encodeTravelerInformationToHex();
               result = anyString;

               mockTim.getRsus();
               result = new RSU[] { mockRsu };

               mockTim.getSnmp();
               result = mockSnmp;

               TIMController.createAndSend((SNMP) any, (RSU) any, anyString);
               result = null;
            }
         };
      } catch (Exception e) {
         fail("Unexpected Exception in expectations block: " + e);
      }

      try {
         tmc.timMessage("testMessage123");
         fail("Expected Exception");
      } catch (Exception e) {
         assertEquals(BadRequestException.class,e.getClass());
         assertEquals(BadRequestException.class + ": Empty response from RSU null", "class " +e.getMessage());
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }

}

package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimParameters;
import us.dot.its.jpo.ode.util.JsonUtils;

@RunWith(JMockit.class)
public class TravelerMessageControllerTest {
   @Mocked(stubOutClassInitialization = true)
   final DdsDepositor mockDdsDepositor = null;
   @Tested
   TravelerMessageController tmc;
   @Injectable
   OdeProperties mockOdeProperties;
   @Mocked
   J2735TravelerInputData mockTim;

   @Mocked
   ManagerAndControllerServices mockTimManagerService;

   @Injectable
   RSU mockRsu;
   @Mocked
   ResponseEvent mockResponseEvent;
   
   @Before
   public void setup() {
      new Expectations() {{
         mockTim.toString();
         result = "something";
         minTimes = 0;
       }};
   }
   @Ignore
   @Test
   public void nullRequestShouldLogAndThrowException(@Mocked final EventLogger eventLogger) {

       try {
           tmc.timMessage(null);
           fail("Expected timException");
       } catch (Exception e) {
           assertEquals(TimMessageException.class, e.getClass());
           assertEquals("TIM CONTROLLER- Endpoint received null request", e.getMessage());
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
           }
       };
   }
   @Ignore
   @Test
   public void nullResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

       new Expectations() {
           {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;
               
               mockTim.getRsus();
               result = new RSU[]{mockRsu};
               
               ManagerAndControllerServices.createAndSend((TimParameters)any, (SnmpProperties)any);
               result = null;
           }
       };

       try {
           assertNotNull(tmc.timMessage("testMessage123"));
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
           }
       };
   }
   @Ignore
   @Test
   public void nullGetResponseShouldLogAndReturn(@Mocked final JsonUtils jsonUtils) {

       new Expectations() {
           {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;
               
               mockTim.getRsus();
               result = new RSU[]{mockRsu};
               
               ManagerAndControllerServices.createAndSend((TimParameters)any, (SnmpProperties)any);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse();
               result = null;
           }
       };

       try {
           assertNotNull(tmc.timMessage("testMessage123"));
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
           }
       };
   }
   @Ignore
   @Test
   public void shouldLogSuccessWhenErrorStatus0(@Mocked final JsonUtils jsonUtils) {

       new Expectations() {
           {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;
               
               mockTim.getRsus();
               result = new RSU[]{mockRsu};
               
               ManagerAndControllerServices.createAndSend((TimParameters)any, (SnmpProperties)any);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse().getErrorStatus();
               result = 0;
           }
       };

       try {
           assertNotNull(tmc.timMessage("testMessage123"));
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
           }
       };
   }
   @Ignore
   @Test
   public void shouldLogFailureWhenErrorStatusNot0(@Mocked final JsonUtils jsonUtils) {

       new Expectations() {
           {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;
               
               mockTim.getRsus();
               result = new RSU[]{mockRsu};
               
               ManagerAndControllerServices.createAndSend((TimParameters)any, (SnmpProperties)any);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse().getErrorStatus();
               result = 17;
           }
       };

       try {
           assertNotNull(tmc.timMessage("testMessage123"));
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
               mockResponseEvent.getResponse().getErrorStatusText();
               times = 1;
           }
       };
   }
   @Ignore
   @Test
   public void shouldCatchParseExceptionAndLog(@Mocked final JsonUtils jsonUtils, @Mocked final GenericAddress genericAddress) {

       new Expectations() {
           {
               JsonUtils.fromJson(anyString, J2735TravelerInputData.class);
               result = mockTim;
               
               mockTim.getRsus();
               result = new RSU[]{mockRsu};
               
               GenericAddress.parse(anyString);
               result = new ParseException("testException123", anyInt);
           }
       };

       try {
           assertNotNull(tmc.timMessage("testMessage123"));
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }

       new Verifications() {
           {
               EventLogger.logger.info(anyString);
           }
       };
   }

}

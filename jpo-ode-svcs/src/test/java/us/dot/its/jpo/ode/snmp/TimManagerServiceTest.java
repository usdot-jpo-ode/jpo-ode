package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import ch.qos.logback.classic.Logger;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import us.dot.its.jpo.ode.ManagerAndControllerServices;

public class TimManagerServiceTest {

   // Create and send tests
   /**
    * Test that a null argument to createAndSend() short circuits, returns null,
    * and logs error
    */
   @Test
   public void createAndSendshouldReturnNullWhenGivenNullArguments(@Mocked SnmpProperties mockSnmpProperties,
         @Mocked final Logger logger) {

      TimParameters testParams = null;

      assertNull(ManagerAndControllerServices.createAndSend(testParams, mockSnmpProperties));

   }

   /**
    * Test that if initializing an SnmpSession returns null, null is returned
    * and an exception is logged
    */
   @Test
   public void createAndSendShouldReturnNullWhenSessionInitThrowsException(@Mocked TimParameters mockTimParameters,
         @Mocked SnmpProperties mockSnmpProperties, @Mocked final Logger logger, @Mocked SnmpSession mockSnmpSession) {

      IOException expectedException = new IOException("testException123");
      try {
         new Expectations() {
            {
               new SnmpSession((SnmpProperties) any);
               result = expectedException;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }

      assertNull(ManagerAndControllerServices.createAndSend(mockTimParameters, mockSnmpProperties));

      new Verifications() {
         {
            logger.error("TIM SERVICE - Failed to create SNMP session: {}", expectedException);
         }
      };

   }

   // Create PDU tests
   @Test
   public void createPDUshouldReturnNullWhenGivenNullParams() {

      TimParameters nullParams = null;
      ScopedPDU result = TimManagerService.createPDU(nullParams);
      assertNull(result);
   }

   @Test
   public void shouldCreatePDU() throws ParseException {

      String expectedResult = "[1.0.15628.4.1.4.1.2.3 = 11, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.8.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9, 1.0.15628.4.1.4.1.11.3 = 10]";

      String rsuSRMPsid = "11";
      int rsuSRMDsrcMsgId = 2;
      int rsuSRMTxMode = 3;
      int rsuSRMTxChannel = 4;
      int rsuSRMTxInterval = 5;
      String rsuSRMPayload = "88";
      int rsuSRMEnable = 9;
      int rsuSRMStatus = 10;

      TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
            rsuSRMTxInterval, "2017-12-02T17:47:11-05:00", "2017-12-02T17:47:11-05:00", rsuSRMPayload, rsuSRMEnable,
            rsuSRMStatus);

      ScopedPDU result = TimManagerService.createPDU(testParams);

      assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
      assertEquals(expectedResult, result.getVariableBindings().toString());
   }
   @Ignore
   @Test
   public void createAndSendShouldSendPDU(@Mocked TimParameters mockTimParameters,
         @Mocked SnmpProperties mockSnmpProperties, @Mocked final Logger logger, @Mocked SnmpSession mockSnmpSession,
         @Mocked ScopedPDU mockScopedPDU, @Mocked ResponseEvent mockResponseEvent) {

      try {
         new Expectations() {
            {
               TimManagerService.createPDU((TimParameters) any);
               result = mockScopedPDU;
               mockSnmpSession.set(mockScopedPDU, (Snmp) any, (TransportMapping) any, (UserTarget) any);
               result = mockResponseEvent;

            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }

      assertEquals(mockResponseEvent,
            ManagerAndControllerServices.createAndSend(mockTimParameters, mockSnmpProperties));
   }

   @Ignore
   @Test
   public void createAndSendShouldThrowPDUException(@Mocked TimParameters mockTimParameters,
         @Mocked SnmpProperties mockSnmpProperties, @Mocked final Logger logger, @Mocked SnmpSession mockSnmpSession,
         @Mocked ScopedPDU mockScopedPDU, @Mocked ResponseEvent mockResponseEvent) {

      IOException expectedException = new IOException("testException123");
      try {
         new Expectations() {
            {
               TimManagerService.createPDU((TimParameters) any);
               result = mockScopedPDU;
               mockSnmpSession.set(mockScopedPDU, (Snmp) any, (TransportMapping) any, (UserTarget) any);
               result = expectedException;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      System.out.println("test 2");
      assertNull(ManagerAndControllerServices.createAndSend(mockTimParameters, mockSnmpProperties));

      new Verifications() {
         {
            logger.error("TIM SERVICE - Error while sending PDU: {}", expectedException);
         }
      };
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TimManagerService> constructor = TimManagerService.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }
}

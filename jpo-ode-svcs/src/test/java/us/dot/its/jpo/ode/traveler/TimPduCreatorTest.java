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
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import ch.qos.logback.classic.Logger;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;

public class TimPduCreatorTest {
    

   // Create and send tests
   /**
    * Test that if initializing an SnmpSession returns null, null is returned
    * and an exception is logged
    * @throws TimPduCreatorException 
    * @throws IOException 
    */
   @Ignore // TODO
   @Test
   public void createAndSendShouldReturnNullWhenSessionInitThrowsException(
         @Mocked SNMP mockSNMP,
         @Mocked RSU mockRSU, @Mocked final Logger logger, 
         @Mocked SnmpSession mockSnmpSession) throws TimPduCreatorException {

      IOException expectedException = new IOException("testException123");
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = expectedException;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }

      try {
         assertNull(SnmpSession.createAndSend(mockSNMP, mockRSU, "", RequestVerb.POST));
         fail("Should have thrown IOException");
      } catch (IOException e) {
      }

   }

   @Test
   public void shouldCreatePDU() throws ParseException, TimPduCreatorException {

      String expectedResult = "[1.0.15628.4.1.4.1.2.3 = 11, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.8.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9, 1.0.15628.4.1.4.1.11.3 = 10]";
      String expectedResult2 = "[1.0.15628.4.1.4.1.2.3 = 11, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.8.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9]";

      
      String rsuSRMPsid = "11";
      int rsuSRMDsrcMsgId = 2;
      int rsuSRMTxMode = 3;
      int rsuSRMTxChannel = 4;
      int rsuSRMTxInterval = 5;
      String rsuSRMPayload = "88";
      int rsuSRMEnable = 9;
      int rsuSRMStatus = 10;

      SNMP testParams = new SNMP(
            rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
            rsuSRMTxInterval, "2017-12-02T17:47:11-05:00", "2017-12-02T17:47:11-05:00", 
            rsuSRMEnable, rsuSRMStatus);

      ScopedPDU result = TimPduCreator.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.POST);

      assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
      assertEquals(expectedResult, result.getVariableBindings().toString());

      ScopedPDU result2 = TimPduCreator.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.GET);

      assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result2.getType());
      assertEquals(expectedResult2, result2.getVariableBindings().toString());
   }
   @Ignore
   @Test
   public void createAndSendShouldSendPDU(@Mocked SNMP mockTimParameters,
         @Mocked RSU mockSnmpProperties, @Mocked final Logger logger, 
         @Mocked SnmpSession mockSnmpSession,
         @Mocked ScopedPDU mockScopedPDU, @Mocked ResponseEvent mockResponseEvent) throws TimPduCreatorException, IOException {

      try {
         new Expectations() {
            {
               TimPduCreator.createPDU((SNMP) any, anyString, anyInt, (RequestVerb) any);
               result = mockScopedPDU;
               mockSnmpSession.set(mockScopedPDU, (Snmp) any, (UserTarget) any, false);
               result = mockResponseEvent;

            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }

      assertEquals(mockResponseEvent,
         SnmpSession.createAndSend(
                  mockTimParameters, mockSnmpProperties, "", RequestVerb.POST));
   }

   @Test @Ignore
   public void createAndSendShouldThrowPDUException(@Mocked SNMP mockTimParameters,
         @Mocked RSU mockSnmpProperties, @Mocked final Logger logger, @Mocked SnmpSession mockSnmpSession,
         @Mocked ScopedPDU mockScopedPDU, @Mocked ResponseEvent mockResponseEvent) throws TimPduCreatorException, IOException {

      IOException expectedException = new IOException("testException123");
      try {
         new Expectations() {
            {
               TimPduCreator.createPDU((SNMP) any, anyString, anyInt, (RequestVerb) any);
               result = mockScopedPDU;
               mockSnmpSession.set(mockScopedPDU, (Snmp) any, (UserTarget) any, false);
               result = expectedException;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      System.out.println("test 2");
      assertNull(SnmpSession.createAndSend(
            mockTimParameters, mockSnmpProperties, "", RequestVerb.POST));

      new Verifications() {
         {
            logger.error("TIM SERVICE - Error while sending PDU: {}", expectedException);
         }
      };
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TimPduCreator> constructor = TimPduCreator.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected InvocationTargetException.class");
      } catch (Exception e) {
         assertTrue(e instanceof InvocationTargetException);
      }
   }
}

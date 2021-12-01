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

import java.io.IOException;
import java.util.Vector;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;

public class TimQueryControllerTest {

   @Tested
   TimQueryController testTimQueryController;

   @Injectable
   OdeProperties mockOdeProperties;

   @Capturing
   SnmpSession capturingSnmpSession;

   @Mocked
   Snmp mockSnmp;
   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockPDU;

   private String defaultRSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";

   @Test
   public void nullRequestShouldReturnError() {
      ResponseEntity<?> result = testTimQueryController.bulkQuery(null);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }

   @Test
   public void emptyRequestShouldReturnError() {
      ResponseEntity<?> result = testTimQueryController.bulkQuery("");
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }

   @Test
   public void snmpSessionExceptionShouldReturnError() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }

   @Test
   public void snmpSessionListenExceptionShouldReturnError() {
      try {
         new Expectations() {
            {
               capturingSnmpSession.startListen();
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }

   @Test
   public void testNullResponseReturnsTimeout() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = null;
         }
      };

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
   }

   @Test
   public void testNullResponseResponseReturnsTimeout() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = null;
         }
      };

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
   }

   @Test
   public void testSuccessfulQuery() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockPDU;

            mockPDU.getVariableBindings();
            result = new Vector<VariableBinding>();
         }
      };

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("indicies_set"));
   }

   @Test
   public void testSuccessfulPopulatedQuery() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockPDU;

            Vector<VariableBinding> fakeVector = new Vector<VariableBinding>();
            fakeVector.add(new VariableBinding());

            mockPDU.getVariableBindings();
            result = fakeVector;
         }
      };

      ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(defaultRSU);
      assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("indicies_set"));
   }
}

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
package us.dot.its.jpo.ode.pdm;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

public class PdmControllerTest {

   @Tested
   PdmController testPdmController;

   @Capturing
   JsonUtils capturingJsonUtils;

   @Capturing
   PdmUtil capturingPdmUtil;
   
   @Capturing
   SnmpSession capturingSnmpSession;

   @Mocked
   J2735PdmRequest mockJ2735PdmRequest;

   @Mocked
   RSU mockRSU;
   
   @Mocked
   ResponseEvent mockResponseEvent;

   @Test
   public void nullRequestShouldReturnBadRequest() {
      assertEquals(HttpStatus.BAD_REQUEST, testPdmController.pdmMessage(null).getStatusCode());
   }

   @Test
   public void checkJsonResponse1RSUTimeout() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Timeout\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
   
   @Test
   public void checkJsonResponse1RSUTimeoutResponse() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = mockResponseEvent;
               mockResponseEvent.getResponse();
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Timeout\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
   
   @Test
   public void checkJsonResponse1RSUSuccess() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse().getErrorStatus();
               result = 0;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit successful\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
   
   @Test
   public void checkJsonResponse1RSUMiscError() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse().getErrorStatus();
               result = 5;
               
               mockResponseEvent.getResponse().getErrorStatusText();
               result = "testError123";
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit failed: testError123\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
   
   @Test
   public void checkJsonResponseSendException() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = new IOException("iyoooException");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Exception occurred\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
   
   @Test
   public void checkJsonResponse2RSUSuccess() {
      try {
         new Expectations() {
            {
               JsonUtils.fromJson(anyString, J2735PdmRequest.class);
               result = mockJ2735PdmRequest;

               mockJ2735PdmRequest.getRsuList();
               result = new RSU[]{mockRSU, mockRSU};
               
               mockRSU.getRsuTarget();
               result = "127.0.0.1";
               
               capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = mockResponseEvent;
               
               mockResponseEvent.getResponse().getErrorStatus();
               result = 0;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit successful\"},{\"127.0.0.1\":\"Deposit successful\"}]}", testPdmController.pdmMessage("not a null string").getBody());
   }
}

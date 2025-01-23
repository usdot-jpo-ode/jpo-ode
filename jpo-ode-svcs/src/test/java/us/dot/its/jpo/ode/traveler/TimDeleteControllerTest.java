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
import static org.junit.Assert.fail;

import java.io.IOException;

import mockit.*;
import org.junit.jupiter.api.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;

import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;

public class TimDeleteControllerTest {
   
   @Tested
   TimDeleteController testTimDeleteController;

   @Injectable
   RsuProperties injectableRsuProperties;
   
   @Capturing
   SnmpSession capturingSnmpSession;
   
   @Mocked
   ResponseEvent mockResponseEvent;

   private String defaultRSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private String fourDot1RSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private String ntcip1218RSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

   private String defaultRSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private String fourDot1RSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private String ntcip1218RSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

   private String defaultRSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private String fourDot1RSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private String ntcip1218RSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";


   @Test
   public void deleteShouldReturnBadRequestWhenNull() {
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(null, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException_fourDot1RSU() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException_ntcip1218RSU() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new NullPointerException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException_fourDot1RSU() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new NullPointerException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException_ntcip1218RSU() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new NullPointerException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteShouldCatchSnmpSetException() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = new IOException("testSnmpException123");
      }};
      
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSnmpSetException_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = new IOException("testSnmpException123");
      }};
      
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSnmpSetException_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = new IOException("testSnmpException123");
      }};
      
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestTimeout1() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout1_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout1_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestTimeout2() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse();
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout2_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse();
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestTimeout2_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse();
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 0;
      }};

      //rsuUsername and rsuPassword are null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are not-null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(defaultRSUNotNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are blank
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(defaultRSUBlankUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 0;
      }};

      //rsuUsername and rsuPassword are null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are not-null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(fourDot1RSUNotNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are blank
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(fourDot1RSUBlankUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 0;
      }};

      //rsuUsername and rsuPassword are null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are not-null
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(ntcip1218RSUNotNullUserPass, 42).getStatusCode());
      //rsuUsername and rsuPassword are blank
      assertEquals(HttpStatus.OK, testTimDeleteController.deleteTim(ntcip1218RSUBlankUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestMessageAlreadyExists() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 12;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestMessageAlreadyExists_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 12;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestMessageAlreadyExists_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 12;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestInvalidIndex() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 10;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestInvalidIndex_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 10;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestInvalidIndex_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 10;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }
   
   @Test
   public void deleteTestUnknownErrorCode() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 5;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestUnknownErrorCode_fourDot1RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 5;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestUnknownErrorCode_ntcip1218RSU() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 5;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

}

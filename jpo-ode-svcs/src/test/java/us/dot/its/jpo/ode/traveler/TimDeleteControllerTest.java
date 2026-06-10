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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;

import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.snmp.SnmpSessionFactory;

@ExtendWith(MockitoExtension.class)
public class TimDeleteControllerTest {

   @Mock
   RsuProperties injectableRsuProperties;

   @Mock
   SnmpSessionFactory snmpSessionFactory;

   @Mock
   ResponseEvent mockResponseEvent;

   @InjectMocks
   TimDeleteController testTimDeleteController;

   private final String defaultRSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private final String fourDot1RSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private final String ntcip1218RSUNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

   private final String defaultRSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private final String fourDot1RSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private final String ntcip1218RSUNotNullUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

   private final String defaultRSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
   private final String fourDot1RSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
   private final String ntcip1218RSUBlankUserPass = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"\",\"rsuPassword\":\"\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

   private SnmpSession stubSessionReturning(ResponseEvent event) throws IOException {
      SnmpSession session = Mockito.mock(SnmpSession.class);
      when(session.set(any(PDU.class), any(), any(), anyBoolean())).thenReturn(event);
      when(snmpSessionFactory.create(any(RSU.class))).thenReturn(session);
      return session;
   }

   private SnmpSession stubSessionThrowingOnSet(IOException toThrow) throws IOException {
      SnmpSession session = Mockito.mock(SnmpSession.class);
      when(session.set(any(PDU.class), any(), any(), anyBoolean())).thenThrow(toThrow);
      when(snmpSessionFactory.create(any(RSU.class))).thenReturn(session);
      return session;
   }

   @Test
   public void deleteShouldReturnBadRequestWhenNull() {
      assertEquals(HttpStatus.BAD_REQUEST,
            testTimDeleteController.deleteTim(null, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new IOException("testException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException_fourDot1RSU() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new IOException("testException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException_ntcip1218RSU() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new IOException("testException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new NullPointerException("testException123"));
      assertEquals(HttpStatus.BAD_REQUEST,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException_fourDot1RSU() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new NullPointerException("testException123"));
      assertEquals(HttpStatus.BAD_REQUEST,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException_ntcip1218RSU() throws IOException {
      when(snmpSessionFactory.create(any(RSU.class)))
            .thenThrow(new NullPointerException("testException123"));
      assertEquals(HttpStatus.BAD_REQUEST,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSnmpSetException() throws IOException {
      stubSessionThrowingOnSet(new IOException("testSnmpException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSnmpSetException_fourDot1RSU() throws IOException {
      stubSessionThrowingOnSet(new IOException("testSnmpException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSnmpSetException_ntcip1218RSU() throws IOException {
      stubSessionThrowingOnSet(new IOException("testSnmpException123"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout1() throws IOException {
      stubSessionReturning(null);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout1_fourDot1RSU() throws IOException {
      stubSessionReturning(null);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout1_ntcip1218RSU() throws IOException {
      stubSessionReturning(null);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout2() throws IOException {
      when(mockResponseEvent.getResponse()).thenReturn(null);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout2_fourDot1RSU() throws IOException {
      when(mockResponseEvent.getResponse()).thenReturn(null);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestTimeout2_ntcip1218RSU() throws IOException {
      when(mockResponseEvent.getResponse()).thenReturn(null);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.REQUEST_TIMEOUT,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK() throws IOException {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(0);
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(defaultRSUNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(defaultRSUNotNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(defaultRSUBlankUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK_fourDot1RSU() throws IOException {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(0);
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(fourDot1RSUNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(fourDot1RSUNotNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(fourDot1RSUBlankUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestOK_ntcip1218RSU() throws IOException {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(0);
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(ntcip1218RSUNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(ntcip1218RSUNotNullUserPass, 42).getStatusCode());
      assertEquals(HttpStatus.OK,
            testTimDeleteController.deleteTim(ntcip1218RSUBlankUserPass, 42).getStatusCode());
   }

   @Test
   public void deleteTestMessageAlreadyExists() throws IOException {
      assertBadRequestForErrorCode(defaultRSUNullUserPass, 12);
   }

   @Test
   public void deleteTestMessageAlreadyExists_fourDot1RSU() throws IOException {
      assertBadRequestForErrorCode(fourDot1RSUNullUserPass, 12);
   }

   @Test
   public void deleteTestMessageAlreadyExists_ntcip1218RSU() throws IOException {
      assertBadRequestForErrorCode(ntcip1218RSUNullUserPass, 12);
   }

   @Test
   public void deleteTestInvalidIndex() throws IOException {
      assertBadRequestForErrorCode(defaultRSUNullUserPass, 10);
   }

   @Test
   public void deleteTestInvalidIndex_fourDot1RSU() throws IOException {
      assertBadRequestForErrorCode(fourDot1RSUNullUserPass, 10);
   }

   @Test
   public void deleteTestInvalidIndex_ntcip1218RSU() throws IOException {
      assertBadRequestForErrorCode(ntcip1218RSUNullUserPass, 10);
   }

   @Test
   public void deleteTestUnknownErrorCode() throws IOException {
      assertBadRequestForErrorCode(defaultRSUNullUserPass, 5);
   }

   @Test
   public void deleteTestUnknownErrorCode_fourDot1RSU() throws IOException {
      assertBadRequestForErrorCode(fourDot1RSUNullUserPass, 5);
   }

   @Test
   public void deleteTestUnknownErrorCode_ntcip1218RSU() throws IOException {
      assertBadRequestForErrorCode(ntcip1218RSUNullUserPass, 5);
   }

   private void assertBadRequestForErrorCode(String rsuJson, int errorStatus) throws IOException {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(errorStatus);
      when(pdu.getErrorStatusText()).thenReturn("mocked error");
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      stubSessionReturning(mockResponseEvent);
      assertEquals(HttpStatus.BAD_REQUEST,
            testTimDeleteController.deleteTim(rsuJson, 42).getStatusCode());
   }
}

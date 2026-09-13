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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;

import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

@ExtendWith(MockitoExtension.class)
public class PdmControllerTest {

   @InjectMocks
   PdmController testPdmController;

   @Mock
   J2735PdmRequest mockJ2735PdmRequest;

   @Mock
   RSU mockRSU;

   @Mock
   ResponseEvent mockResponseEvent;

   private static MockedConstruction<SnmpSession> mockSnmpSession(Consumer<SnmpSession> initializer) {
      return mockConstruction(SnmpSession.class, (mock, ctx) -> initializer.accept(mock));
   }

   @Test
   public void nullRequestShouldReturnBadRequest() {
      assertEquals(HttpStatus.BAD_REQUEST, testPdmController.pdmMessage(null).getStatusCode());
   }

   @Test
   public void checkJsonResponse1RSUTimeout() {
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean())).thenReturn(null);
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Timeout\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

   @Test
   public void checkJsonResponse1RSUTimeoutResponse() {
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      when(mockResponseEvent.getResponse()).thenReturn(null);
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean())).thenReturn(mockResponseEvent);
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Timeout\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

   @Test
   public void checkJsonResponse1RSUSuccess() {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(0);
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean())).thenReturn(mockResponseEvent);
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit successful\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

   @Test
   public void checkJsonResponse1RSUMiscError() {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(5);
      when(pdu.getErrorStatusText()).thenReturn("testError123");
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean())).thenReturn(mockResponseEvent);
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit failed: testError123\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

   @Test
   public void checkJsonResponseSendException() {
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean()))
                       .thenThrow(new IOException("iyoooException"));
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Exception occurred\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

   @Test
   public void checkJsonResponse2RSUSuccess() {
      PDU pdu = Mockito.mock(PDU.class);
      when(pdu.getErrorStatus()).thenReturn(0);
      when(mockJ2735PdmRequest.getRsuList()).thenReturn(new RSU[]{mockRSU, mockRSU});
      when(mockRSU.getRsuTarget()).thenReturn("127.0.0.1");
      when(mockResponseEvent.getResponse()).thenReturn(pdu);
      try (MockedStatic<JsonUtils> jsonStatic = mockStatic(JsonUtils.class);
           MockedStatic<PdmUtil> pdmUtilStatic = mockStatic(PdmUtil.class);
           MockedConstruction<SnmpSession> sessionCtor = mockSnmpSession(session -> {
              try {
                 when(session.set(any(), any(), any(), anyBoolean())).thenReturn(mockResponseEvent);
              } catch (IOException e) {
                 throw new AssertionError(e);
              }
           })) {
         jsonStatic.when(() -> JsonUtils.fromJson(anyString(), eq(J2735PdmRequest.class)))
               .thenReturn(mockJ2735PdmRequest);

         assertEquals("{\"rsu_responses\":[{\"127.0.0.1\":\"Deposit successful\"},{\"127.0.0.1\":\"Deposit successful\"}]}",
               testPdmController.pdmMessage("not a null string").getBody());
      }
   }

}

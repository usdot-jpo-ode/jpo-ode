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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.snmp.SnmpSessionFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TimQueryControllerTest {

    @Mock
    RsuProperties mockRsuProperties;

    @Mock
    SnmpSessionFactory snmpSessionFactory;

    @InjectMocks
    TimQueryController testTimQueryController;

    private final String defaultRSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
    private final String fourDot1RSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";
    private final String ntcip1218RSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

    @Test
    void nullRequestShouldReturnError() {
        ResponseEntity<?> result = testTimQueryController.bulkQuery(null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
    }

    @Test
    void emptyRequestShouldReturnError() {
        ResponseEntity<?> result = testTimQueryController.bulkQuery("");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
    }

    @Test
    void snmpSessionExceptionShouldReturnError() throws IOException {
        assertSessionFactoryThrowYields500(defaultRSU);
    }

    @Test
    void snmpSessionExceptionShouldReturnError_fourDot1RSU() throws IOException {
        assertSessionFactoryThrowYields500(fourDot1RSU);
    }

    @Test
    void snmpSessionExceptionShouldReturnError_ntcip1218RSU() throws IOException {
        assertSessionFactoryThrowYields500(ntcip1218RSU);
    }

    private void assertSessionFactoryThrowYields500(String rsuJson) throws IOException {
        when(snmpSessionFactory.create(any(RSU.class)))
                .thenThrow(new IOException("testException123"));
        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(rsuJson);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void snmpSessionListenExceptionShouldReturnError() throws IOException {
        assertListenIoThrowsYields500(defaultRSU);
    }

    @Test
    void snmpSessionListenExceptionShouldReturnError_fourDot1RSU() throws IOException {
        assertListenIoThrowsYields500(fourDot1RSU);
    }

    @Test
    void snmpSessionListenExceptionShouldReturnError_ntcip1218RSU() throws IOException {
        assertListenIoThrowsYields500(ntcip1218RSU);
    }

    private void assertListenIoThrowsYields500(String rsuJson) throws IOException {
        SnmpSession session = Mockito.mock(SnmpSession.class);
        Mockito.doThrow(new IOException("testException123")).when(session).startListen();
        when(snmpSessionFactory.create(any(RSU.class))).thenReturn(session);
        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(rsuJson);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void testNullResponseReturnsTimeout() throws IOException {
        assertSendReturning(null, defaultRSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testNullResponseReturnsTimeout_fourDot1RSU() throws IOException {
        assertSendReturning(null, fourDot1RSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testNullResponseReturnsTimeout_ntcip1218RSU() throws IOException {
        assertSendReturning(null, ntcip1218RSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testNullResponseResponseReturnsTimeout() throws IOException {
        ResponseEvent event = Mockito.mock(ResponseEvent.class);
        when(event.getResponse()).thenReturn(null);
        assertSendReturning(event, defaultRSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testNullResponseResponseReturnsTimeout_fourDot1RSU() throws IOException {
        ResponseEvent event = Mockito.mock(ResponseEvent.class);
        when(event.getResponse()).thenReturn(null);
        assertSendReturning(event, fourDot1RSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testNullResponseResponseReturnsTimeout_ntcip1218RSU() throws IOException {
        ResponseEvent event = Mockito.mock(ResponseEvent.class);
        when(event.getResponse()).thenReturn(null);
        assertSendReturning(event, ntcip1218RSU, HttpStatus.BAD_REQUEST, "Timeout, no response from RSU.");
    }

    @Test
    void testSuccessfulQuery() throws IOException {
        assertSuccessfulQuery(defaultRSU, new Vector<>());
    }

    @Test
    void testSuccessfulQuery_fourDot1RSU() throws IOException {
        assertSuccessfulQuery(fourDot1RSU, new Vector<>());
    }

    @Test
    void testSuccessfulQuery_ntcip1218RSU() throws IOException {
        assertSuccessfulQuery(ntcip1218RSU, new Vector<>());
    }

    @Test
    void testSuccessfulPopulatedQuery() throws IOException {
        Vector<VariableBinding> fakeVector = new Vector<>();
        fakeVector.add(new VariableBinding());
        assertSuccessfulQuery(defaultRSU, fakeVector);
    }

    @Test
    void testSuccessfulPopulatedQuery_fourDot1RSU() throws IOException {
        Vector<VariableBinding> fakeVector = new Vector<>();
        fakeVector.add(new VariableBinding());
        assertSuccessfulQuery(fourDot1RSU, fakeVector);
    }

    @Test
    void testSuccessfulPopulatedQuery_ntcip1218RSU() throws IOException {
        Vector<VariableBinding> fakeVector = new Vector<>();
        fakeVector.add(new VariableBinding());
        assertSuccessfulQuery(ntcip1218RSU, fakeVector);
    }

    private void assertSuccessfulQuery(String rsuJson, Vector<VariableBinding> bindings) throws IOException {
        PDU pdu = Mockito.mock(PDU.class);
        Mockito.doReturn(bindings).when(pdu).getVariableBindings();
        ResponseEvent event = Mockito.mock(ResponseEvent.class);
        when(event.getResponse()).thenReturn(pdu);
        assertSendReturning(event, rsuJson, HttpStatus.OK, "indicies_set");
    }

    private void assertSendReturning(ResponseEvent sendResult, String rsuJson, HttpStatus expectedStatus,
                                     String expectedBodyContains) throws IOException {
        when(mockRsuProperties.getSrmSlots()).thenReturn(1);
        Snmp snmp = Mockito.mock(Snmp.class);
        when(snmp.send(any(), any())).thenReturn(sendResult);
        SnmpSession session = Mockito.mock(SnmpSession.class);
        when(session.getSnmp()).thenReturn(snmp);
        when(snmpSessionFactory.create(any(RSU.class))).thenReturn(session);
        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(rsuJson);
        assertEquals(expectedStatus, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains(expectedBodyContains));
    }

    @Test
    void testPopulatedQuery_unrecognizedProtocol() {
        String unrecognizedProtocolRSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"banana\"}";

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(unrecognizedProtocolRSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Unrecognized protocol"));
    }

}

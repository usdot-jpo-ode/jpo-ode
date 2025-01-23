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

import mockit.*;
import org.junit.jupiter.api.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;

import java.io.IOException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class TimQueryControllerTest {

    @Tested
    TimQueryController testTimQueryController;

    @Injectable
    RsuProperties mockRsuProperties;

    @Capturing
    SnmpSession capturingSnmpSession;

    @Mocked
    Snmp mockSnmp;
    @Mocked
    ResponseEvent mockResponseEvent;
    @Mocked
    PDU mockPDU;

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
    void snmpSessionExceptionShouldReturnError() {
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
    void snmpSessionExceptionShouldReturnError_fourDot1RSU() {
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void snmpSessionExceptionShouldReturnError_ntcip1218RSU() {
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void snmpSessionListenExceptionShouldReturnError() {
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
    void snmpSessionListenExceptionShouldReturnError_fourDot1RSU() {
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void snmpSessionListenExceptionShouldReturnError_ntcip1218RSU() {
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
    }

    @Test
    void testNullResponseReturnsTimeout() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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
    void testNullResponseReturnsTimeout_fourDot1RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
                result = 1;

                capturingSnmpSession.getSnmp();
                result = mockSnmp;

                mockSnmp.send((PDU) any, (UserTarget) any);
                result = null;
            }
        };

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
    }

    @Test
    void testNullResponseReturnsTimeout_ntcip1218RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
                result = 1;

                capturingSnmpSession.getSnmp();
                result = mockSnmp;

                mockSnmp.send((PDU) any, (UserTarget) any);
                result = null;
            }
        };

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
    }

    @Test
    void testNullResponseResponseReturnsTimeout() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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
    void testNullResponseResponseReturnsTimeout_fourDot1RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
                result = 1;

                capturingSnmpSession.getSnmp();
                result = mockSnmp;

                mockSnmp.send((PDU) any, (UserTarget) any);
                result = mockResponseEvent;

                mockResponseEvent.getResponse();
                result = null;
            }
        };

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
    }

    @Test
    void testNullResponseResponseReturnsTimeout_ntcip1218RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
                result = 1;

                capturingSnmpSession.getSnmp();
                result = mockSnmp;

                mockSnmp.send((PDU) any, (UserTarget) any);
                result = mockResponseEvent;

                mockResponseEvent.getResponse();
                result = null;
            }
        };

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
    }

    @Test
    void testSuccessfulQuery() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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
    void testSuccessfulQuery_fourDot1RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("indicies_set"));
    }

    @Test
    void testSuccessfulQuery_ntcip1218RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("indicies_set"));
    }

    @Test
    void testSuccessfulPopulatedQuery() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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

    @Test
    void testSuccessfulPopulatedQuery_fourDot1RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(fourDot1RSU);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("indicies_set"));
    }

    @Test
    void testSuccessfulPopulatedQuery_ntcip1218RSU() throws IOException {
        new Expectations() {
            {
                mockRsuProperties.getSrmSlots();
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

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(ntcip1218RSU);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("indicies_set"));
    }

    @Test
    void testPopulatedQuery_unrecognizedProtocol() throws IOException {
        String unrecognizedProtocolRSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"banana\"}";

        ResponseEntity<String> actualResponse = testTimQueryController.bulkQuery(unrecognizedProtocolRSU);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().contains("Unrecognized protocol"));
    }

}

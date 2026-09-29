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
package us.dot.its.jpo.ode.rsuHealth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;

import us.dot.its.jpo.ode.heartbeat.RsuSnmp;

public class RsuSnmpTest {

    private Snmp mockSnmp;

    @BeforeEach
    public void setUpSnmp() {
        mockSnmp = mock(Snmp.class);
    }

    @Test
    public void shouldCreateSnmpV3Request() {
        String targetAddress = null;
        String targetOid = null;

        try {
            RsuSnmp.sendSnmpV3Request(targetAddress, targetOid, mockSnmp, null);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldThrowExceptionNullParameter() {
        try {
            RsuSnmp.sendSnmpV3Request(null, null, null, null);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass(), "Incorrect exception thrown.");
            assertTrue("Invalid SNMP request parameter".equals(e.getMessage()), "Incorrect exception message");
        }
    }

    @Test
    public void sendShouldCatchException() throws IOException {
        Snmp snmp = mock(Snmp.class);
        when(snmp.send(any(PDU.class), any(Target.class))).thenThrow(new IOException("testException123"));

        RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", snmp, null);
    }

    @Test
    public void sendShouldReturnConnectionError() throws IOException {
        String expectedMessage = "[ERROR] SNMP connection error";

        Snmp snmp = mock(Snmp.class);
        when(snmp.send(any(PDU.class), any(Target.class))).thenReturn(null);

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", snmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnEmptyResponse() throws IOException {
        String expectedMessage = "[ERROR] Empty SNMP response";

        Snmp snmp = mock(Snmp.class);
        ResponseEvent mockResponseEvent = mock(ResponseEvent.class);
        when(snmp.send(any(PDU.class), any(Target.class))).thenReturn(mockResponseEvent);
        when(mockResponseEvent.getResponse()).thenReturn(null);

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", snmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnVariableBindings() throws IOException {
        String inputMessage = "test_rsu_message_1";
        String expectedMessage = "[test_rsu_message_1]";

        Vector<String> fakeVector = new Vector<>();
        fakeVector.add(inputMessage);

        Snmp snmp = mock(Snmp.class);
        ResponseEvent mockResponseEvent = mock(ResponseEvent.class);
        PDU mockPDU = mock(PDU.class);
        when(snmp.send(any(PDU.class), any(Target.class))).thenReturn(mockResponseEvent);
        when(mockResponseEvent.getResponse()).thenReturn(mockPDU);
        when(mockPDU.getVariableBindings()).thenReturn((Vector) fakeVector);

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", snmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

}

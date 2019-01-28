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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.heartbeat.RsuSnmp;

public class RsuSnmpTest {
    
    @Mock private Snmp mockSnmp;

    @Before
    public void setUpSnmp() throws IOException {
        
        //MockitoAnnotations.initMocks(this);
        
        mockSnmp = mock(Snmp.class);    
    }

    @Test
    public void shouldCreateSnmpV3Request() throws IOException {
        
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
            assertEquals("Incorrect exception thrown.", IllegalArgumentException.class, e.getClass());
            assertTrue("Incorrect exception message", ("Invalid SNMP request parameter").equals(e.getMessage()));
        }
    }

    @Test
    public void sendShouldCatchException(@Injectable Snmp mockSnmp) {

        try {
            new Expectations() {
                {
                    mockSnmp.send((PDU) any, (Target) any);
                    // result = null;
                    result = new IOException("testException123");
                    // mockSnmp.close();
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp, null);
    }

    @Test
    public void sendShouldReturnConnectionError(@Injectable Snmp mockSnmp) {

        String expectedMessage = "[ERROR] SNMP connection error";

        try {
            new Expectations() {
                {
                    mockSnmp.send((PDU) any, (Target) any);
                    result = null;
                    // result = new IOException("testException123");
                    mockSnmp.close();
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnEmptyResponse(@Injectable Snmp mockSnmp, @Injectable ResponseEvent mockResponseEvent) {

        String expectedMessage = "[ERROR] Empty SNMP response";

        try {
            new Expectations() {
                {
                    mockSnmp.send((PDU) any, (Target) any);
                    result = mockResponseEvent;
                    // result = new IOException("testException123");
                    mockSnmp.close();

                    mockResponseEvent.getResponse();
                    result = null;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnVariableBindings(@Injectable Snmp mockSnmp, @Injectable ResponseEvent mockResponseEvent,
            @Injectable PDU mockPDU) {

        String inputMessage = "test_rsu_message_1";
        String expectedMessage = "[test_rsu_message_1]";
        
        Vector<String> fakeVector = new Vector<>();
        fakeVector.add(inputMessage);

        try {
            new Expectations() {
                {
                    mockSnmp.send((PDU) any, (Target) any);
                    result = mockResponseEvent;
                    
                    mockSnmp.close();

                    mockResponseEvent.getResponse();
                    result = mockPDU;
                    mockPDU.getVariableBindings();
                    result = fakeVector;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        String actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp, null);

        assertEquals(expectedMessage, actualMessage);
    }

}

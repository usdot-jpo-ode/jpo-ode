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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.snmp4j.Snmp;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.USM;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import us.dot.its.jpo.ode.heartbeat.RsuHealthController;
import us.dot.its.jpo.ode.heartbeat.RsuSnmp;

public class RsuHealthControllerTest {

    @Test
    public void shouldRefuseConnectionNullIp() {
        String testIp = null;
        String testOid = "1.1";

        try {
            RsuHealthController.heartBeat(null, testIp, testOid);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldRefuseConnectionNullOid() {
        String testIp = "127.0.0.1";
        String testOid = null;

        try {
            RsuHealthController.heartBeat(null, testIp, testOid);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldAttemptToSendNoAuth() throws IOException {
        SecurityModels mockSecurityModels = Mockito.mock(SecurityModels.class);
        try (MockedConstruction<DefaultUdpTransportMapping> udpCtor = mockConstruction(DefaultUdpTransportMapping.class);
             MockedConstruction<Snmp> snmpCtor = mockConstruction(Snmp.class);
             MockedConstruction<USM> usmCtor = mockConstruction(USM.class);
             MockedStatic<SecurityModels> secModelsStatic = mockStatic(SecurityModels.class);
             MockedStatic<RsuSnmp> rsuSnmpStatic = mockStatic(RsuSnmp.class)) {

            secModelsStatic.when(SecurityModels::getInstance).thenReturn(mockSecurityModels);

            try {
                RsuHealthController.heartBeat(null, "127.0.0.1", "1.1");
            } catch (IOException e) {
                fail("Unexpected Exception: " + e);
            }

            assertEquals(1, udpCtor.constructed().size(), "expected exactly one DefaultUdpTransportMapping");
            assertEquals(1, snmpCtor.constructed().size(), "expected exactly one Snmp instance");
            assertEquals(1, usmCtor.constructed().size(), "expected exactly one USM instance");
            verify(mockSecurityModels).addSecurityModel(usmCtor.constructed().get(0));
            rsuSnmpStatic.verify(() ->
                    RsuSnmp.sendSnmpV3Request(eq("127.0.0.1"), eq("1.1"), any(Snmp.class), isNull()));
            verify(snmpCtor.constructed().get(0)).close();
        }
    }

    @Test
    public void shouldAttemptToSendWithAuth() throws IOException {
        SecurityModels mockSecurityModels = Mockito.mock(SecurityModels.class);
        try (MockedConstruction<DefaultUdpTransportMapping> udpCtor = mockConstruction(DefaultUdpTransportMapping.class);
             MockedConstruction<Snmp> snmpCtor = mockConstruction(Snmp.class);
             MockedConstruction<USM> usmCtor = mockConstruction(USM.class);
             MockedStatic<SecurityModels> secModelsStatic = mockStatic(SecurityModels.class);
             MockedStatic<RsuSnmp> rsuSnmpStatic = mockStatic(RsuSnmp.class)) {

            secModelsStatic.when(SecurityModels::getInstance).thenReturn(mockSecurityModels);

            try {
                RsuHealthController.heartBeat("Basic YWxhZGRpbjpvcGVuc2VzYW1l", "127.0.0.1", "1.1");
            } catch (IOException e) {
                fail("Unexpected Exception: " + e);
            }

            assertEquals(1, udpCtor.constructed().size(), "expected exactly one DefaultUdpTransportMapping");
            assertEquals(1, snmpCtor.constructed().size(), "expected exactly one Snmp instance");
            assertEquals(1, usmCtor.constructed().size(), "expected exactly one USM instance");
            verify(mockSecurityModels).addSecurityModel(usmCtor.constructed().get(0));
            rsuSnmpStatic.verify(() ->
                    RsuSnmp.sendSnmpV3Request(eq("127.0.0.1"), eq("1.1"), any(Snmp.class), eq("aladdin")));
            verify(snmpCtor.constructed().get(0)).close();
        }
    }
}

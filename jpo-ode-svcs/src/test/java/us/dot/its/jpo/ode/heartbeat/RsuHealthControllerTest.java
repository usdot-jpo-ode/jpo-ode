package us.dot.its.jpo.ode.heartbeat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class RsuHealthControllerTest {

    @Test
    public void shouldThrowExceptionNullIp() {
        try {
            RsuHealthController.heartBeat(null, "1.1");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldThrowExceptionNullOid() {
        try {
            RsuHealthController.heartBeat("127.0.0.1", null);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldAttemptToSend(@Mocked final RsuSnmp mockRsuSnmp) {

        try {
            new Expectations() {
                {
                    new DefaultUdpTransportMapping();
                    new Snmp((TransportMapping) any);
                    new USM(null, null, maxTimes);
                    USM usm = new USM((SecurityProtocols) any, (OctetString) any, anyInt);
                    SecurityModels.getInstance().addSecurityModel(usm);
                    // mockTransport.listen();

                    RsuSnmp.sendSnmpV3Request(anyString, anyString, (Snmp) any);
                    result = null;
                }
            };
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            RsuHealthController.heartBeat("127.0.0.1", "1.1");
        } catch (IOException e) {
            fail("Unexpected Exception: " + e);
        }

    }

}

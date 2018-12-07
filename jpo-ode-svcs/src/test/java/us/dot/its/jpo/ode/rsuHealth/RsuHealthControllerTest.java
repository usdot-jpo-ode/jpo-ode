package us.dot.its.jpo.ode.rsuHealth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import mockit.Expectations;
import mockit.Mocked;
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
    public void shouldAttemptToSendNoAuth(@Mocked final RsuSnmp mockRsuSnmp) {

        try {
            new Expectations() {
                {
                    new DefaultUdpTransportMapping();
                    new Snmp((TransportMapping) any);
                    new USM(null, null, maxTimes);
                    USM usm = new USM((SecurityProtocols) any, (OctetString) any, anyInt);
                    SecurityModels.getInstance().addSecurityModel(usm);
                    // mockTransport.listen();

                    RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", (Snmp) any, null);
                    result = null;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        try {
            RsuHealthController.heartBeat(null, "127.0.0.1", "1.1");
        } catch (IOException e) {
            fail("Unexpected Exception: " + e);
        }

    }

    @Test
    public void shouldAttemptToSendWithAuth(@Mocked final RsuSnmp mockRsuSnmp) {

        try {
            new Expectations() {
                {
                    new DefaultUdpTransportMapping();
                    new Snmp((TransportMapping) any);
                    new USM(null, null, maxTimes);
                    USM usm = new USM((SecurityProtocols) any, (OctetString) any, anyInt);
                    SecurityModels.getInstance().addSecurityModel(usm);
                    // mockTransport.listen();

                    RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", (Snmp) any, "aladdin");
                    result = null;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }

        try {
            RsuHealthController.heartBeat("Basic YWxhZGRpbjpvcGVuc2VzYW1l", "127.0.0.1", "1.1");
        } catch (IOException e) {
            fail("Unexpected Exception: " + e);
        }

    }
}

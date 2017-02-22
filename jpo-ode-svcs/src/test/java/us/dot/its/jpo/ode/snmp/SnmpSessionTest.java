package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;

public class SnmpSessionTest {
    
    /**
     * Test that the constructor breaks when given a purely null props object
     */
    @Test
    public void constructorShouldFailWhenGivenNullPropsObject() {
        
        SnmpProperties nullProps = null;
        
        try {
            SnmpSession testSession = new SnmpSession(nullProps);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("Expected IllegalArgumentException", IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    @Test
    public void shouldSendMockSetCall() {
        
        Address testTarget = GenericAddress.parse("127.0.0.1" + "/161");
        String testUsername = "testUser";
        String testPassword = "testPass";
        int testRetries = 1;
        int testTimeout = 2000;
        int testVersion = SnmpConstants.version3;
        int testSecurityLevel = SecurityLevel.AUTH_NOPRIV;

        SnmpProperties testProps = new SnmpProperties(testTarget, testUsername, testPassword, testRetries, testTimeout,
                testVersion, testSecurityLevel);
        
        SnmpSession testSession = null;
        try {
            testSession = new SnmpSession(testProps);
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
        
        PDU mockPDU = Mockito.mock(PDU.class);
        Snmp mockSnmp = Mockito.mock(Snmp.class);
        TransportMapping mockTransport = Mockito.mock(TransportMapping.class);
        UserTarget mockTarget = Mockito.mock(UserTarget.class);
        
        try {
            testSession.set(mockPDU, mockSnmp, mockTransport, mockTarget);
        } catch (IOException e) {
            fail("Unexpected error: " + e);
        }
    }
}

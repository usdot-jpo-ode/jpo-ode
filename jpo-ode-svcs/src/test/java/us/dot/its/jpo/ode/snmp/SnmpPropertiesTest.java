package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.*;

import org.junit.Test;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;

/**
 * Basic unit test for SnmpProperties. Simply tests the two constructors and
 * getters and setters.
 *
 */
public class SnmpPropertiesTest {

    @Test
    public void shouldConstructWithMinimalContents() {

        Address testTarget = GenericAddress.parse("127.0.0.1" + "/161");
        String testUsername = "testUser";
        String testPassword = "testPass";

        int expectedRetries = 1;
        int expectedTimeout = 2000;
        int expectedVersion = SnmpConstants.version3;
        int expectedSecurityLevel = SecurityLevel.AUTH_NOPRIV;

        SnmpProperties actual = new SnmpProperties(testTarget, testUsername, testPassword);

        assertEquals("Incorrect target address", testTarget, actual.getTarget());
        assertEquals("Incorrect username", testUsername, actual.getUsername());
        assertEquals("Incorrect password", testPassword, actual.getPassword());
        assertEquals("Incorret retries", expectedRetries, actual.getRetries());
        assertEquals("Incorrect timeout", expectedTimeout, actual.getTimeout());
        assertEquals("Incorrect version", expectedVersion, actual.getVersion());
        assertEquals("Incorrect security level", expectedSecurityLevel, actual.getSecurityLevel());
    }

    @Test
    public void shouldConstructWithAllContents() {

        Address testTarget = GenericAddress.parse("127.0.0.1" + "/161");
        String testUsername = "testUser";
        String testPassword = "testPass";
        int testRetries = 1;
        int testTimeout = 2000;
        int testVersion = SnmpConstants.version3;
        int testSecurityLevel = SecurityLevel.AUTH_NOPRIV;

        SnmpProperties actual = new SnmpProperties(testTarget, testUsername, testPassword, testRetries, testTimeout,
                testVersion, testSecurityLevel);

        assertEquals("Incorrect target address", testTarget, actual.getTarget());
        assertEquals("Incorrect username", testUsername, actual.getUsername());
        assertEquals("Incorrect password", testPassword, actual.getPassword());
        assertEquals("Incorret retries", testRetries, actual.getRetries());
        assertEquals("Incorrect timeout", testTimeout, actual.getTimeout());
        assertEquals("Incorrect version", testVersion, actual.getVersion());
        assertEquals("Incorrect security level", testSecurityLevel, actual.getSecurityLevel());
    }
    
    @Test
    public void shouldSetProperties() {
        
        Address testTarget = GenericAddress.parse("127.0.0.1" + "/161");
        String testUsername = "testUser";
        String testPassword = "testPass";
        int testRetries = 1;
        int testTimeout = 2000;
        int testVersion = SnmpConstants.version3;
        int testSecurityLevel = SecurityLevel.AUTH_NOPRIV;

        SnmpProperties props = new SnmpProperties(testTarget, testUsername, testPassword, testRetries, testTimeout,
                testVersion, testSecurityLevel);
        
        Address modifiedTestTarget = GenericAddress.parse("192.168.1.1" + "/161");
        String modifiedTestUsername = "modifiedTestUser";
        String modifiedTestPassword = "modifiedTestPass";
        int modifiedTestRetries = 2;
        int modifiedTestTimeout = 3000;
        int modifiedTestVersion = SnmpConstants.version2c;
        int modifiedTestSecurityLevel = SecurityLevel.AUTH_PRIV;
        
        props.setTarget(modifiedTestTarget);
        props.setUsername(modifiedTestUsername);
        props.setPassword(modifiedTestPassword);
        props.setRetries(modifiedTestRetries);
        props.setTimeout(modifiedTestTimeout);
        props.setVersion(modifiedTestVersion);
        props.setSecurityLevel(modifiedTestSecurityLevel);
        
        assertEquals("Incorrect target address", modifiedTestTarget, props.getTarget());
        assertEquals("Incorrect username", modifiedTestUsername, props.getUsername());
        assertEquals("Incorrect password", modifiedTestPassword, props.getPassword());
        assertEquals("Incorret retries", modifiedTestRetries, props.getRetries());
        assertEquals("Incorrect timeout", modifiedTestTimeout, props.getTimeout());
        assertEquals("Incorrect version", modifiedTestVersion, props.getVersion());
        assertEquals("Incorrect security level", modifiedTestSecurityLevel, props.getSecurityLevel());
    }

}

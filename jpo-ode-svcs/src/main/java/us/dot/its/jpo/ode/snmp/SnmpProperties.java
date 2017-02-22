package us.dot.its.jpo.ode.snmp;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.Address;

/**
 * Container class for information required to set up SNMP pseudo-connection.
 * Defaults to one retry, two second timeout, SNMPv3, and AUTH NOPRIV security
 * level.
 */
public class SnmpProperties {

    private Address target;
    private String username;
    private String password;
    private int retries = 1;
    private int timeout = 2000;
    private int version = SnmpConstants.version3;
    private int securityLevel = SecurityLevel.AUTH_NOPRIV;

    public SnmpProperties(Address tar, String user, String pass) {
        this.target = tar;
        this.username = user;
        this.password = pass;
    }

    public SnmpProperties(Address tar, String user, String pass, int retries, int timeout, int version, int security) {
        this.target = tar;
        this.username = user;
        this.password = pass;
        this.retries = retries;
        this.timeout = timeout;
        this.version = version;
        this.securityLevel = security;
    }

    public Address getTarget() {
        return target;
    }

    public void setTarget(Address target) {
        this.target = target;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

}

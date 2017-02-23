package us.dot.its.jpo.ode.rsuHealth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

public class RsuSnmp {

    private static final String SNMP_USER = "v3user";
    private static final String SNMP_PASS = "password";
    private static Logger logger = LoggerFactory.getLogger(RsuSnmp.class);

    private RsuSnmp() {
    }

    public static String sendSnmpV3Request(String ip, String oid, Snmp snmp) {

        if (ip == null || oid == null || snmp == null) {
            logger.debug("Invalid SNMP request parameter");
            throw new IllegalArgumentException("Invalid SNMP request parameter");
        }
        
        // Setup snmp request
        Address targetAddress = GenericAddress.parse(ip + "/161");
        snmp.getUSM().addUser(new OctetString(SNMP_USER),
                new UsmUser(new OctetString(SNMP_USER), AuthMD5.ID, new OctetString(SNMP_PASS), null, null));

        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(2000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        target.setSecurityName(new OctetString(SNMP_USER));

        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        // Try to send the snmp request
        ResponseEvent responseEvent;
        try {
            responseEvent = snmp.send(pdu, target);
            snmp.close();
        } catch (Exception e) {
            responseEvent = null;
            logger.debug("SNMP4J library exception: " + e);
        }

        // Interpret snmp response
        String stringResponse;
        if (responseEvent == null) {
            logger.debug("SNMP connection error");
            stringResponse = "[ERROR] SNMP connection error";
        } else if (responseEvent.getResponse() == null) {
            logger.debug("Empty SNMP response");
            stringResponse = "[ERROR] Empty SNMP response";
        } else {
            stringResponse = responseEvent.getResponse().getVariableBindings().toString();
        }

        return stringResponse;
    }

}

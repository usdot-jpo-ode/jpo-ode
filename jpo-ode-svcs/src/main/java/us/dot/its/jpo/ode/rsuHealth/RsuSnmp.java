package us.dot.its.jpo.ode.rsuHealth;

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

    public static String sendSnmpV3Request(String ip, String oid, Snmp snmp) throws Exception {
        
        if (ip == null) {
            throw new IllegalArgumentException("[ERROR] RsuSnmp null ip");
        }
        if (oid == null) {
            throw new IllegalArgumentException("[ERROR] RsuSnmp null oid");
        }
        if (snmp == null) {
            throw new IllegalArgumentException("[ERROR] RsuSnmp null snmp");
        }

        // Setup SNMP session
        Address targetAddress = GenericAddress.parse(ip + "/161");

        // Create user with v3 credentials
        snmp.getUSM().addUser(new OctetString("v3user"),
                new UsmUser(new OctetString("v3user"), AuthMD5.ID, new OctetString("password"), null, null));

        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(5000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        target.setSecurityName(new OctetString("v3user"));

        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GETNEXT);

        // Send request
        ResponseEvent responseEvent = snmp.send(pdu, target);

        String stringResponse = null;

        if (responseEvent == null) {
            stringResponse = "[ERROR] Timeout";
        } else if (responseEvent.getResponse() == null) {
            stringResponse = "[ERROR] Empty response";
        } else {
            stringResponse = responseEvent.getResponse().getVariableBindings().toString();
        }

        snmp.close();
        return stringResponse;
    }

}

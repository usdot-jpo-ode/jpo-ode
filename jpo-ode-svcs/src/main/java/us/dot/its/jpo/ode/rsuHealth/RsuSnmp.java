package us.dot.its.jpo.ode.rsuHealth;

import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class RsuSnmp {
    
    public static ResponseEvent sendSnmpV3(String userName, String password, Address addr, OID oid, Snmp snmp) throws IOException {
        
        if (addr == null || oid == null || snmp == null) {
            throw new IllegalArgumentException("[ERROR] Null argument");
        }

        // Setup SNMP session
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.getUSM().addUser(
                new OctetString(userName), 
                new UsmUser(
                        new OctetString(password),
                        AuthMD5.ID,
                        new OctetString(password), null, null));
        
        // Create user with v3 credentials
        UserTarget target = new UserTarget();
        target.setAddress(addr);
        target.setRetries(1);
        target.setTimeout(2000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        target.setSecurityName(new OctetString("v3user"));
        
        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GETNEXT);

        // Send request
        ResponseEvent response = snmp.send(pdu, target);
        
        return response;
        
    }

}

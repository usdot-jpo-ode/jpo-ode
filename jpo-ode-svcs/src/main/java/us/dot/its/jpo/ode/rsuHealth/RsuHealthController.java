package us.dot.its.jpo.ode.rsuHealth;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RsuHealthController {

    @RequestMapping(value = "/rsuHeartbeat", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String heartBeat(@RequestParam("ip") String ip, @RequestParam("oid") String oid) throws Exception {
        
        String ipAddress = ip;
        String oidValue = oid;
        
        //return sendSnmpV2CRequest(ipAddress, oidValue);
        return sendSnmpV3Request(ipAddress, oidValue);
    }

    private String sendSnmpV2CRequest(String ip, String oid) throws Exception{

        // SNMP version 2c
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString("public"));
        comtarget.setVersion(SnmpConstants.version2c);
        comtarget.setAddress(new UdpAddress(ip + "/161"));
        comtarget.setTimeout(1000);

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        
        Snmp snmp = new Snmp(transport);
        
        ResponseEvent response = snmp.get(pdu, comtarget);
        
        // Send request
        String responseValue = "null";
        
        if (response != null && response.getResponse() != null) {
            responseValue = response.getResponse().getVariableBindings().toString();
        } 
        
        snmp.close();
        return responseValue;
    }
    
    private String sendSnmpV3Request(String ip, String oid) throws Exception {
        
        // Setup SNMP session
        Address targetAddress = GenericAddress.parse(ip + "/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(),
                          new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        transport.listen();
        
        // Create user with v3 credentials
        snmp.getUSM().addUser(
                new OctetString("v3user"), 
                new UsmUser(
                        new OctetString("v3user"),
                        AuthMD5.ID,
                        new OctetString("password"), null, null));
        
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
        ResponseEvent response = snmp.send(pdu, target);
        
        String responseValue = "null";
        
        if (response != null && response.getResponse() != null) {
            responseValue = response.getResponse().getVariableBindings().toString();
        } 
        
        snmp.close();
        return responseValue;
    }
}


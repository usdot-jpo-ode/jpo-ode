package us.dot.its.jpo.ode.rsuHealth;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
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
        
        return sendSnmpRequest(ipAddress, oidValue);
    }

    private String sendSnmpRequest(String ip, String oid) throws Exception{

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

}


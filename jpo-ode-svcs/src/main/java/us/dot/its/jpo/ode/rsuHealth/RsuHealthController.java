package us.dot.its.jpo.ode.rsuHealth;

import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
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
    public static String heartBeat(@RequestParam("ip") String ip, @RequestParam("oid") String oid) throws Exception {
        
        // Handle and prepare parameters
        if (ip == null || oid == null) {
            throw new IllegalArgumentException("[ERROR] Null argument");
        }
        
        String user = "v3user";
        String pw = "password";
        Address targetAddress = GenericAddress.parse(ip + "/161");
        OID targetOid = new OID(oid);
        
        // Prepare snmp session and send request
        Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        ResponseEvent responseEvent = RsuSnmp.sendSnmpV3(user, pw, targetAddress, targetOid, snmp);
        
        String stringResponse = null;
        
        if (responseEvent == null) {
            stringResponse = "[ERROR] Timeout";
        } else if (responseEvent.getResponse() == null) {
            stringResponse = "[ERROR] Empty response";
        } else {
            stringResponse = responseEvent.getResponse().getVariableBindings().toString();
        }
        
        return stringResponse;
    }
    
}


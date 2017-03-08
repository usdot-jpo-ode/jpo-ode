package us.dot.its.jpo.ode.snmp;

import java.text.ParseException;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestSnmpController {
    
    private TestSnmpController() {}

    @GetMapping("/test/snmp/{ip}/{message}")
    @ResponseBody
    public String sendSnmpRequest(@PathVariable("ip") String ip, @PathVariable("message") String message) throws ParseException {
        
   //        snmpset -v3 
   //        -u v3user 
   //        -a MD5 
   //        -A password 
   //        -l authNoPriv 
   //        RSU-MIB::rsuSRMStatus.3 = 4 
   //        rsuSRMTxChannel.3 = 178 
   //        rsuSRMTxMode.3 = 1 
   //        rsuSRMPsid.3 x "8300" 
   //        rsuSRMDsrcMsgId.3 = 31 
   //        rsuSRMTxInterval.3 = 1 
   //        rsuSRMDeliveryStart.3 x "010114111530" 
   //        rsuSRMDeliveryStop.3 x "010114130000" 
   //        rsuSRMPayload.3 x "0EFF82445566778899000000AABBCCDDEEFF00E00EA0C12A00" 
   //        rsuSRMEnable.3 = 1
        
        String rsuSRMPsid = "8300";
        int rsuSRMDsrcMsgId = 31;
        int rsuSRMTxMode = 1;
        int rsuSRMTxChannel = 178;
        int rsuSRMTxInterval = 1;
        String rsuSRMDeliveryStart = "010114111530";
        String rsuSRMDeliveryStop = "010114130000";
        String rsuSRMPayload = "0EFF82445566778899000000AABBCCDDEEFF00E00EA0C12A00";
        int rsuSRMEnable = 1;
        int rsuSRMStatus = 4;
        
        Address addr = GenericAddress.parse(ip + "/161");
        
        SnmpProperties testProps = new SnmpProperties(addr, "v3user", "password");
        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
                rsuSRMTxInterval, rsuSRMDeliveryStart, rsuSRMDeliveryStop, rsuSRMPayload,
                rsuSRMEnable, rsuSRMStatus);
        
        ResponseEvent response = TimManagerService.createAndSend(testParams, testProps);
        
        
        if (response != null && response.getResponse() != null) {
            
            return response.getResponse().toString();
            
        } else {
            
            return "{\"success\": false}";
            
        }
    }
}

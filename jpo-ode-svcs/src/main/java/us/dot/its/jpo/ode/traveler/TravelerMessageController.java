package us.dot.its.jpo.ode.traveler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimManagerService;
import us.dot.its.jpo.ode.snmp.TimParameters;


@Controller
public class TravelerMessageController {
    
    private TravelerMessageController() {}

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timMessage(@RequestBody String jsonString ) {
        
        Logger logger = LoggerFactory.getLogger(TravelerMessageController.class);

        if (jsonString == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null TIM");
        }
        
        // Step 1 - Serialize the JSON into a TIM object
        TravelerSerializer timObject = new TravelerSerializer(jsonString);
        logger.debug("TIM CONTROLLER - Serialized TIM: {}", timObject.getTravelerInformationObject().toString());
        
        // Step 2 - Encode the TIM object and then create the tim parameters object
        String rsuSRMPayload = null;
        try {
            rsuSRMPayload = timObject.getHexTravelerInformation();
            if (rsuSRMPayload == null) {
                throw new TimMessageException("Returned null string"); 
            }
        } catch (Exception e) {
            logger.error("TIM CONTROLLER - Failed to encode TIM: {}", e);
            return "{\"success\": false}";
        }
        logger.debug("TIM CONTROLLER - Encoded Hex TIM: {}", rsuSRMPayload);
        
        // TODO - Get these values from the JSON
        String rsuSRMPsid = "8300";
        int rsuSRMDsrcMsgId = 31;
        int rsuSRMTxMode = 1;
        int rsuSRMTxChannel = 178;
        int rsuSRMTxInterval = 1;
        String rsuSRMDeliveryStart = "010114111530";
        String rsuSRMDeliveryStop = "010114130000";
        int rsuSRMEnable = 1;
        int rsuSRMStatus = 4;
        
        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
                rsuSRMTxInterval, rsuSRMDeliveryStart, rsuSRMDeliveryStop, rsuSRMPayload,
                rsuSRMEnable, rsuSRMStatus);

        // Step 3 - Create the SNMP properties object
        JSONObject obj = new JSONObject(jsonString);
        JSONArray rsuList = obj.getJSONArray("RSUs");
        String ip = rsuList.getJSONObject(0).getString("target");
        Address addr = GenericAddress.parse(ip + "/161");
        SnmpProperties testProps = new SnmpProperties(addr, "v3user", "password");
        
        // Step 4 - Send the request out
        ResponseEvent response = TimManagerService.createAndSend(testParams, testProps);
        if (response != null && response.getResponse() != null) {
            return response.getResponse().toString();
        } else {
            logger.error("TIM CONTROLLER - Empty response from RSU");
            return "{\"success\": false}";
        }
    }
    
}
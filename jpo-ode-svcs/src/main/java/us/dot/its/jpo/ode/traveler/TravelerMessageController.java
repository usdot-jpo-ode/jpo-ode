package us.dot.its.jpo.ode.traveler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.util.JsonUtils;

@Controller
public class TravelerMessageController {
    
    private TravelerMessageController() {}

    @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timMessage(@RequestBody String jsonString ) {
        
        // TODO Loop through all RSUs in the JSON and create and send an SNMP message to each
        
        Logger logger = LoggerFactory.getLogger(TravelerMessageController.class);

        if (jsonString == null) {
            throw new IllegalArgumentException("[ERROR] TIM CONTROLLER - Endpoint received null TIM");
        }
        
        // Step 1 - Serialize the JSON into a TIM object
        TravelerInputData travelerInfo = null;
        try {
           travelerInfo = (TravelerInputData) JsonUtils.fromJson(jsonString, TravelerInputData.class);
        }
        catch (Exception e) {
           System.out.println("Error Deserializing TravelerInputData");
        }
        
        TravelerMessageBuilder build = new TravelerMessageBuilder();
        TravelerInformation traveler = null;
        logger.debug("TIM CONTROLLER - Serialized TIM: {}", travelerInfo.toString());
        try {
           traveler = build.buildTravelerInformation(travelerInfo);
        }
        catch (Exception e)
        {
           System.out.println("error Building travelerinfo");
        }
        
        // Step 2 - Populate the SnmpProperties object with SNMP preferences
        JSONObject obj = new JSONObject(jsonString);
        JSONArray rsuList = obj.getJSONArray("rsus");
        String ip = rsuList.getJSONObject(0).getString("target");
        String user = rsuList.getJSONObject(0).getString("username");
        String pass = rsuList.getJSONObject(0).getString("password");
        int retries = rsuList.getJSONObject(0).getInt("retries");
        int timeout = rsuList.getJSONObject(0).getInt("timeout");
        
        Address addr = GenericAddress.parse(ip + "/161");

        SnmpProperties testProps = new SnmpProperties(addr, user, pass, retries, timeout);
        
        // Step 2 - Encode the TIM object to a hex string
        String rsuSRMPayload = null;
        try {
            rsuSRMPayload = build.getHexTravelerInformation(traveler);
            if (rsuSRMPayload == null) {
                throw new TimMessageException("Returned null string"); 
            }
        } catch (Exception e) {
            logger.error("TIM CONTROLLER - Failed to encode TIM: {}", e);
            return "{\"success\": false}";
        }
        logger.debug("TIM CONTROLLER - Encoded Hex TIM: {}", rsuSRMPayload);
        
        // Step 3 - Populate the TimParameters object with OID values
        JSONObject snmpParams= obj.getJSONObject("snmp");

        String rsuSRMPsid = snmpParams.getString("rsuid");
        int rsuSRMDsrcMsgId = snmpParams.getInt("msgid");
        int rsuSRMTxMode = snmpParams.getInt("mode");
        int rsuSRMTxChannel = snmpParams.getInt("channel");
        int rsuSRMTxInterval = snmpParams.getInt("interval");
        String rsuSRMDeliveryStart = snmpParams.getString("deliverystart");
        String rsuSRMDeliveryStop = snmpParams.getString("deliverystop");
        int rsuSRMEnable = snmpParams.getInt("enable");
        int rsuSRMStatus = snmpParams.getInt("status");

        return "{\"success\": false}";
//        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
//                rsuSRMTxInterval, rsuSRMDeliveryStart, rsuSRMDeliveryStop, rsuSRMPayload,
//                rsuSRMEnable, rsuSRMStatus);

        // Step 4 - Send the request out
//        ResponseEvent response = TimManagerService.createAndSend(testParams, testProps);
//        if (response != null && response.getResponse() != null) {
//            return response.getResponse().toString();
//        } else {
//            logger.error("TIM CONTROLLER - Empty response from RSU");
//            return "{\"success\": false}";
//        }
    }
    
}
package us.dot.its.jpo.ode.traveler;

import org.json.JSONArray;
import org.json.JSONObject;
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


    private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timMessage(@RequestBody String jsonString ) throws Exception {

        if (jsonString == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null TIM");
        }

        TravelerSerializer timObject = new TravelerSerializer(jsonString);
        System.out.println(timObject.getTravelerInformationObject());

        System.out.print(timObject.getHexTravelerInformation());

        JSONObject obj = new JSONObject(jsonString);
        JSONArray rsuList = obj.getJSONArray("RSUs");

        // Needs to be a for loop
        String ip = rsuList.getJSONObject(0).getString("target");

        String rsuSRMPsid = "8300";
        int rsuSRMDsrcMsgId = 31;
        int rsuSRMTxMode = 1;
        int rsuSRMTxChannel = 178;
        int rsuSRMTxInterval = 1;
        String rsuSRMDeliveryStart = "010114111530";
        String rsuSRMDeliveryStop = "010114130000";
        String rsuSRMPayload = timObject.getHexTravelerInformation();
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
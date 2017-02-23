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
        JSONObject snmpParams= obj.getJSONObject("snmp");

        // TODO Needs to be a for loop

        // SNMP Properties
        String ip = rsuList.getJSONObject(0).getString("target");
        String user = rsuList.getJSONObject(0).getString("username");
        String pass = rsuList.getJSONObject(0).getString("password");
        int retries = Integer.parseInt(rsuList.getJSONObject(0).getString("retries"));
        int timeout = Integer.parseInt(rsuList.getJSONObject(0).getString("timeout"));

        // TIM parameters
        String rsuSRMPayload = timObject.getHexTravelerInformation();
        String rsuSRMPsid = snmpParams.getString("rsuid");
        int rsuSRMDsrcMsgId = Integer.parseInt(snmpParams.getString("msgid"));
        int rsuSRMTxMode = Integer.parseInt(snmpParams.getString("mode"));
        int rsuSRMTxChannel = Integer.parseInt(snmpParams.getString("channel"));
        int rsuSRMTxInterval = Integer.parseInt(snmpParams.getString("interval"));
        String rsuSRMDeliveryStart = snmpParams.getString("deliverystart");
        String rsuSRMDeliveryStop = snmpParams.getString("deliverystop");
        int rsuSRMEnable = Integer.parseInt(snmpParams.getString("enable"));
        int rsuSRMStatus = Integer.parseInt(snmpParams.getString("status"));



        Address addr = GenericAddress.parse(ip + "/161");

        SnmpProperties testProps = new SnmpProperties(addr, user, pass, retries, timeout);
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
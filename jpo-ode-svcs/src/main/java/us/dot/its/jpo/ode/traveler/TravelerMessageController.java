package us.dot.its.jpo.ode.traveler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimManagerService;
import us.dot.its.jpo.ode.snmp.TimParameters;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositClient;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.dds.SituationDataWarehouse;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.WebSocketClient;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;


@Controller
public class TravelerMessageController {

    private TravelerMessageController() {}

    @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
   private SituationDataWarehouse<DdsStatusMessage> sdw;

   @Autowired
    public TravelerMessageController(OdeProperties odeProperties) {
      super();
      try {
         WebSocketClient client = new DdsDepositClient();
         sdw = new SituationDataWarehouse<DdsStatusMessage>(odeProperties, client);
         
         OdeDepRequest depRequest = new OdeDepRequest();
         depRequest.setData("3081C68001108109000000000000003714830101A481AE3081AB800102A11BA119A0108004194FBA1F8104CE45CE2382020A0681020006820102820207DE830301C17084027D00850102A6108004194FC1988104CE45DA4082020A008702016E880100A92430228002000EA21CA01AA31804040CE205A104040ADA04F70404068004D60404034D0704AA3AA0383006A004800235293006A0048002010C3006A004800231283006A004800222113006A0048002010C3006A004800231203006A0048002221185021001");
         depRequest.setDataSource(DataSource.SDW);
         depRequest.setDataType(OdeDataType.AsnHex);
         depRequest.setEncodeType("hex");
         depRequest.setRequestType(OdeRequestType.Deposit);
         sdw.send(depRequest);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timMessage(@RequestBody String jsonString ) {

        // TODO Loop through all RSUs in the JSON and create and send an SNMP message to each
        
        Logger logger = LoggerFactory.getLogger(TravelerMessageController.class);

        if (jsonString == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null TIM");
        }
        
        
        // Step 1 - Serialize the JSON into a TIM object
        TravelerSerializer timObject = new TravelerSerializer(jsonString);


        // Step 2 - Populate the SnmpProperties object with SNMP preferences
        JSONObject obj = new JSONObject(jsonString);
        JSONArray rsuList = obj.getJSONArray("RSUs");
        String ip = rsuList.getJSONObject(0).getString("target");
        String user = rsuList.getJSONObject(0).getString("username");
        String pass = rsuList.getJSONObject(0).getString("password");
        int retries = Integer.parseInt(rsuList.getJSONObject(0).getString("retries"));
        int timeout = Integer.parseInt(rsuList.getJSONObject(0).getString("timeout"));
        
        Address addr = GenericAddress.parse(ip + "/161");

        SnmpProperties testProps = new SnmpProperties(addr, user, pass, retries, timeout);
        logger.debug("TIM CONTROLLER - Serialized TIM: {}", timObject.getTravelerInformationObject().toString());
        
        // Step 2 - Encode the TIM object to a hex string
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
    
        
        // Step 3 - Populate the TimParameters object with OID values
        JSONObject snmpParams= obj.getJSONObject("snmp");

        String rsuSRMPsid = snmpParams.getString("rsuid");
        int rsuSRMDsrcMsgId = Integer.parseInt(snmpParams.getString("msgid"));
        int rsuSRMTxMode = Integer.parseInt(snmpParams.getString("mode"));
        int rsuSRMTxChannel = Integer.parseInt(snmpParams.getString("channel"));
        int rsuSRMTxInterval = Integer.parseInt(snmpParams.getString("interval"));
        String rsuSRMDeliveryStart = snmpParams.getString("deliverystart");
        String rsuSRMDeliveryStop = snmpParams.getString("deliverystop");
        int rsuSRMEnable = Integer.parseInt(snmpParams.getString("enable"));
        int rsuSRMStatus = Integer.parseInt(snmpParams.getString("status"));
        
        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
                rsuSRMTxInterval, rsuSRMDeliveryStart, rsuSRMDeliveryStop, rsuSRMPayload,
                rsuSRMEnable, rsuSRMStatus);

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
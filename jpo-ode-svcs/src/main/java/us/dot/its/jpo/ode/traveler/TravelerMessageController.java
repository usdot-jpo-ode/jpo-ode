package us.dot.its.jpo.ode.traveler;

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

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.plugin.GenericSnmp.SNMP;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimParameters;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

import java.text.ParseException;

@Controller
public class TravelerMessageController {

    private static Logger logger = LoggerFactory.getLogger(TravelerMessageController.class);

    private OdeProperties odeProperties;

    private DdsDepositor<DdsStatusMessage> depositor;

    @Autowired
    public TravelerMessageController(OdeProperties odeProperties) {
        super();
        this.odeProperties = odeProperties;

        try {
            depositor = new DdsDepositor<>(this.odeProperties);
        } catch (Exception e) {
            logger.error("Error starting SDW depositor", e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
    public String timMessage(@RequestBody String jsonString) {
        if (jsonString == null) {
           String msg = "TIM CONTROLLER - Endpoint received null request";
           ManagerAndControllerServices.log(false, msg, null);
           throw new TimMessageException(msg);
        }

        // Step 1 - Serialize the JSON into a TIM object
        J2735TravelerInputData travelerinputData = null;
        try {
           travelerinputData = (J2735TravelerInputData) JsonUtils.fromJson(
                 jsonString, J2735TravelerInputData.class);
           String travelerinputDataStr = travelerinputData.toString();
           logger.debug("TIM CONTROLLER - Serialized TIM: {}", travelerinputDataStr);
        }
        catch (Exception e) {
           ManagerAndControllerServices.log(false, "Error Deserializing TravelerInputData", e);
           throw new TimMessageException(e);
        }
        
        OssTravelerMessageBuilder builder = new OssTravelerMessageBuilder();
        try {
           builder.buildTravelerInformation(travelerinputData);
        }
        catch (Exception e)
        {
           String msg = "Error Building travelerinfo";
           ManagerAndControllerServices.log(false, msg, e);
           throw new TimMessageException(e);
        }
        
        // Step 2 - Encode the TIM object to a hex string
        String rsuSRMPayload = null;
        try {
            rsuSRMPayload = builder.getHexTravelerInformation();
            if (rsuSRMPayload == null) {
               String msg = "TIM Builder returned null";
               ManagerAndControllerServices.log(false, msg, null);
               throw new TimMessageException(msg);
            }
        } catch (Exception e) {
           String msg = "TIM CONTROLLER - Failed to encode TIM";
           ManagerAndControllerServices.log(false, msg, e);
           throw new TimMessageException(e);
        }
        logger.debug("TIM CONTROLLER - Encoded Hex TIM: {}", rsuSRMPayload);

        boolean success = true;
        try {
           // Step 3 - Send TIM to all specified RSUs if rsus element exists
           if (travelerinputData.getSnmp() != null && travelerinputData.getRsus() != null) {
                 for (RSU rsu : travelerinputData.getRsus()) {
                    ResponseEvent response = sendToRSU(rsu, travelerinputData.getSnmp(), rsuSRMPayload);
                    if (response != null && response.getResponse() != null) {
                       String msg = String.format("RSU %1$s Response: %2$s", rsu.getrsuTarget(), response.getResponse());
                       EventLogger.logger.info(msg);
                       logger.info(msg);
                       depositToDDS(travelerinputData, rsuSRMPayload);
                   } else {
                      String msg = String.format("Empty response from RSU %1$s", rsu.getrsuTarget());
                      EventLogger.logger.error(msg);
                      logger.error(msg);
                      depositToDDS(travelerinputData, rsuSRMPayload);
                      throw new TimMessageException(msg);
                   }
                 }
           }
      } catch (Exception e) {
         String msg = "TIM CONTROLLER ERROR";
         ManagerAndControllerServices.log(false, msg , e);
         throw new TimMessageException(e);
      }

      return ManagerAndControllerServices.log(success, "TIM CONTROLLER RESPONSE", null);
    }

   private void depositToDDS(J2735TravelerInputData travelerinputData, String rsuSRMPayload)
         throws ParseException, DdsRequestManagerException, DdsClientException, WebSocketException,
         EncodeFailedException, EncodeNotSupportedException {
      // Step 4 - Step Deposit TIM to SDW if sdw element exists
        if (travelerinputData.getSdw() != null) {
           AsdMessage message = new AsdMessage(
               travelerinputData.getSnmp().getDeliverystart(), 
               travelerinputData.getSnmp().getDeliverystop(),
               rsuSRMPayload,
               travelerinputData.getSdw().getServiceRegion(),
               travelerinputData.getSdw().getTtl());
           depositor.deposit(message);
        }
   }

    private ResponseEvent sendToRSU(RSU rsu, SNMP snmp, String payload) throws ParseException {
       Address addr = GenericAddress.parse(rsu.getrsuTarget() + "/161");

       // Populate the SnmpProperties object with SNMP preferences
       SnmpProperties testProps = new SnmpProperties(addr, rsu.getrsuUsername(), rsu.getrsuPassword(), rsu.getrsuRetries(), rsu.getrsuTimeout());

       // Populate the TimParameters object with OID values
       TimParameters testParams = new TimParameters(
             snmp.getRsuid(), snmp.getMsgid(), snmp.getMode(), snmp.getChannel(),
             snmp.getInterval(), snmp.getDeliverystart(), snmp.getDeliverystop(), 
             payload, snmp.getEnable(), snmp.getStatus());

       // Send the request out
       return ManagerAndControllerServices.createAndSend(testParams, testProps);

    }
}
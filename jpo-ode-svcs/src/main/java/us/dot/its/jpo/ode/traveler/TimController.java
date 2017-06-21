package us.dot.its.jpo.ode.traveler;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssTravelerMessageBuilder;
import us.dot.its.jpo.ode.snmp.SNMP;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

@Controller
public class TimController {

   private static Logger logger = LoggerFactory.getLogger(TimController.class);

   private OdeProperties odeProperties;

   private DdsDepositor<DdsStatusMessage> depositor;

   @Autowired
   public TimController(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;

      try {
         depositor = new DdsDepositor<>(this.odeProperties);
      } catch (Exception e) {
         logger.error("Error starting SDW depositor", e);
      }
   }

   /**
    * Checks given RSU for all TIMs set
    * 
    * @param jsonString
    *           Request body containing RSU info
    * @return list of occupied TIM slots on RSU
    */
   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim/query", method = RequestMethod.POST)
   public String queryForTims(@RequestBody String jsonString) {
      
      HashMap<Integer, Boolean> resultTable = new HashMap<>();
      
      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);
      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM query SNMP session", e);
         String errMsg = "{success:false, \"error\": \"".concat(e.getMessage()).concat("\"}");
         logger.info("REST response: {}", errMsg);
         return errMsg;
      }
      
      // Repeatedly query the RSU to establish set rows
      // Repeatedly query the RSU "rows" to get their current status
      // 4 = set
      // 6 = deleted
      for (int i = 0; i < 15; i++) {
         PDU pdu = new ScopedPDU();
         pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(i)))));
         pdu.setType(PDU.GET);
         

         ResponseEvent rsuResponse = null;
         try {
            rsuResponse = ss.get(pdu, ss.getSnmp(), ss.getTransport(), ss.getTarget());
         } catch (IOException e) {
            logger.error("Error sending TIM query PDU to RSU", e);
            String errMsg = "{success:false, \"error\": \"".concat(e.getMessage()).concat("\"}");
            logger.info("TIM query REST response: {}", errMsg);
            return errMsg;
         }
         
         // TODO - check for SNMP error status
         
         Integer status = ((VariableBinding)(rsuResponse.getResponse().getVariableBindings().firstElement())).getVariable().toInt();
         
         if (4 == status) {
            resultTable.put(i, true);
         } else {
            resultTable.put(i, false);
         }
      }
      
      String msg = "{success:true, \"payload\": \"".concat(resultTable.toString()).concat("\"}");
      logger.info("SNMP query successful, REST response: {}", msg);
      
      return msg;
   }
   
   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim", method = RequestMethod.DELETE)
   public String deleteTim(@RequestBody String jsonString, @RequestParam("index") Integer index) {
      
      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);
      logger.info("TIM delete call, RSU info {}", queryTarget.toJson());
      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM delete SNMP session", e);
         String errMsg = "{success:false, \"error\": \"".concat(e.getMessage()).concat("\"}");
         logger.info("TIM delete REST response: {}", errMsg);
         return errMsg;
      }
      
      PDU pdu = new ScopedPDU();
      pdu.add(new VariableBinding( new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(index))), new Integer32(6) ));
      pdu.setType(PDU.SET);
      
      ResponseEvent rsuResponse = null;
      try {
         rsuResponse = ss.set(pdu, ss.getSnmp(), ss.getTransport(), ss.getTarget());
      } catch (IOException e) {
         logger.error("Error sending TIM query PDU to RSU", e);
         String errMsg = "{success:false, \"error\": \"".concat(e.getMessage()).concat("\"}");
         logger.info("TIM query REST response: {}", errMsg);
         return errMsg;
      }
      
      String returnMsg = "";
      if (rsuResponse.getResponse().getErrorStatus() != 0) {
         returnMsg = "{success:false, \"error\": \"".concat(rsuResponse.getResponse().getErrorStatusText()).concat("\"}");
      } else {
         returnMsg = "{success:true, \"variables\": \"".concat(rsuResponse.getResponse().getVariableBindings().toString()).concat("\"}");
      }
      
      return returnMsg;
   }

   /**
    * Deposit a TIM
    * 
    * @param jsonString
    *           TIM in JSON
    * @return list of success/failures
    */
   @ResponseBody
   @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
   @CrossOrigin
   public String timMessage(@RequestBody String jsonString) {
      logger.debug("Received request: {}", jsonString);
      if (StringUtils.isEmpty(jsonString)) {
         String msg = "Endpoint received null request";
         msg = ManagerAndControllerServices.log(false, msg, null);
         throw new BadRequestException(msg);
      }

      TravelerInputData travelerinputData = null;
      try {
         travelerinputData = (TravelerInputData) JsonUtils.fromJson(jsonString, TravelerInputData.class);

         logger.debug("J2735TravelerInputData: {}", travelerinputData.toJson(true));

      } catch (Exception e) {
         throw new BadRequestException(ManagerAndControllerServices.log(false, "Invalid Request Body", e));
      }

      OssTravelerMessageBuilder builder = new OssTravelerMessageBuilder();
      try {
         builder.buildTravelerInformation(travelerinputData.getTim());
      } catch (Exception e) {
         String msg = "Invalid Traveler Information Message data value in the request";
         throw new BadRequestException(ManagerAndControllerServices.log(false, msg, e), e);
      }

      // Step 2 - Encode the TIM object to a hex string
      String rsuSRMPayload = null;
      try {
         rsuSRMPayload = builder.encodeTravelerInformationToHex();
      } catch (Exception e) {
         String msg = "Failed to encode Traveler Information Message";
         throw new BadRequestException(ManagerAndControllerServices.log(false, msg, e), e);
      }
      logger.debug("Encoded Hex TIM: {}", rsuSRMPayload);

      HashMap<String, String> responseList = new HashMap<>();

      for (RSU curRsu : travelerinputData.getRsus()) {

         ResponseEvent response = null;
         try {
            response = createAndSend(travelerinputData.getSnmp(), curRsu, travelerinputData.getTim().getIndex(), rsuSRMPayload);

            if (null == response || null == response.getResponse()) {
               responseList.put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false, "No response from RSU IP=" + curRsu.getRsuTarget(), null));
            } else if (0 == response.getResponse().getErrorStatus()) {
               responseList
                     .put(curRsu.getRsuTarget(),
                           ManagerAndControllerServices.log(true, "SNMP deposit successful. RSU IP = "
                                 + curRsu.getRsuTarget() + ", Status Code: " + response.getResponse().getErrorStatus(),
                                 null));
            } else {
               responseList.put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false,
                           "SNMP deposit failed for RSU IP " + curRsu.getRsuTarget() + ", error code="
                                 + response.getResponse().getErrorStatus() + "("
                                 + response.getResponse().getErrorStatusText() + ")",
                           null));
            }

         } catch (Exception e) {
            responseList.put(curRsu.getRsuTarget(),
                  ManagerAndControllerServices.log(false, "Exception while sending message to RSU", e));
         }
      }

      try {
         depositToDDS(travelerinputData, rsuSRMPayload);
         responseList.put("SDW", ManagerAndControllerServices.log(true, "Deposit to SDW was successful", null));
      } catch (Exception e) {
         String msg = "Error depositing to SDW";
         responseList.put("SDW", ManagerAndControllerServices.log(false, msg, e));
      }

      return responseList.toString();
   }

   private void depositToDDS(TravelerInputData travelerinputData, String rsuSRMPayload)
         throws ParseException, DdsRequestManagerException, DdsClientException, WebSocketException,
         EncodeFailedException, EncodeNotSupportedException {
      // Step 4 - Step Deposit TIM to SDW if sdw element exists
      if (travelerinputData.getSdw() != null) {
         AsdMessage message = new AsdMessage(travelerinputData.getSnmp().getDeliverystart(),
               travelerinputData.getSnmp().getDeliverystop(), rsuSRMPayload,
               travelerinputData.getSdw().getServiceRegion(), travelerinputData.getSdw().getTtl());
         depositor.deposit(message);
      }
   }

   /**
    * Create an SNMP session given the values in
    * 
    * @param tim
    *           - The TIM parameters (payload, channel, mode, etc)
    * @param props
    *           - The SNMP properties (ip, username, password, etc)
    * @return ResponseEvent
    * @throws TimPduCreatorException
    * @throws IOException
    */
   public static ResponseEvent createAndSend(SNMP snmp, RSU rsu, int index, String payload)
         throws IOException, TimPduCreatorException {

      SnmpSession session = new SnmpSession(rsu);

      // Send the PDU
      ResponseEvent response = null;
      ScopedPDU pdu = TimPduCreator.createPDU(snmp, payload, index);
      response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
      return response;
   }
}
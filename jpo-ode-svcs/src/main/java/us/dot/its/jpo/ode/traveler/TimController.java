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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
   public ResponseEntity<?> queryForTims(@RequestBody String jsonString) { // NOSONAR

      if (null == jsonString) {
         logger.error("Empty request");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty request");
      }

      HashMap<Integer, Boolean> resultTable = new HashMap<>();
      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM query SNMP session", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
      } catch (NullPointerException e) {
         logger.error("TIM query error, malformed JSON", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed JSON");
      }

      // Repeatedly query the RSU to establish set rows
      for (int i = 0; i < 100; i++) { // TODO hardcoded number
         PDU pdu = new ScopedPDU();
         pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(i)))));
         pdu.setType(PDU.GET);

         ResponseEvent rsuResponse = null;
         try {
            rsuResponse = ss.get(pdu, ss.getSnmp(), ss.getTarget(), true);
         } catch (IOException e) {
            logger.error("Error sending TIM query PDU to RSU", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
         }

         if (null == rsuResponse || null == rsuResponse.getResponse()) {
            logger.error("Timeout error, no response from RSU.");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("No response from RSU.");
         }

         Integer status = ((VariableBinding) (rsuResponse.getResponse().getVariableBindings().firstElement()))
               .getVariable().toInt();

         if (1 == status) { // 1 == set, 129 == unset
            resultTable.put(i, true);
         }
      }
      
      try {
         ss.endSession();
      } catch (IOException e) {
         logger.error("Error closing SNMP session", e);
      }

      logger.info("TIM query successful: {}", resultTable.keySet());
      return ResponseEntity.status(HttpStatus.OK).body("{\"indicies_set\",".concat(resultTable.keySet().toString()).concat("}"));
   }

   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim", method = RequestMethod.DELETE)
   public ResponseEntity<?> deleteTim(@RequestBody String jsonString, @RequestParam(value="index", required=true) Integer index) { // NOSONAR
      
      if (null == jsonString) {
         logger.error("Empty request");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty request.");
      }

      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      logger.info("TIM delete call, RSU info {}", queryTarget);
      
      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM delete SNMP session", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
      } catch (NullPointerException e) {
         logger.error("TIM query error, malformed JSON", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed JSON");
      }

      PDU pdu = new ScopedPDU();
      pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(index))), new Integer32(6)));
      pdu.setType(PDU.SET);

      ResponseEvent rsuResponse = null;
      try {
         rsuResponse = ss.set(pdu, ss.getSnmp(), ss.getTarget(), false);
      } catch (IOException e) {
         logger.error("Error sending TIM query PDU to RSU", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
      }
      
      // Try to explain common errors
      HttpStatus returnCode = null;
      String bodyMsg = "";
      if (null == rsuResponse || null == rsuResponse.getResponse()) {
         // Timeout
         returnCode = HttpStatus.REQUEST_TIMEOUT;
         bodyMsg = "Timeout";
      } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
         // Success
         returnCode = HttpStatus.OK;
         bodyMsg = "{\"deleted_msg\":\"".concat(Integer.toString(index)).concat("\"}");
      } else if (rsuResponse.getResponse().getErrorStatus() == 12) {
         // Message previously deleted or doesn't exist
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = "No message at index ".concat(Integer.toString(index));
      } else if (rsuResponse.getResponse().getErrorStatus() == 10) {
         // Invalid index
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = "Invalid index ".concat(Integer.toString(index));
      } else {
         // Misc error
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = rsuResponse.getResponse().getErrorStatusText();
      }
      
      logger.info("Delete call response code: {}, message: {}", returnCode, bodyMsg);

      return ResponseEntity.status(returnCode).body(bodyMsg);
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
            response = createAndSend(travelerinputData.getSnmp(), curRsu, travelerinputData.getTim().getIndex(),
                  rsuSRMPayload);

            if (null == response || null == response.getResponse()) {
               responseList.put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false, "No response from RSU IP=" + curRsu.getRsuTarget(), null));
            } else if (0 == response.getResponse().getErrorStatus()) {
               responseList
                     .put(curRsu.getRsuTarget(),
                           ManagerAndControllerServices.log(true, "SNMP deposit successful. RSU IP = "
                                 + curRsu.getRsuTarget() + ", Status Code: " + response.getResponse().getErrorStatus() + " (no error)",
                                 null));
            } else if (5 == response.getResponse().getErrorStatus()) {
               responseList
               .put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false, "SNMP deposit failed for RSU IP " + curRsu.getRsuTarget() + ", message already exists at index " + travelerinputData.getTim().getIndex(), null));
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
      response = session.set(pdu, session.getSnmp(), session.getTarget(), false);
      return response;
   }

}
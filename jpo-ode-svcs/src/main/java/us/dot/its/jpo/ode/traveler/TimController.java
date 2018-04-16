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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputData;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimSerializer;

@Controller
public class TimController {

   public static class TimControllerException extends Exception {

      private static final long serialVersionUID = 1L;

      public TimControllerException(String errMsg, Exception e) {
         super(errMsg, e);
      }

   }

   private static final Logger logger = LoggerFactory.getLogger(TimController.class);

   private static final String ERRSTR = "error";
   private static final String WARNING = "warning";

   private OdeProperties odeProperties;
   private MessageProducer<String, String> stringMsgProducer;
   private MessageProducer<String, OdeObject> timProducer;

   @Autowired
   public TimController(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;

      this.stringMsgProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
            odeProperties.getKafkaProducerType());
      this.timProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, OdeTimSerializer.class.getName());
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
   public synchronized ResponseEntity<String> bulkQuery(@RequestBody String jsonString) { // NOSONAR

      if (null == jsonString || jsonString.isEmpty()) {
         logger.error("Empty request.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Empty request."));
      }

      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      SnmpSession snmpSession = null;
      try {
         snmpSession = new SnmpSession(queryTarget);
         snmpSession.startListen();
      } catch (IOException e) {
         logger.error("Error creating SNMP session.", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(jsonKeyValue(ERRSTR, "Failed to create SNMP session."));
      }

      PDU pdu0 = new ScopedPDU();
      pdu0.setType(PDU.GET);
      PDU pdu1 = new ScopedPDU();
      pdu1.setType(PDU.GET);

      for (int i = 0; i < odeProperties.getRsuSrmSlots() - 50; i++) {
         pdu0.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(i)))));
      }

      for (int i = 50; i < odeProperties.getRsuSrmSlots(); i++) {
         pdu1.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(i)))));
      }

      ResponseEvent response0 = null;
      ResponseEvent response1 = null;
      try {
         response0 = snmpSession.getSnmp().send(pdu0, snmpSession.getTarget());
         response1 = snmpSession.getSnmp().send(pdu1, snmpSession.getTarget());
      } catch (IOException e) {
         logger.error("Error creating SNMP session.", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(jsonKeyValue(ERRSTR, "Failed to create SNMP session."));
      }

      // Process response
      if (response0 == null || response0.getResponse() == null || response1 == null
            || response1.getResponse() == null) {
         logger.error("RSU query failed, timeout.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(jsonKeyValue(ERRSTR, "Timeout, no response from RSU."));
      }

      HashMap<String, Boolean> resultsMap = new HashMap<>();
      for (Object vbo : response0.getResponse().getVariableBindings().toArray()) {
         VariableBinding vb = (VariableBinding) vbo;
         if (vb.getVariable().toInt() == 1) {
            resultsMap.put(vb.getOid().toString().substring(21), true);
         }
      }

      for (Object vbo : response1.getResponse().getVariableBindings().toArray()) {
         VariableBinding vb = (VariableBinding) vbo;
         if (vb.getVariable().toInt() == 1) {
            resultsMap.put(vb.getOid().toString().substring(21), true);
         }
      }

      try {
         snmpSession.endSession();
      } catch (IOException e) {
         logger.error("Error closing SNMP session.", e);
      }

      logger.info("RSU query successful: {}", resultsMap.keySet());
      return ResponseEntity.status(HttpStatus.OK).body(jsonKeyValue("indicies_set", resultsMap.keySet().toString()));
   }

   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim", method = RequestMethod.DELETE)
   public ResponseEntity<String> deleteTim(@RequestBody String jsonString,
         @RequestParam(value = "index", required = true) Integer index) { // NOSONAR

      if (null == jsonString) {
         logger.error("Empty request");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Empty request"));
      }

      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      logger.info("TIM delete call, RSU info {}", queryTarget);

      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM delete SNMP session", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonKeyValue(ERRSTR, e.getMessage()));
      } catch (NullPointerException e) {
         logger.error("TIM query error, malformed JSON", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Malformed JSON"));
      }

      PDU pdu = new ScopedPDU();
      pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(index))), new Integer32(6)));
      pdu.setType(PDU.SET);

      ResponseEvent rsuResponse = null;
      try {
         rsuResponse = ss.set(pdu, ss.getSnmp(), ss.getTarget(), false);
      } catch (IOException e) {
         logger.error("Error sending TIM query PDU to RSU", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonKeyValue(ERRSTR, e.getMessage()));
      }

      // Try to explain common errors
      HttpStatus returnCode = null;
      String bodyMsg = "";
      if (null == rsuResponse || null == rsuResponse.getResponse()) {
         // Timeout
         returnCode = HttpStatus.REQUEST_TIMEOUT;
         bodyMsg = jsonKeyValue(ERRSTR, "Timeout.");
      } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
         // Success
         returnCode = HttpStatus.OK;
         bodyMsg = jsonKeyValue("deleted_msg", Integer.toString(index));
      } else if (rsuResponse.getResponse().getErrorStatus() == 12) {
         // Message previously deleted or doesn't exist
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, "No message at index ".concat(Integer.toString(index)));
      } else if (rsuResponse.getResponse().getErrorStatus() == 10) {
         // Invalid index
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, "Invalid index ".concat(Integer.toString(index)));
      } else {
         // Misc error
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, rsuResponse.getResponse().getErrorStatusText());
      }

      logger.info("Delete call response code: {}, message: {}", returnCode, bodyMsg);

      return ResponseEntity.status(returnCode).body(bodyMsg);
   }

   /**
    * Send a TIM with the appropriate deposit type, ODE.PUT or ODE.POST
    * 
    * @param jsonString
    * @param verb
    * @return
    */
   public ResponseEntity<String> depositTim(String jsonString, int verb) {
      // Check empty
      if (null == jsonString || jsonString.isEmpty()) {
         String errMsg = "Empty request.";
         logger.error(errMsg);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      OdeTravelerInputData travelerInputData = null;
      try {
         // Convert JSON to POJO
         travelerInputData = (OdeTravelerInputData) JsonUtils.fromJson(jsonString, OdeTravelerInputData.class);
         if (travelerInputData.getOde() == null) {
            travelerInputData.setOde(new ODE());
         }

         travelerInputData.getOde().setVerb(verb);

         logger.debug("J2735TravelerInputData: {}", jsonString);

      } catch (Exception e) {
         String errMsg = "Malformed or non-compliant JSON.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      // Add metadata to message and publish to kafka
      OdeMsgPayload timDataPayload = new OdeMsgPayload(travelerInputData.getTim());
      OdeMsgMetadata timMetadata = new OdeMsgMetadata(timDataPayload);
      OdeTimData odeTimData = new OdeTimData(timMetadata, timDataPayload);
      timProducer.send(odeProperties.getKafkaTopicOdeTimBroadcastPojo(), null, odeTimData);

      // Short circuit
      // If the TIM has no RSU/SNMP or SDW structures, we are done
      if ((travelerInputData.getRsus() == null || travelerInputData.getSnmp() == null)
            && travelerInputData.getSdw() == null) {
         String warningMsg = "Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to POJO broadcast stream.";
         logger.warn(warningMsg);
         return ResponseEntity.status(HttpStatus.OK).body(jsonKeyValue(WARNING, warningMsg));
      }

      // Craft ASN-encodable TIM
      ObjectNode encodableTid;
      try {
         encodableTid = TravelerMessageFromHumanToAsnConverter
               .changeTravelerInformationToAsnValues(JsonUtils.toObjectNode(travelerInputData.toJson()));

         logger.debug("Encodable TravelerInputData: {}", encodableTid);

      } catch (JsonUtilsException e) {
         String errMsg = "Error converting to encodable TIM.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      try {
         String xmlMsg = convertToXml(encodableTid);
         stringMsgProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, xmlMsg);
      } catch (JsonUtilsException | XmlUtilsException | ParseException e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      return ResponseEntity.status(HttpStatus.OK).body(jsonKeyValue("Success", "true"));
   }

   /**
    * Update an already-deposited TIM
    * 
    * @param jsonString
    *           TIM in JSON
    * @return list of success/failures
    */
   @ResponseBody
   @RequestMapping(value = "/tim", method = RequestMethod.PUT, produces = "application/json")
   @CrossOrigin
   public ResponseEntity<String> updateTim(@RequestBody String jsonString) {

      return depositTim(jsonString, ODE.PUT);
   }

   /**
    * Deposit a new TIM
    * 
    * @param jsonString
    *           TIM in JSON
    * @return list of success/failures
    */
   @ResponseBody
   @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
   @CrossOrigin
   public ResponseEntity<String> postTim(@RequestBody String jsonString) {

      return depositTim(jsonString, ODE.POST);
   }

   /**
    * Takes in a key, value pair and returns a valid json string such as
    * {"error":"message"}
    * 
    * @param key
    * @param value
    * @return
    */
   public static String jsonKeyValue(String key, String value) {
      return "{\"" + key + "\":\"" + value + "\"}";
   }

   private String convertToXml(ObjectNode encodableTidObj)
         throws JsonUtilsException, XmlUtilsException, ParseException {

      TravelerInputData inOrderTid = (TravelerInputData) JsonUtils.jacksonFromJson(encodableTidObj.toString(),
            TravelerInputData.class);
      logger.debug("In order tim: {}", inOrderTid);
      ObjectNode inOrderTidObj = JsonUtils.toObjectNode(inOrderTid.toJson());

      JsonNode timObj = inOrderTidObj.remove("tim");
      ObjectNode requestObj = inOrderTidObj; // with 'tim' element removed,
                                             // encodableTid becomes the
                                             // 'request' element

      // Create valid payload from scratch
      OdeMsgPayload payload = null;

      ObjectNode dataBodyObj = JsonUtils.newNode();

      // Build a MessageFrame
      ObjectNode mfBodyObj = (ObjectNode) JsonUtils.newNode();
      mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
      mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set("TravelerInformation", timObj));
      dataBodyObj = (ObjectNode) JsonUtils.newNode().set("MessageFrame", mfBodyObj);
      payload = new OdeTimPayload();
      payload.setDataType("MessageFrame");

      ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
      payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

      // Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());
      metaObject.set("request", requestObj);

      // Workaround for XML Array issue. Set a placeholder for the encodings to
      // be added later as a string replacement
      metaObject.set("encodings_palceholder", null);

      ObjectNode message = JsonUtils.newNode();
      message.set(AppContext.METADATA_STRING, metaObject);
      message.set(AppContext.PAYLOAD_STRING, payloadObj);

      ObjectNode root = JsonUtils.newNode();
      root.set("OdeAsn1Data", message);

      // Convert to XML
      logger.debug("pre-xml: {}", root);
      String outputXml = XmlUtils.toXmlS(root);
      String encStr = buildEncodings();
      outputXml = outputXml.replace("<encodings_palceholder/>", encStr);

      // Fix tagnames by String replacements
      String fixedXml = outputXml.replaceAll("tcontent>", "content>");// workaround
                                                                      // for the
                                                                      // "content"
                                                                      // reserved
                                                                      // name
      fixedXml = fixedXml.replaceAll("llong>", "long>"); // workaround for
                                                         // "long" being a type
                                                         // in java
      fixedXml = fixedXml.replaceAll("node_LL3>", "node-LL3>");
      fixedXml = fixedXml.replaceAll("node_LatLon>", "node-LatLon>");
      fixedXml = fixedXml.replaceAll("nodeLL>", "NodeLL>");
      fixedXml = fixedXml.replaceAll("nodeXY>", "NodeXY>");
      fixedXml = fixedXml.replaceAll("sequence>", "SEQUENCE>");
      fixedXml = fixedXml.replaceAll("geographicalPath>", "GeographicalPath>");

      // workarounds for self-closing tags
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.EMPTY_FIELD_FLAG, "");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_TRUE, "<true />");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_FALSE, "<false />");

      // remove the surrounding <ObjectNode></ObjectNode>
      fixedXml = fixedXml.replace("<ObjectNode>", "");
      fixedXml = fixedXml.replace("</ObjectNode>", "");

      logger.debug("Fixed XML: {}", fixedXml);
      return fixedXml;
   }

   private String buildEncodings() throws JsonUtilsException, XmlUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(addEncoding("MessageFrame", "MessageFrame", EncodingRule.UPER));
      ObjectNode encodingWrap = (ObjectNode) JsonUtils.newNode().set("wrap", encodings);
      String encStr = XmlUtils.toXmlS(encodingWrap).replace("</wrap><wrap>", "").replace("<wrap>", "")
            .replace("</wrap>", "").replace("<ObjectNode>", "<encodings>").replace("</ObjectNode>", "</encodings>");
      return encStr;
   }

   private JsonNode addEncoding(String name, String type, EncodingRule rule) throws JsonUtilsException {
      Asn1Encoding mfEnc = new Asn1Encoding(name, type, rule);
      return JsonUtils.newNode().set("encodings", JsonUtils.toObjectNode(mfEnc.toJson()));
   }

}
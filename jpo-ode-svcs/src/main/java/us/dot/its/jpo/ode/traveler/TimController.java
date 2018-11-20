package us.dot.its.jpo.ode.traveler;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.json.JSONObject;
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
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeRequestMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Content;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputData;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimSerializer;

@Controller
public class TimController {

   public static final String REQUEST = "request";

   public static class TimControllerException extends Exception {

      private static final long serialVersionUID = 1L;

      public TimControllerException(String errMsg) {
        super(errMsg);
      }

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
   private SerialId serialIdJ2735;
   private SerialId serialIdOde;


   @Autowired
   public TimController(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;

      this.stringMsgProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
            odeProperties.getKafkaProducerType());
      this.timProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, OdeTimSerializer.class.getName());
      this.serialIdJ2735 = new SerialId();
      this.serialIdOde = new SerialId();
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
   public ResponseEntity<String> depositTim(String jsonString, RequestVerb verb) {
      // Check empty
      if (null == jsonString || jsonString.isEmpty()) {
         String errMsg = "Empty request.";
         logger.error(errMsg);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      OdeTravelerInputData odeTID = null;
      ServiceRequest request;
      try {
         // Convert JSON to POJO
        odeTID = (OdeTravelerInputData) JsonUtils.fromJson(jsonString, OdeTravelerInputData.class);
        request = odeTID.getRequest();
        if (request == null) {
          throw new TimControllerException("request element is required as of version 3"); 
        }
        if (request.getOde() != null) {
          if (request.getOde().getVersion() != ServiceRequest.OdeInternal.LATEST_VERSION) {
            throw new TimControllerException("Invalid REST API Schema Version Specified: " 
              + request.getOde().getVersion() + ". Supported Schema Version is " 
                + ServiceRequest.OdeInternal.LATEST_VERSION) ; 
           }
        } else {
          request.setOde(new OdeInternal());
        }

        request.getOde().setVerb(verb);

        logger.debug("OdeTravelerInputData: {}", jsonString);

      } catch (TimControllerException e) {
         String errMsg = "Missing or invalid argument: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      } catch (Exception e) {
        String errMsg = "Malformed or non-compliant JSON.";
        logger.error(errMsg, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
     }

      // Add metadata to message and publish to kafka
      OdeTravelerInformationMessage tim = odeTID.getTim();
      OdeMsgPayload timDataPayload = new OdeMsgPayload(tim);
      OdeRequestMsgMetadata timMetadata = new OdeRequestMsgMetadata(timDataPayload, request);
      
      //Setting the SerialId to OdeBradcastTim serialId to be changed to J2735BroadcastTim serialId after the message has been published to OdeTimBrodcast topic
      timMetadata.setSerialId(serialIdOde);
      timMetadata.setRecordGeneratedBy(GeneratedBy.TMC);
      
      try {
        timMetadata.setRecordGeneratedAt(DateTimeUtils.isoDateTime(
            DateTimeUtils.isoDateTime(tim.getTimeStamp())));
      } catch (ParseException e) {
        String errMsg = "Invalid timestamp in tim record: " + tim.getTimeStamp();
        logger.error(errMsg, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }
      
      OdeTimData odeTimData = new OdeTimData(timMetadata, timDataPayload);
      timProducer.send(odeProperties.getKafkaTopicOdeTimBroadcastPojo(), null, odeTimData);

      String obfuscatedTimData = obfuscateRsuPassword(odeTimData.toJson());
      stringMsgProducer.send(odeProperties.getKafkaTopicOdeTimBroadcastJson(), null, obfuscatedTimData);
      
      //Now that the message gas been published to OdeBradcastTim topic, it should be changed to J2735BroadcastTim serialId
      timMetadata.setSerialId(serialIdJ2735);

      // Short circuit
      // If the TIM has no RSU/SNMP or SDW structures, we are done
      if (request != null && (request.getRsus() == null || request.getSnmp() == null)
            && request.getSdw() == null) {
         String warningMsg = "Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to POJO broadcast stream.";
         logger.warn(warningMsg);
         return ResponseEntity.status(HttpStatus.OK).body(jsonKeyValue(WARNING, warningMsg));
      }

      // Craft ASN-encodable TIM
      ObjectNode encodableTid;
      try {
         encodableTid = JsonUtils.toObjectNode(odeTID.toJson());
         TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(encodableTid);

         logger.debug("Encodable Traveler Information Data: {}", encodableTid);

      } catch (JsonUtilsException e) {
         String errMsg = "Error converting to encodable TIM.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      try {
         logger.debug("securitySvcsSignatureUri = {}", odeProperties.getSecuritySvcsSignatureUri());
         String xmlMsg;
         DdsAdvisorySituationData asd = null;
         if (!odeProperties.dataSigningEnabled()) {
            // We need to send data UNSECURED, so we should try to build the ASD as well as MessageFrame
            asd = buildASD(odeTID.getRequest());
         }
         xmlMsg = convertToXml(asd, encodableTid, timMetadata);
         // publish Broadcast TIM to a J2735 compliant topic.
         JSONObject jsonMsg = XmlUtils.toJSONObject(xmlMsg);
         
         String j2735Tim = OdeTimDataCreatorHelper.createOdeTimData(jsonMsg.getJSONObject(AppContext.ODE_ASN1_DATA)).toString();

         stringMsgProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, xmlMsg);

         //Publish JSON version
         String obfuscatedj2735Tim = obfuscateRsuPassword(j2735Tim);
         stringMsgProducer.send(odeProperties.getKafkaTopicJ2735TimBroadcastJson(), null, obfuscatedj2735Tim);
         // publish J2735 TIM also to general un-filtered TIM topic
         stringMsgProducer.send(odeProperties.getKafkaTopicOdeTimJson(), null, obfuscatedj2735Tim);
         
         serialIdOde.increment();
         serialIdJ2735.increment();
      } catch (JsonUtilsException | XmlUtilsException | ParseException e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      } catch (Exception e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      return ResponseEntity.status(HttpStatus.OK).body(jsonKeyValue("Success", "true"));
   }

  public static String obfuscateRsuPassword(String message) {
    return message.replaceAll("\"rsuPassword\": *\".*?\"", "\"rsuPassword\":\"*\"");
  }

//  public static void obfuscateRsuPassword(JSONObject jsonMsg) {
//    try {
//       JSONObject req = jsonMsg.getJSONObject(AppContext.METADATA_STRING).getJSONObject(REQUEST);
//       JSONArray rsus = req.optJSONArray("rsus");
//       if (rsus != null) {
//         for (int index = 0; index < rsus.length(); index++) {
//           JSONObject rsu = rsus.getJSONObject(index);
//           if (rsu.has("rsuPassword")) {
//             rsu.put("rsuPassword", "*");
//           }
//         }
//       }
//     } catch (JSONException e) {
//       logger.info("No RSUs found in the metadata/request/rsus");
//     }
//  }

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

      return depositTim(jsonString, ServiceRequest.OdeInternal.RequestVerb.PUT);
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

      return depositTim(jsonString, ServiceRequest.OdeInternal.RequestVerb.POST);
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

   private JsonNode addEncoding(String name, String type, EncodingRule rule) throws JsonUtilsException {
      Asn1Encoding mfEnc = new Asn1Encoding(name, type, rule);
      return JsonUtils.newNode().set("encodings", JsonUtils.toObjectNode(mfEnc.toJson()));
   }

   private DdsAdvisorySituationData buildASD(ServiceRequest travelerInputData) {
      Ieee1609Dot2DataTag ieeeDataTag = new Ieee1609Dot2DataTag();
      Ieee1609Dot2Data ieee = new Ieee1609Dot2Data();
      Ieee1609Dot2Content ieeeContent = new Ieee1609Dot2Content();
      J2735MessageFrame j2735Mf = new J2735MessageFrame();
      MessageFrame mf = new MessageFrame();
      mf.setMessageFrame(j2735Mf);
      ieeeContent.setUnsecuredData(mf);
      ieee.setContent(ieeeContent);
      ieeeDataTag.setIeee1609Dot2Data(ieee);

      byte sendToRsu = travelerInputData.getRsus() != null ? DdsAdvisorySituationData.RSU
            : DdsAdvisorySituationData.NONE;
      byte distroType = (byte) (DdsAdvisorySituationData.IP | sendToRsu);

      // take deliverystart and stop times from SNMP object, if present
      // else take from SDW object
      SNMP snmp = travelerInputData.getSnmp();

      SDW sdw = travelerInputData.getSdw();
      DdsAdvisorySituationData asd = null;
      if (null != sdw) {
         try {
            if (null != snmp) {
   
               asd = new DdsAdvisorySituationData(snmp.getDeliverystart(), snmp.getDeliverystop(), ieeeDataTag,
                     GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()), sdw.getTtl(), sdw.getGroupID(),
                     sdw.getRecordId(), distroType);
               } else {
               asd = new DdsAdvisorySituationData(sdw.getDeliverystart(), sdw.getDeliverystop(), ieeeDataTag,
                     GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()), sdw.getTtl(), sdw.getGroupID(),
                     sdw.getRecordId(), distroType);
            }
   
         } catch (ParseException e) {
            String errMsg = "Error AdvisorySituationDatae: " + e.getMessage();
            logger.error(errMsg, e);
         }
      }
      return asd;
   }

   private String convertToXml(DdsAdvisorySituationData asd, ObjectNode encodableTidObj, OdeMsgMetadata timMetadata)
         throws JsonUtilsException, XmlUtilsException, ParseException {

      TravelerInputData inOrderTid = (TravelerInputData) JsonUtils.jacksonFromJson(encodableTidObj.toString(), TravelerInputData.class);
      logger.debug("In Order TravelerInputData: {}", inOrderTid);
      ObjectNode inOrderTidObj = JsonUtils.toObjectNode(inOrderTid.toJson());

      JsonNode timObj = inOrderTidObj.get("tim");
      JsonNode requestObj = inOrderTidObj.get(REQUEST);

      //Create valid payload from scratch
      OdeMsgPayload payload = null;

      ObjectNode dataBodyObj = JsonUtils.newNode();
      if (null != asd) {
         logger.debug("Converting request to ASD/Ieee1609Dot2Data/MessageFrame!");
         ObjectNode asdObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode mfBodyObj = (ObjectNode) asdObj.findValue("MessageFrame");
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set(
            "TravelerInformation", timObj));

         dataBodyObj.set("AdvisorySituationData", asdObj);

         payload = new OdeAsdPayload(asd);
      } else {
         logger.debug("Converting request to Ieee1609Dot2Data/MessageFrame!");
         //Build a MessageFrame
         ObjectNode mfBodyObj = (ObjectNode) JsonUtils.newNode();
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set("TravelerInformation", timObj));
         dataBodyObj = (ObjectNode) JsonUtils.newNode().set("MessageFrame", mfBodyObj);
         payload = new OdeTimPayload();
         payload.setDataType("MessageFrame");
      }

      ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
      payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

      // Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      metadata.setSerialId(serialIdJ2735);
      metadata.setRecordGeneratedBy(timMetadata.getRecordGeneratedBy());
      metadata.setRecordGeneratedAt(timMetadata.getRecordGeneratedAt());
      ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());
      metaObject.set(REQUEST, requestObj);
      
      //Workaround for XML Array issue. Set a placeholder for the encodings to be added later as a string replacement
      metaObject.set("encodings_palceholder", null);

      ObjectNode message = JsonUtils.newNode();
      message.set(AppContext.METADATA_STRING, metaObject);
      message.set(AppContext.PAYLOAD_STRING, payloadObj);

      ObjectNode root = JsonUtils.newNode();
      root.set(AppContext.ODE_ASN1_DATA, message);

      // Convert to XML
      logger.debug("pre-xml: {}", root);
      String outputXml = XmlUtils.toXmlS(root);
      String encStr = buildEncodings(asd);
      outputXml = outputXml.replace("<encodings_palceholder/>", encStr);
      
      // Fix  tagnames by String replacements
      String fixedXml = outputXml.replaceAll("tcontent>", "content>");// workaround
                                                                      // for the
                                                                      // "content"
                                                                      // reserved
                                                                      // name
      fixedXml = fixedXml.replaceAll("llong>", "long>"); // workaround for
                                                         // "long" being a type
                                                         // in java
      fixedXml = fixedXml.replaceAll("node_LL", "node-LL");
      fixedXml = fixedXml.replaceAll("node_XY", "node-XY");
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

   private String buildEncodings(DdsAdvisorySituationData asd) throws JsonUtilsException, XmlUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(addEncoding("MessageFrame", "MessageFrame", EncodingRule.UPER));
      if (null != asd) {
         encodings.add(addEncoding("Ieee1609Dot2Data", "Ieee1609Dot2Data", EncodingRule.COER));
         encodings.add(addEncoding("AdvisorySituationData", "AdvisorySituationData", EncodingRule.UPER));
      }
      ObjectNode encodingWrap = (ObjectNode) JsonUtils.newNode().set("wrap", encodings);
      String encStr = XmlUtils.toXmlS(encodingWrap)
            .replace("</wrap><wrap>", "")
            .replace("<wrap>", "")
            .replace("</wrap>", "")
            .replace("<ObjectNode>", "<encodings>")
            .replace("</ObjectNode>", "</encodings>");
      return encStr;
   }
}
package us.dot.its.jpo.ode.traveler;

import java.text.ParseException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeRequestMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Content;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MessageFrame;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimSerializer;

@Controller
public class TimDepositController {


   public static class TimDepositControllerException extends Exception {

      private static final long serialVersionUID = 1L;

      public TimDepositControllerException(String errMsg) {
         super(errMsg);
      }

      public TimDepositControllerException(String errMsg, Exception e) {
         super(errMsg, e);
      }

   }

   private static final Logger logger = LoggerFactory.getLogger(TimDepositController.class);

   private static final String ERRSTR = "error";
   private static final String WARNING = "warning";
   private static final String SUCCESS = "success";

   private OdeProperties odeProperties;
   private TimMessageManipulator timMessageManipulator;
   
   private MessageProducer<String, String> stringMsgProducer;
   private MessageProducer<String, OdeObject> timProducer;
   private SerialId serialIdJ2735;
   private SerialId serialIdOde;



   @Autowired
   public TimDepositController(OdeProperties odeProperties, TimMessageManipulator timMessageManipulator) {
      super();
      this.odeProperties = odeProperties;
      this.timMessageManipulator = timMessageManipulator;

      this.stringMsgProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
            odeProperties.getKafkaProducerType(), odeProperties.getKafkaTopicsDisabledSet());
      this.timProducer = new MessageProducer<>(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(),
            null, OdeTimSerializer.class.getName(), odeProperties.getKafkaTopicsDisabledSet());
      this.serialIdJ2735 = new SerialId();
      this.serialIdOde = new SerialId();
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
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      }

      OdeTravelerInputData odeTID = null;
      ServiceRequest request;
      try {
         // Convert JSON to POJO
         odeTID = (OdeTravelerInputData) JsonUtils.fromJson(jsonString, OdeTravelerInputData.class);
         request = odeTID.getRequest();
         if (request == null) {
            throw new TimDepositControllerException("request element is required as of version 3");
         }
         if (request.getOde() != null) {
            if (request.getOde().getVersion() != ServiceRequest.OdeInternal.LATEST_VERSION) {
               throw new TimDepositControllerException(
                     "Invalid REST API Schema Version Specified: " + request.getOde().getVersion()
                           + ". Supported Schema Version is " + ServiceRequest.OdeInternal.LATEST_VERSION);
            }
         } else {
            request.setOde(new OdeInternal());
         }

         request.getOde().setVerb(verb);

         logger.debug("OdeTravelerInputData: {}", jsonString);

      } catch (TimDepositControllerException e) {
         String errMsg = "Missing or invalid argument: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      } catch (Exception e) {
         String errMsg = "Malformed or non-compliant JSON.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      }

      // Add metadata to message and publish to kafka
      OdeTravelerInformationMessage tim = odeTID.getTim();
      OdeMsgPayload timDataPayload = new OdeMsgPayload(tim);
      OdeRequestMsgMetadata timMetadata = new OdeRequestMsgMetadata(timDataPayload, request);

      // Setting the SerialId to OdeBradcastTim serialId to be changed to
      // J2735BroadcastTim serialId after the message has been published to
      // OdeTimBrodcast topic
      timMetadata.setSerialId(serialIdOde);
      timMetadata.setRecordGeneratedBy(GeneratedBy.TMC);

      try {
         timMetadata.setRecordGeneratedAt(DateTimeUtils.isoDateTime(DateTimeUtils.isoDateTime(tim.getTimeStamp())));
      } catch (ParseException e) {
         String errMsg = "Invalid timestamp in tim record: " + tim.getTimeStamp();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      }

      OdeTimData odeTimData = new OdeTimData(timMetadata, timDataPayload);
      timProducer.send(odeProperties.getKafkaTopicOdeTimBroadcastPojo(), null, odeTimData);

      String obfuscatedTimData = obfuscateRsuPassword(odeTimData.toJson());
      stringMsgProducer.send(odeProperties.getKafkaTopicOdeTimBroadcastJson(), null, obfuscatedTimData);

      // Now that the message gas been published to OdeBradcastTim topic, it should be
      // changed to J2735BroadcastTim serialId
      timMetadata.setSerialId(serialIdJ2735);

      // Short circuit
      // If the TIM has no RSU/SNMP or SDW structures, we are done
      if (request != null && (request.getRsus() == null || request.getSnmp() == null) && request.getSdw() == null) {
         String warningMsg = "Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to POJO broadcast stream.";
         logger.warn(warningMsg);
         return ResponseEntity.status(HttpStatus.OK).body(JsonUtils.jsonKeyValue(WARNING, warningMsg));
      }

      // Craft ASN-encodable TIM
      ObjectNode encodableTid;
      try {
         encodableTid = JsonUtils.toObjectNode(odeTID.toJson());
         TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(encodableTid);

         logger.debug("Encodable Traveler Information Data: {}", encodableTid);

      } catch (JsonUtilsException e) {
         String errMsg = "Error converting to encodable TravelerInputData.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      }

      try {
         logger.debug("securitySvcsSignatureUri = {}", odeProperties.getSecuritySvcsSignatureUri());
         String xmlMsg;
         DdsAdvisorySituationData asd = null;
         if (!odeProperties.dataSigningEnabled()) {
            // We need to send data UNSECURED, so we should try to build the ASD as well as
            // MessageFrame
            asd = buildASD(odeTID.getRequest());
         }
         xmlMsg = timMessageManipulator.convertToXml(asd, encodableTid, timMetadata, serialIdJ2735);
         JSONObject jsonMsg = XmlUtils.toJSONObject(xmlMsg);

         String j2735Tim = OdeTimDataCreatorHelper.createOdeTimData(jsonMsg.getJSONObject(AppContext.ODE_ASN1_DATA))
               .toString();

         stringMsgProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, xmlMsg);

         String obfuscatedj2735Tim = obfuscateRsuPassword(j2735Tim);
         // publish Broadcast TIM to a J2735 compliant topic.
         stringMsgProducer.send(odeProperties.getKafkaTopicJ2735TimBroadcastJson(), null, obfuscatedj2735Tim);
         // publish J2735 TIM also to general un-filtered TIM topic
         stringMsgProducer.send(odeProperties.getKafkaTopicOdeTimJson(), null, obfuscatedj2735Tim);

         serialIdOde.increment();
         serialIdJ2735.increment();
      } catch (JsonUtilsException | XmlUtilsException | ParseException e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      } catch (Exception e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
      }

      return ResponseEntity.status(HttpStatus.OK).body(JsonUtils.jsonKeyValue(SUCCESS, "true"));
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
    * @param jsonString TIM in JSON
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
    * @param jsonString TIM in JSON
    * @return list of success/failures
    */
   @ResponseBody
   @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
   @CrossOrigin
   public ResponseEntity<String> postTim(@RequestBody String jsonString) {

      return depositTim(jsonString, ServiceRequest.OdeInternal.RequestVerb.POST);
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

         } catch (Exception e) {
            String errMsg = "Error AdvisorySituationDatae: " + e.getMessage();
            logger.error(errMsg, e);
         }
      }
      return asd;
   }



}

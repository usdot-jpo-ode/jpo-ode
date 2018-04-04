package us.dot.its.jpo.ode.services.asn1;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.traveler.TimController.TimControllerException;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1EncodedDataRouter extends AbstractSubscriberProcessor<String, String> {

   public static class Asn1EncodedDataRouterException extends Exception {

      private static final long serialVersionUID = 1L;

      public Asn1EncodedDataRouterException(String string) {
         super(string);
      }

   }

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private OdeProperties odeProperties;
   private MessageProducer<String, String> stringMsgProducer;
   private Asn1CommandManager asn1CommandManager;

   public Asn1EncodedDataRouter(OdeProperties odeProperties) {
      super();
      
      this.odeProperties = odeProperties;

      this.stringMsgProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
            odeProperties.getKafkaProducerType());

      this.asn1CommandManager = new Asn1CommandManager(odeProperties);

   }

   @Override
   public Object process(String consumedData) {
      try {
         logger.debug("Consumed: {}", consumedData);
         JSONObject consumedObj = XmlUtils.toJSONObject(consumedData).getJSONObject(OdeAsn1Data.class.getSimpleName());

         /*
          * When receiving the 'rsus' in xml, since there is only one 'rsu' and
          * there is no construct for array in xml, the rsus does not translate
          * to an array of 1 element. The following workaround, resolves this
          * issue.
          */
         JSONObject metadata = consumedObj.getJSONObject(AppContext.METADATA_STRING);

         if (metadata.has("request")) {
            JSONObject request = metadata.getJSONObject("request");

            if (request.has("rsus")) {
               Object rsu = request.get("rsus");
               if (!(rsu instanceof JSONArray)) {
                  JSONArray rsus = new JSONArray();
                  rsus.put(rsu);
                  request.put("rsus", rsus);
               }
            }

            // Convert JSON to POJO
            OdeTravelerInputData travelerinputData = buildTravelerInputData(consumedObj);

            processEncodedTim(travelerinputData, consumedObj);

         } else {
            throw new Asn1EncodedDataRouterException("Encoder response missing 'request'");
         }
      } catch (Exception e) {
         String msg = "Error in processing received message from ASN.1 Encoder module: " + consumedData;
         EventLogger.logger.error(msg, e);
         logger.error(msg, e);
      }
      return null;
   }

   public OdeTravelerInputData buildTravelerInputData(JSONObject consumedObj) {
      String request = consumedObj.getJSONObject(AppContext.METADATA_STRING).getJSONObject("request").toString();

      // Convert JSON to POJO
      OdeTravelerInputData travelerinputData = null;
      try {
         logger.debug("JSON: {}", request);
         travelerinputData = (OdeTravelerInputData) JsonUtils.fromJson(request, OdeTravelerInputData.class);

      } catch (Exception e) {
         String errMsg = "Malformed JSON.";
         EventLogger.logger.error(errMsg, e);
         logger.error(errMsg, e);
      }

      return travelerinputData;
   }

   public void processEncodedTim(OdeTravelerInputData travelerInfo, JSONObject consumedObj)
         throws TimControllerException {

      JSONObject dataObj = consumedObj.getJSONObject(AppContext.PAYLOAD_STRING).getJSONObject(AppContext.DATA_STRING);
      
      // CASE 1: no SDW in metadata (SNMP deposit only)
      //  - sign MF
      //  - send to RSU
      // CASE 2: SDW in metadata but no ASD in body (send back for another encoding)
      //  - sign MF
      //  - send to RSU
      //  - craft ASD object
      //  - publish back to encoder stream
      // CASE 3: If SDW in metadata and ASD in body (double encoding complete)
      //  - send to DDS
      
      if (!dataObj.has("AdvisorySituationData")) {
         // Cases 1 & 2
         // Sign and send to RSUs
         
         JSONObject mfObj = dataObj.getJSONObject("MessageFrame");

         String encodedTim = mfObj.getString("bytes");
         logger.debug("Encoded message: {}", encodedTim);

         logger.debug("Sending message for signature!");
         String signedResponse = asn1CommandManager.sendForSignature(encodedTim);
         logger.debug("Message signed!");

         String signedTim = null;
         try {
            signedTim = JsonUtils.toJSONObject(signedResponse).getString("result");
         } catch (JsonUtilsException e1) {
            logger.error("Unable to parse signed message response {}", e1);
         }

         asn1CommandManager.sendToRsus(travelerInfo, signedTim);
         
         if (travelerInfo.getSdw() != null) {
            // Case 2 only
            
            logger.debug("Publishing message for round 2 encoding!");
            String xmlizedMessage = putSignedTimIntoAsdObject(travelerInfo, signedTim);

            stringMsgProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, xmlizedMessage);
         }
         
      } else {
         // Case 3
         JSONObject asdObj = dataObj.getJSONObject("AdvisorySituationData");
         asn1CommandManager.depositToDDS(asdObj.getString("bytes"));
      }

   }

   public String putSignedTimIntoAsdObject(OdeTravelerInputData travelerInputData, String signedMsg) {

      SDW sdw = travelerInputData.getSdw();
      SNMP snmp = travelerInputData.getSnmp();
      DdsAdvisorySituationData asd = null;

      byte sendToRsu = travelerInputData.getRsus() != null ? DdsAdvisorySituationData.RSU
            : DdsAdvisorySituationData.NONE;
      byte distroType = (byte) (DdsAdvisorySituationData.IP | sendToRsu);
      //
      String outputXml = null;
      try {
         if (null != snmp) {

            asd = new DdsAdvisorySituationData(snmp.getDeliverystart(), snmp.getDeliverystop(), null,
                  GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()), sdw.getTtl(), sdw.getGroupID(),
                  sdw.getRecordId(), distroType);
         } else {
            asd = new DdsAdvisorySituationData(sdw.getDeliverystart(), sdw.getDeliverystop(), null,
                  GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()), sdw.getTtl(), sdw.getGroupID(),
                  sdw.getRecordId(), distroType);
         }

         OdeMsgPayload payload = null;

         ObjectNode dataBodyObj = JsonUtils.newNode();
         ObjectNode asdObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode admDetailsObj = (ObjectNode) asdObj.findValue("asdmDetails");
         admDetailsObj.remove("advisoryMessage");
         admDetailsObj.put("advisoryMessage", signedMsg);

         dataBodyObj.set("AdvisorySituationData", asdObj);

         payload = new OdeAsdPayload(asd);

         ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
         payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

         OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
         ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());

         ObjectNode requestObj = JsonUtils.toObjectNode(JsonUtils.toJson(travelerInputData, false));

         requestObj.remove("tim");

         metaObject.set("request", requestObj);

         metaObject.set("encodings_placeholder", null);

         ObjectNode message = JsonUtils.newNode();
         message.set(AppContext.METADATA_STRING, metaObject);
         message.set(AppContext.PAYLOAD_STRING, payloadObj);

         ObjectNode root = JsonUtils.newNode();
         root.set("OdeAsn1Data", message);

         outputXml = XmlUtils.toXmlS(root);
         String encStr = buildEncodings(asd);
         outputXml = outputXml.replace("<encodings_placeholder/>", encStr);

         // remove the surrounding <ObjectNode></ObjectNode>
         outputXml = outputXml.replace("<ObjectNode>", "");
         outputXml = outputXml.replace("</ObjectNode>", "");

      } catch (ParseException | JsonUtilsException | XmlUtilsException e) {
         logger.error("Parsing exception thrown while populating ASD structure: {}", e);
      }

      logger.debug("Here is the fully crafted structure, I think this should go to the encoder again: {}", outputXml);

      return outputXml;
   }

   private String buildEncodings(DdsAdvisorySituationData asd) throws JsonUtilsException, XmlUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(addEncoding("AdvisorySituationData", "AdvisorySituationData", EncodingRule.UPER));
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

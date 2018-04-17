package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimController;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class Asn1CommandManager {

   private static final Logger logger = LoggerFactory.getLogger(Asn1CommandManager.class);

   public static class Asn1CommandManagerException extends Exception {

      private static final long serialVersionUID = 1L;

      public Asn1CommandManagerException(String string) {
         super(string);
      }

   }

   private String signatureUri;
   private DdsDepositor<DdsStatusMessage> depositor;

   public Asn1CommandManager(OdeProperties odeProperties) {

      this.signatureUri = odeProperties.getSecuritySvcsSignatureUri();

      try {
         depositor = new DdsDepositor<>(odeProperties);
      } catch (Exception e) {
         String msg = "Error starting SDW depositor";
         EventLogger.logger.error(msg, e);
         logger.error(msg, e);
      }

   }

   public void depositToDDS(String asdBytes) {
      try {
         depositor.deposit(asdBytes);
         EventLogger.logger.info("Message Deposited to SDW: {}", asdBytes);
         logger.info("Message deposited to SDW: {}", asdBytes);
      } catch (DdsRequestManagerException e) {
         EventLogger.logger.error("Failed to deposit message to SDW: {}", e);
         logger.error("Failed to deposit message to SDW: {}", e);
      }
   }

   public HashMap<String, String> sendToRsus(OdeTravelerInputData travelerInfo, String encodedMsg) {

      HashMap<String, String> responseList = new HashMap<>();
      for (RSU curRsu : travelerInfo.getRsus()) {

         ResponseEvent rsuResponse = null;

         String httpResponseStatus;
         try {
            rsuResponse = SnmpSession.createAndSend(travelerInfo.getSnmp(), curRsu, travelerInfo.getOde().getIndex(),
                  encodedMsg, travelerInfo.getOde().getVerb());
            if (null == rsuResponse || null == rsuResponse.getResponse()) {
               // Timeout
               httpResponseStatus = "Timeout";
            } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
               // Success
               httpResponseStatus = "Success";
            } else if (rsuResponse.getResponse().getErrorStatus() == 5) {
               // Error, message already exists
               httpResponseStatus = "Message already exists at ".concat(Integer.toString(travelerInfo.getOde().getIndex()));
            } else {
               // Misc error
               httpResponseStatus = "Error code " + rsuResponse.getResponse().getErrorStatus() + " "
                           + rsuResponse.getResponse().getErrorStatusText();
            }
         } catch (IOException | TimPduCreatorException e) {
            String msg = "Exception caught in TIM RSU deposit loop.";
            EventLogger.logger.error(msg, e);
            logger.error(msg, e);
            httpResponseStatus = e.getClass().getName() + ": " + e.getMessage();
         }
         responseList.put(curRsu.getRsuTarget(), httpResponseStatus);

         if (null == rsuResponse || null == rsuResponse.getResponse()) {
            // Timeout
            logger.error("Error on RSU SNMP deposit to {}: timed out.", curRsu.getRsuTarget());
         } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
            // Success
            logger.info("RSU SNMP deposit to {} successful.", curRsu.getRsuTarget());
         } else if (rsuResponse.getResponse().getErrorStatus() == 5) {
            // Error, message already exists
            Integer destIndex = travelerInfo.getOde().getIndex();
            logger.error("Error on RSU SNMP deposit to {}: message already exists at index {}.", curRsu.getRsuTarget(),
                  destIndex);
         } else {
            // Misc error
            logger.error("Error on RSU SNMP deposit to {}: {}", curRsu.getRsuTarget(), "Error code "
                  + rsuResponse.getResponse().getErrorStatus() + " " + rsuResponse.getResponse().getErrorStatusText());
         }

      }
      return responseList;
   }

   public String sendForSignature(String message) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(TimController.jsonKeyValue("message", message), headers);

      RestTemplate template = new RestTemplate();

      logger.info("Security services module request: {}", entity);

      ResponseEntity<String> respEntity = template.postForEntity(signatureUri, entity, String.class);

      logger.info("Security services module response: {}", respEntity);

      return respEntity.getBody();
   }
   
   public String packageSignedTimIntoAsd(OdeTravelerInputData travelerInputData, String signedMsg) {

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
         
         String encStr = buildEncodings();
         outputXml = outputXml.replace("<encodings_placeholder/>", encStr);

         // remove the surrounding <ObjectNode></ObjectNode>
         outputXml = outputXml.replace("<ObjectNode>", "");
         outputXml = outputXml.replace("</ObjectNode>", "");

      } catch (ParseException | JsonUtilsException | XmlUtilsException e) {
         logger.error("Parsing exception thrown while populating ASD structure: {}", e);
      }

      logger.debug("Fully crafted ASD to be encoded: {}", outputXml);

      return outputXml;
   }
   
   private String buildEncodings() throws JsonUtilsException, XmlUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(addEncoding("AdvisorySituationData", "AdvisorySituationData", EncodingRule.UPER));
      ObjectNode encodingWrap = (ObjectNode) JsonUtils.newNode().set("wrap", encodings);
      String encStr = XmlUtils.toXmlS(encodingWrap)
            .replace("</wrap><wrap>", "")
            .replace("<wrap>", "")
            .replace("</wrap>", "")
            .replace("<ObjectNode>", "<encodings>")
            .replace("</ObjectNode>", "</encodings>");
      return encStr;
   }

   private JsonNode addEncoding(String name, String type, EncodingRule rule) throws JsonUtilsException {
      Asn1Encoding mfEnc = new Asn1Encoding(name, type, rule);
      return JsonUtils.newNode().set("encodings", JsonUtils.toObjectNode(mfEnc.toJson()));
   }

}

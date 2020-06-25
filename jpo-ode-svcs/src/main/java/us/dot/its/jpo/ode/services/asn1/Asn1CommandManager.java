/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1CommandManager {

   public static final String ADVISORY_SITUATION_DATA_STRING = "AdvisorySituationData";
   private static final Logger logger = LoggerFactory.getLogger(Asn1CommandManager.class);

   public static class Asn1CommandManagerException extends Exception {

      private static final long serialVersionUID = 1L;

      public Asn1CommandManagerException(String string) {
         super(string);
      }

      public Asn1CommandManagerException(String msg, Exception e) {
         super(msg, e);
      }

   }

   private String signatureUri;
   private OdeProperties odeProperties;

   private MessageProducer<String, String> stringMessageProducer;

   private String depositTopic;
   private DdsDepositor<DdsStatusMessage> depositor;

   public Asn1CommandManager(OdeProperties odeProperties) {

      this.odeProperties = odeProperties;

      this.signatureUri = odeProperties.getSecuritySvcsSignatureUri();

      try {
         this.depositor = new DdsDepositor<>(odeProperties);
         this.stringMessageProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
               odeProperties.getKafkaProducerType(), odeProperties.getKafkaTopicsDisabledSet());
         this.setDepositTopic(odeProperties.getKafkaTopicSdwDepositorInput());
      } catch (Exception e) {
         String msg = "Error starting SDW depositor";
         EventLogger.logger.error(msg, e);
         logger.error(msg, e);
      }

   }

   public void depositToSdw(String asdBytes) throws Asn1CommandManagerException {

      if (this.odeProperties.shouldDepositSdwMessagesOverWebsocket()) {
         try {
            depositor.deposit(asdBytes);

            String websocketDepositMessage = "Deposited message to SDW directly via websocket {}";
            logger.info(websocketDepositMessage, asdBytes);
            EventLogger.logger.info(websocketDepositMessage, asdBytes);
         } catch (DdsRequestManagerException e) {
            String msg = "Failed to deposit message to SDW";
            throw new Asn1CommandManagerException(msg, e);
         }
      } else {
         stringMessageProducer.send(this.getDepositTopic(), null, asdBytes);

         String sdwKafkaDepositMessage = "Published message to SDW deposit Kafka topic {}";
         logger.info(sdwKafkaDepositMessage, asdBytes);
         EventLogger.logger.info(sdwKafkaDepositMessage, asdBytes);
      }
   }

   public Map<String, String> sendToRsus(ServiceRequest request, String encodedMsg) {

      HashMap<String, String> responseList = new HashMap<>();
      for (RSU curRsu : request.getRsus()) {

         TimTransmogrifier.updateRsuCreds(curRsu, odeProperties);

         ResponseEvent rsuResponse = null;

         String httpResponseStatus;
         try {
            rsuResponse = SnmpSession.createAndSend(request.getSnmp(), curRsu, encodedMsg, request.getOde().getVerb());
            if (null == rsuResponse || null == rsuResponse.getResponse()) {
               // Timeout
               httpResponseStatus = "Timeout";
            } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
               // Success
               httpResponseStatus = "Success";
            } else if (rsuResponse.getResponse().getErrorStatus() == 5) {
               // Error, message already exists
               httpResponseStatus = "Message already exists at ".concat(Integer.toString(curRsu.getRsuIndex()));
            } else {
               // Misc error
               httpResponseStatus = "Error code " + rsuResponse.getResponse().getErrorStatus() + " "
                     + rsuResponse.getResponse().getErrorStatusText();
            }
         } catch (IOException | ParseException e) {
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
            Integer destIndex = curRsu.getRsuIndex();
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

      HttpEntity<String> entity = new HttpEntity<>(JsonUtils.jsonKeyValue("message", message), headers);

      RestTemplate template = new RestTemplate();

      logger.info("Sending data to security services module at {} to be signed: {}", signatureUri, entity);

      ResponseEntity<String> respEntity = template.postForEntity(signatureUri, entity, String.class);

      logger.info("Security services module response: {}", respEntity);

      return respEntity.getBody();
   }
   public String sendForSignature(String message,int sigValidityOverride) {
	      HttpHeaders headers = new HttpHeaders();
	      headers.setContentType(MediaType.APPLICATION_JSON);
	      Map<String,String> map = new HashMap<>();
	      map.put("message",message);
	      map.put("sigValidityOverride",Integer.toString(sigValidityOverride));

	      //HttpEntity<String> entity = new HttpEntity<>(JsonUtils.jsonKeyValue("message", message), headers);
	      HttpEntity<Map<String,String>> entity = new HttpEntity<>(map, headers);
	      RestTemplate template = new RestTemplate();

	      logger.info("Sending data to security services module  with validity override at {} to be signed: {}", signatureUri, entity);

	      ResponseEntity<String> respEntity = template.postForEntity(signatureUri, entity, String.class);

	      logger.info("Security services module response: {}", respEntity);

	      return respEntity.getBody();
	   }

   public String packageSignedTimIntoAsd(ServiceRequest request, String signedMsg) {

      SDW sdw = request.getSdw();
      SNMP snmp = request.getSnmp();
      DdsAdvisorySituationData asd = null;

      byte sendToRsu = request.getRsus() != null ? DdsAdvisorySituationData.RSU : DdsAdvisorySituationData.NONE;
      byte distroType = (byte) (DdsAdvisorySituationData.IP | sendToRsu);
      //
      String outputXml = null;
      try {
         if (null != snmp) {

            asd = new DdsAdvisorySituationData()
                  .setAsdmDetails(snmp.getDeliverystart(), snmp.getDeliverystop(), distroType, null)
                  .setServiceRegion(GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion())).setTimeToLive(sdw.getTtl())
                  .setGroupID(sdw.getGroupID()).setRecordID(sdw.getRecordId());
         } else {
            asd = new DdsAdvisorySituationData()
                  .setAsdmDetails(sdw.getDeliverystart(), sdw.getDeliverystop(), distroType, null)
                  .setServiceRegion(GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion())).setTimeToLive(sdw.getTtl())
                  .setGroupID(sdw.getGroupID()).setRecordID(sdw.getRecordId());
         }

         OdeMsgPayload payload = null;

         ObjectNode dataBodyObj = JsonUtils.newNode();
         ObjectNode asdObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode admDetailsObj = (ObjectNode) asdObj.findValue("asdmDetails");
         admDetailsObj.remove("advisoryMessage");
         admDetailsObj.put("advisoryMessage", signedMsg);

         dataBodyObj.set(ADVISORY_SITUATION_DATA_STRING, asdObj);

         payload = new OdeAsdPayload(asd);

         ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
         payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

         OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
         ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());

         ObjectNode requestObj = JsonUtils.toObjectNode(JsonUtils.toJson(request, false));

         requestObj.remove("tim");

         metaObject.set("request", requestObj);

         ArrayNode encodings = buildEncodings();
         ObjectNode enc = XmlUtils.createEmbeddedJsonArrayForXmlConversion(AppContext.ENCODINGS_STRING, encodings);
         metaObject.set(AppContext.ENCODINGS_STRING, enc);

         ObjectNode message = JsonUtils.newNode();
         message.set(AppContext.METADATA_STRING, metaObject);
         message.set(AppContext.PAYLOAD_STRING, payloadObj);

         ObjectNode root = JsonUtils.newNode();
         root.set(AppContext.ODE_ASN1_DATA, message);

         outputXml = XmlUtils.toXmlStatic(root);

         // remove the surrounding <ObjectNode></ObjectNode>
         outputXml = outputXml.replace("<ObjectNode>", "");
         outputXml = outputXml.replace("</ObjectNode>", "");

      } catch (ParseException | JsonUtilsException | XmlUtilsException e) {
         logger.error("Parsing exception thrown while populating ASD structure: {}", e);
      }

      logger.debug("Fully crafted ASD to be encoded: {}", outputXml);

      return outputXml;
   }

   public static ArrayNode buildEncodings() throws JsonUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(TimTransmogrifier.buildEncodingNode(ADVISORY_SITUATION_DATA_STRING, ADVISORY_SITUATION_DATA_STRING,
            EncodingRule.UPER));
      return encodings;
   }

   public String getDepositTopic() {
      return depositTopic;
   }

   public void setDepositTopic(String depositTopic) {
      this.depositTopic = depositTopic;
   }

}

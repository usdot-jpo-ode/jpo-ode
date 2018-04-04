package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimController;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;

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

   public void sendToRsus(OdeTravelerInputData travelerInfo, String encodedMsg) {

      for (RSU curRsu : travelerInfo.getRsus()) {

         ResponseEvent rsuResponse = null;

         try {
            rsuResponse = SnmpSession.createAndSend(travelerInfo.getSnmp(), curRsu, travelerInfo.getOde().getIndex(),
                  encodedMsg, travelerInfo.getOde().getVerb());
         } catch (IOException | TimPduCreatorException e) {
            String msg = "Exception caught in TIM RSU deposit loop.";
            EventLogger.logger.error(msg, e);
            logger.error(msg, e);
         }

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
   }

   public String sendForSignature(String message) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(TimController.jsonKeyValue("message", message), headers);

      RestTemplate template = new RestTemplate();

      logger.info("Rest request: {}", entity);

      ResponseEntity<String> respEntity = template.postForEntity(signatureUri, entity, String.class);

      logger.info("Security services module response: {}", respEntity);

      return respEntity.getBody();
   }

}

package us.dot.its.jpo.ode.services.asn1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.traveler.TimController.TimControllerException;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
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
      // - sign MF
      // - send to RSU
      // CASE 2: SDW in metadata but no ASD in body (send back for another
      // encoding)
      // - sign MF
      // - send to RSU
      // - craft ASD object
      // - publish back to encoder stream
      // CASE 3: If SDW in metadata and ASD in body (double encoding complete)
      // - send to DDS

      if (!dataObj.has("AdvisorySituationData")) {
         // Cases 1 & 2
         // Sign and send to RSUs

         JSONObject mfObj = dataObj.getJSONObject("MessageFrame");

         String hexEncodedTim = mfObj.getString("bytes");
         logger.debug("Encoded message: {}", hexEncodedTim);

         if (odeProperties.getSecuritySvcsSignatureUri() != null &&
             !odeProperties.getSecuritySvcsSignatureUri().equalsIgnoreCase("UNSECURED")) {
            logger.debug("Sending message for signature!");
            String base64EncodedTim = CodecUtils.toBase64(
               CodecUtils.fromHex(hexEncodedTim));
            String signedResponse = asn1CommandManager.sendForSignature(base64EncodedTim );
            logger.debug("Message signed!");
   
            try {
               hexEncodedTim = CodecUtils.toHex(
                  CodecUtils.fromBase64(
                     JsonUtils.toJSONObject(signedResponse).getString("result")));
            } catch (JsonUtilsException e1) {
               logger.error("Unable to parse signed message response {}", e1);
            }
         }
         
         logger.debug("Sending message to RSUs...");
         asn1CommandManager.sendToRsus(travelerInfo, hexEncodedTim);

         if (travelerInfo.getSdw() != null) {
            // Case 2 only

            logger.debug("Publishing message for round 2 encoding!");
            String xmlizedMessage = asn1CommandManager.packageSignedTimIntoAsd(travelerInfo, hexEncodedTim);

            stringMsgProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, xmlizedMessage);
         }

      } else {
         // Case 3
         JSONObject asdObj = dataObj.getJSONObject("AdvisorySituationData");
         asn1CommandManager.depositToDDS(asdObj.getString("bytes"));
      }

   }

}

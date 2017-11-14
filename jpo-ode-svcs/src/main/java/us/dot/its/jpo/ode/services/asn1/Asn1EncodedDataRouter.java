package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimController.TimControllerException;
import us.dot.its.jpo.ode.traveler.TimPduCreator;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

public class Asn1EncodedDataRouter extends AbstractSubscriberProcessor<String, String> {

   public static class Asn1EncodedDataRouterException extends Exception {

      private static final long serialVersionUID = 1L;

      public Asn1EncodedDataRouterException(String string) {
         super(string);
      }

   }

   private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OdeProperties odeProperties;
    private DdsDepositor<DdsStatusMessage> depositor;

    public Asn1EncodedDataRouter(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;

      try {
         depositor = new DdsDepositor<>(this.odeProperties);
      } catch (Exception e) {
         String msg = "Error starting SDW depositor";
         EventLogger.logger.error(msg, e);
         logger.error(msg, e);
      }

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

            // Convert JSON to POJO
            OdeTravelerInputData travelerinputData = buildTravelerInputData(consumedObj);

            processEncodedTim(travelerinputData, consumedObj);

            if (request.has("rsus")) {
               Object rsu = request.get("rsus");
               if (!(rsu instanceof JSONArray)) {
                  JSONArray rsus = new JSONArray();
                  rsus.put(rsu);
                  request.put("rsus", rsus);
               }
            }
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
       String request = consumedObj
             .getJSONObject(AppContext.METADATA_STRING)
             .getJSONObject("request").toString();
       
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

    public void processEncodedTim(OdeTravelerInputData travelerInfo, JSONObject consumedObj) throws TimControllerException {
       // Send TIMs and record results
       //HashMap<String, String> responseList = new HashMap<>();

       JSONObject dataObj = consumedObj
             .getJSONObject(AppContext.PAYLOAD_STRING)
             .getJSONObject(AppContext.DATA_STRING);
       
       if (null != travelerInfo.getSdw()) {
         JSONObject mfTimObj = dataObj.getJSONObject("MessageFrame");
         if (null != mfTimObj) {
            String mfTimBytes = mfTimObj.getString("bytes");

            // Deposit to DDS
            try {
               depositToDDS(travelerInfo, mfTimBytes);
               logger.info("DDS deposit successful.");
            } catch (Exception e) {
               logger.error("Error on DDS deposit.", e);
            }

         } else {
            String msg = "ASN.1 Encoder did not return ASD encoding {}";
            EventLogger.logger.error(msg, consumedObj.toString());
            logger.error(msg, consumedObj.toString());
         }
       }
       
       // Deposit to RSUs
       JSONObject mfObj = dataObj.getJSONObject("MessageFrame");
       
      // only send message to rsu if snmp, rsus, and message frame fields are present
      if (null != travelerInfo.getSnmp() && null != travelerInfo.getRsus() && null != mfObj) {
         String timBytes = mfObj.getString("bytes");
         for (RSU curRsu : travelerInfo.getRsus()) {
             
             logger.info("Depositing TIM via SNMP to RSU {}", curRsu.getRsuTarget());

            ResponseEvent rsuResponse = null;

             try {
                rsuResponse = createAndSend(travelerInfo.getSnmp(), curRsu, 
                   travelerInfo.getOde().getIndex(), timBytes);

                if (null == rsuResponse || null == rsuResponse.getResponse()) {
                   logger.error("RSU SNMP deposit to {} failed, timeout.", curRsu.getRsuTarget());
                } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
                   logger.info("RSU SNMP deposit to {} successful.", curRsu.getRsuTarget());
                } else if (rsuResponse.getResponse().getErrorStatus() == 5) {
                   logger.error("RSU SNMP deposit to {} failed, message already exists at index {}.", curRsu.getRsuTarget(), travelerInfo.getOde().getIndex());
                } else {
                   logger.error("RSU SNMP deposit to {} failed, error code {}, error: {}", curRsu.getRsuTarget(), rsuResponse.getResponse().getErrorStatus(), rsuResponse.getResponse().getErrorStatusText());
                }

             } catch (Exception e) {
                String msg = "Exception caught in TIM RSU SNMP deposit loop.";
                EventLogger.logger.error(msg, e);
                logger.error(msg, e);
             }
          }
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
       EventLogger.logger.info("Message Sent to {}: {}", rsu.getRsuTarget(), payload);
       return response;
    }

    private void depositToDDS(OdeTravelerInputData travelerinputData, String asdBytes)
          throws ParseException, DdsRequestManagerException, DdsClientException, WebSocketException {
       // Step 4 - Step Deposit TIM to SDW if sdw element exists
       if (travelerinputData.getSdw() != null) {
          depositor.deposit(asdBytes);
          EventLogger.logger.info("Message Deposited to SDW: {}", asdBytes);
       }
    }

//    /**
//    * Temporary method using OSS to build a ASD with IEEE 1609.2 encapsulating MF/TIM
//    * @param travelerInputData
//    * @param mfTimBytes
//    * @throws ParseException
//    * @throws EncodeFailedException
//    * @throws DdsRequestManagerException
//    * @throws DdsClientException
//    * @throws WebSocketException
//    * @throws EncodeNotSupportedException
//    */
//   private void depositToDDSUsingOss(OdeTravelerInputData travelerInputData, String mfTimBytes) 
//         throws ParseException, EncodeFailedException, DdsRequestManagerException, DdsClientException, WebSocketException, EncodeNotSupportedException {
//      // Step 4 - Step Deposit IEEE 1609.2 wrapped TIM to SDW if sdw element exists
//      SDW sdw = travelerInputData.getSdw();
//      if (sdw != null) {
//         Ieee1609Dot2Data ieee1609Data = new Ieee1609Dot2Data();
//         ieee1609Data.setProtocolVersion(new Uint8(3));
//         Ieee1609Dot2Content ieee1609Dot2Content = new Ieee1609Dot2Content();
//         ieee1609Dot2Content.setUnsecuredData(new Opaque(CodecUtils.fromHex(mfTimBytes)));
//         ieee1609Data.setContent(ieee1609Dot2Content);
//         ByteBuffer ieee1609DataBytes = ossCoerCoder.encode(ieee1609Data);
//         
//         // take deliverystart and stop times from SNMP object, if present
//         // else take from SDW object
//         SNMP snmp = travelerInputData.getSnmp();
//         AsdMessage asdMsg = null;
//         if (null != snmp) {
//            asdMsg = new AsdMessage(
//               snmp.getDeliverystart(),
//               snmp.getDeliverystop(), 
//               CodecUtils.toHex(ieee1609DataBytes.array()),
//               sdw.getServiceRegion(), 
//               sdw.getTtl());
//         } else {
//            asdMsg = new AsdMessage(
//               sdw.getDeliverystart(),
//               sdw.getDeliverystop(), 
//               CodecUtils.toHex(ieee1609DataBytes.array()),
//               sdw.getServiceRegion(), 
//               sdw.getTtl());
//         }
//
//         depositor.deposit(asdMsg.encodeHex());
//         EventLogger.logger.info("Message Deposited to SDW: {}", mfTimBytes);
//      }
//      
//   }

}

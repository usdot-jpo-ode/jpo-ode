package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.snmp.SNMP;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimController.TimControllerException;
import us.dot.its.jpo.ode.traveler.TimPduCreator;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

public class Asn1EncodedDataRouter extends AbstractSubscriberProcessor<String, String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OdeProperties odeProperties;
    private DdsDepositor<DdsStatusMessage> depositor;
    
    public Asn1EncodedDataRouter(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;

      try {
         depositor = new DdsDepositor<>(this.odeProperties);
      } catch (Exception e) {
         logger.error("Error starting SDW depositor", e);
      }

    }

    @Override
    public Object process(String consumedData) {
        try {
           JSONObject consumedObj = XmlUtils.toJSONObject(consumedData)
                 .getJSONObject(OdeAsn1Data.class.getSimpleName());

           // Convert JSON to POJO
           TravelerInputData travelerinputData = buildTravelerInputData(consumedObj);
           
           String hexBytes = consumedObj
                 .getJSONObject(AppContext.PAYLOAD_STRING)
                 .getJSONObject(AppContext.DATA_STRING)
                 .getString("bytes");
           
           processEncodedTim(travelerinputData, hexBytes);
           
        } catch (Exception e) {
           logger.error("Error in processing received message from ASN.1 Encoder module: " + consumedData, e);
        }
        return null;
    }

    public TravelerInputData buildTravelerInputData(JSONObject consumedObj) {
       String request = consumedObj
             .getJSONObject(AppContext.METADATA_STRING)
             .getJSONObject("request").toString();
       
       // Convert JSON to POJO
       TravelerInputData travelerinputData = null;
       try {
          logger.debug("JSON: {}", request);
          travelerinputData = (TravelerInputData) JsonUtils.fromJson(request, TravelerInputData.class);

       } catch (Exception e) {
          String errMsg = "Malformed JSON.";
          logger.error(errMsg, e);
       }

       return travelerinputData;
    }

    public void processEncodedTim(TravelerInputData travelerInfo, String rsuSRMPayload) throws TimControllerException {
       // Send TIMs and record results
       HashMap<String, String> responseList = new HashMap<>();

       for (RSU curRsu : travelerInfo.getRsus()) {

          ResponseEvent rsuResponse = null;
          String httpResponseStatus = null;

          try {
             rsuResponse = createAndSend(travelerInfo.getSnmp(), curRsu, 
                travelerInfo.getOde().getIndex(), rsuSRMPayload);

             if (null == rsuResponse || null == rsuResponse.getResponse()) {
                // Timeout
                httpResponseStatus = "Timeout";
             } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
                // Success
                httpResponseStatus = "Success";
             } else if (rsuResponse.getResponse().getErrorStatus() == 5) {
                // Error, message already exists
                httpResponseStatus = "Message already exists at ".concat(Integer.toString(travelerInfo.getTim().getIndex()));
             } else {
                // Misc error
                httpResponseStatus = "Error code " + rsuResponse.getResponse().getErrorStatus() + " "
                            + rsuResponse.getResponse().getErrorStatusText();
             }

          } catch (Exception e) {
             logger.error("Exception caught in TIM deposit loop.", e);
             httpResponseStatus = e.getClass().getName() + ": " + e.getMessage();
          }
          
          responseList.put(curRsu.getRsuTarget(), httpResponseStatus);
       }

       // Deposit to DDS
       String ddsMessage = "";
       try {
          depositToDDS(travelerInfo, rsuSRMPayload);
          ddsMessage = "\"dds_deposit\":{\"success\":\"true\"}";
          logger.info("DDS deposit successful.");
       } catch (Exception e) {
          ddsMessage = "\"dds_deposit\":{\"success\":\"false\"}";
          logger.error("Error on DDS deposit.", e);
       }

       responseList.put("ddsMessage", ddsMessage);
       
       logger.info("TIM deposit response {}", responseList);
       
       return;
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

    private void depositToDDS(TravelerInputData travelerinputData, String rsuSRMPayload)
          throws ParseException, DdsRequestManagerException, DdsClientException, WebSocketException,
          EncodeFailedException, EncodeNotSupportedException {
       // Step 4 - Step Deposit TIM to SDW if sdw element exists
       if (travelerinputData.getSdw() != null) {
          DdsAdvisorySituationData message = new DdsAdvisorySituationData(
                travelerinputData.getSnmp().getDeliverystart(),
                travelerinputData.getSnmp().getDeliverystop(), 
                rsuSRMPayload,
                travelerinputData.getSdw().getServiceRegion(), 
                travelerinputData.getSdw().getTtl());
          depositor.deposit(message);
       }
    }

}

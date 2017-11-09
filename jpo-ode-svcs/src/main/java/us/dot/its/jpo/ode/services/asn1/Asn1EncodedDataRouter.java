package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import com.oss.asn1.COERCoder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.OctetString;
import com.oss.asn1.PERUnalignedCoder;

import gov.usdot.asn1.generated.ieee1609dot2.Ieee1609dot2;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Opaque;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Uint8;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DFullTime;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.AdvisoryBroadcastType;
import us.dot.its.jpo.ode.j2735.semi.AdvisoryDetails;
import us.dot.its.jpo.ode.j2735.semi.AdvisorySituationData;
import us.dot.its.jpo.ode.j2735.semi.DistributionType;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.TimeToLive;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssGeoRegion;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimController.TimControllerException;
import us.dot.its.jpo.ode.traveler.TimPduCreator;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

public class Asn1EncodedDataRouter extends AbstractSubscriberProcessor<String, String> {

   public class AsdMessage extends OdeObject {

      private static final long serialVersionUID = 8870804435074223135L;

      private AdvisorySituationData asd = new AdvisorySituationData();

      public AsdMessage(String startTime, String stopTime, String advisoryMessage, OdeGeoRegion serviceRegion,
            SituationDataWarehouse.SDW.TimeToLive ttl) throws ParseException {
         super();

         DFullTime dStartTime = dFullTimeFromIsoTimeString(startTime);

         DFullTime dStopTime = dFullTimeFromIsoTimeString(stopTime);

         byte[] fourRandomBytes = new byte[4];
         new Random(System.currentTimeMillis()).nextBytes(fourRandomBytes);

         OctetString oAdvisoryMessage = new OctetString(CodecUtils.fromHex(advisoryMessage));

         byte[] distroType = { 1 };
         asd.asdmDetails = new AdvisoryDetails(new TemporaryID(fourRandomBytes), AdvisoryBroadcastType.tim,
               new DistributionType(distroType), dStartTime, dStopTime, oAdvisoryMessage);

         asd.dialogID = SemiDialogID.advSitDataDep;
         asd.groupID = new GroupID(new byte[]{0,0,0,0});
         asd.requestID = new TemporaryID(fourRandomBytes);
         asd.seqID = new SemiSequenceID(5);
         asd.serviceRegion = OssGeoRegion.geoRegion(serviceRegion);
         if (ttl != null)
            asd.timeToLive = new TimeToLive(ttl.ordinal());
         else
            asd.timeToLive = new TimeToLive(SituationDataWarehouse.SDW.TimeToLive.thirtyminutes.ordinal());
      }

      private DFullTime dFullTimeFromIsoTimeString(String startTime) throws ParseException {
         ZonedDateTime zdtStart = DateTimeUtils.isoDateTime(startTime);
         DFullTime dStartTime = new DFullTime(new DYear(zdtStart.getYear()), new DMonth(zdtStart.getMonthValue()),
               new DDay(zdtStart.getDayOfMonth()), new DHour(zdtStart.getHour()), new DMinute(zdtStart.getMinute()));
         return dStartTime;
      }

      public String encodeHex() throws EncodeFailedException, EncodeNotSupportedException {
         ByteBuffer binAsd = ossUperCoder.encode(asd);
         return CodecUtils.toHex(binAsd.array());
      }

   }

   private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OdeProperties odeProperties;
    private DdsDepositor<DdsStatusMessage> depositor;
    private COERCoder ossCoerCoder;
    private PERUnalignedCoder ossUperCoder;

    public Asn1EncodedDataRouter(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;
      this.ossCoerCoder = Ieee1609dot2.getCOERCoder();
      this.ossUperCoder = J2735.getPERUnalignedCoder();

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
         }

         // Convert JSON to POJO
         TravelerInputData travelerinputData = buildTravelerInputData(consumedObj);

         processEncodedTim(travelerinputData, consumedObj);

      } catch (Exception e) {
         String msg = "Error in processing received message from ASN.1 Encoder module: " + consumedData;
         EventLogger.logger.error(msg, e);
         logger.error(msg, e);
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
          EventLogger.logger.error(errMsg, e);
          logger.error(errMsg, e);
       }

       return travelerinputData;
    }

    public void processEncodedTim(TravelerInputData travelerInfo, JSONObject consumedObj) throws TimControllerException {
       // Send TIMs and record results
       HashMap<String, String> responseList = new HashMap<>();

       JSONObject dataObj = consumedObj
             .getJSONObject(AppContext.PAYLOAD_STRING)
             .getJSONObject(AppContext.DATA_STRING);
       
       if (null != travelerInfo.getSdw()) {
// TODO - Workaround until asn1_codec is ready to encode ieee 1609.2
//        Let's use OSS for creating the ieee msg
//          JSONObject asdObj = dataObj.getJSONObject("AdvisorySituationData");
//          if (null != asdObj) {
//             String asdBytes = asdObj.getString("bytes");
//   
//             // Deposit to DDS
//             String ddsMessage = "";
//             try {
//                depositToDDS(travelerInfo, asdBytes);
//                ddsMessage = "\"dds_deposit\":{\"success\":\"true\"}";
//                logger.info("DDS deposit successful.");
//             } catch (Exception e) {
//                ddsMessage = "\"dds_deposit\":{\"success\":\"false\"}";
//                logger.error("Error on DDS deposit.", e);
//             }
//   
//             responseList.put("ddsMessage", ddsMessage);

         JSONObject mfTimObj = dataObj.getJSONObject("MessageFrame");
         if (null != mfTimObj) {
            String mfTimBytes = mfTimObj.getString("bytes");

            // Deposit to DDS
            String ddsMessage = "";
            try {
               depositToDDSUsingOss(travelerInfo, mfTimBytes);
               ddsMessage = "\"dds_deposit\":{\"success\":\"true\"}";
               logger.info("DDS deposit successful.");
            } catch (Exception e) {
               ddsMessage = "\"dds_deposit\":{\"success\":\"false\"}";
               logger.error("Error on DDS deposit.", e);
            }

            responseList.put("ddsMessage", ddsMessage);
         } else {
            String msg = "ASN.1 Encoder did not return ASD encoding {}";
            EventLogger.logger.error(msg, consumedObj.toString());
            logger.error(msg, consumedObj.toString());
         }
          
       }
       
       JSONObject mfObj = dataObj.getJSONObject("MessageFrame");
       
      // only send message to rsu if snmp, rsus, and message frame fields are present
      if (null != travelerInfo.getSnmp() && null != travelerInfo.getRsus() && null != mfObj) {
         String timBytes = mfObj.getString("bytes");
         for (RSU curRsu : travelerInfo.getRsus()) {

            ResponseEvent rsuResponse = null;
            String httpResponseStatus = null;

             try {
                rsuResponse = createAndSend(travelerInfo.getSnmp(), curRsu, 
                   travelerInfo.getOde().getIndex(), timBytes);

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
                String msg = "Exception caught in TIM deposit loop.";
               EventLogger.logger.error(msg, e);
                logger.error(msg, e);
                httpResponseStatus = e.getClass().getName() + ": " + e.getMessage();
             }
             
             responseList.put(curRsu.getRsuTarget(), httpResponseStatus);
          }

       }
       
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
       EventLogger.logger.info("Message Sent to {}: {}", rsu.getRsuTarget(), payload);
       return response;
    }

    private void depositToDDS(TravelerInputData travelerinputData, String asdBytes)
          throws ParseException, DdsRequestManagerException, DdsClientException, WebSocketException,
          EncodeFailedException, EncodeNotSupportedException {
       // Step 4 - Step Deposit TIM to SDW if sdw element exists
       if (travelerinputData.getSdw() != null) {
          depositor.deposit(asdBytes);
          EventLogger.logger.info("Message Deposited to SDW: {}", asdBytes);
       }
    }

    /**
    * Temporary method using OSS to build a ASD with IEEE 1609.2 encapsulating MF/TIM
    * @param travelerInputData
    * @param mfTimBytes
    * @throws ParseException
    * @throws EncodeFailedException
    * @throws DdsRequestManagerException
    * @throws DdsClientException
    * @throws WebSocketException
    * @throws EncodeNotSupportedException
    */
   private void depositToDDSUsingOss(TravelerInputData travelerInputData, String mfTimBytes) 
         throws ParseException, EncodeFailedException, DdsRequestManagerException, DdsClientException, WebSocketException, EncodeNotSupportedException {
      // Step 4 - Step Deposit IEEE 1609.2 wrapped TIM to SDW if sdw element exists
      SDW sdw = travelerInputData.getSdw();
      if (sdw != null) {
         Ieee1609Dot2Data ieee1609Data = new Ieee1609Dot2Data();
         ieee1609Data.setProtocolVersion(new Uint8(3));
         Ieee1609Dot2Content ieee1609Dot2Content = new Ieee1609Dot2Content();
         ieee1609Dot2Content.setUnsecuredData(new Opaque(CodecUtils.fromHex(mfTimBytes)));
         ieee1609Data.setContent(ieee1609Dot2Content);
         ByteBuffer ieee1609DataBytes = ossCoerCoder.encode(ieee1609Data);
         
         // take deliverystart and stop times from SNMP object, if present
         // else take from SDW object
         SNMP snmp = travelerInputData.getSnmp();
         AsdMessage asdMsg = null;
         if (null != snmp) {
            asdMsg = new AsdMessage(
               snmp.getDeliverystart(),
               snmp.getDeliverystop(), 
               CodecUtils.toHex(ieee1609DataBytes.array()),
               sdw.getServiceRegion(), 
               sdw.getTtl());
         } else {
            asdMsg = new AsdMessage(
               sdw.getDeliverystart(),
               sdw.getDeliverystop(), 
               CodecUtils.toHex(ieee1609DataBytes.array()),
               sdw.getServiceRegion(), 
               sdw.getTtl());
         }

         depositor.deposit(asdMsg.encodeHex());
         EventLogger.logger.info("Message Deposited to SDW: {}", mfTimBytes);
      }
      
   }

}

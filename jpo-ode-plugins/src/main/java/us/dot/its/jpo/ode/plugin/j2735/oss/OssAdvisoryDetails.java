package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.j2735.semi.AdvisoryDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisoryDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisoryDetails.J2735AdvisoryBroadcastType;
import us.dot.its.jpo.ode.plugin.j2735.J2735DateTime;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssAdvisoryDetails {

   private static final Logger logger = LoggerFactory.getLogger(OssAdvisoryDetails.class);

   private OssAdvisoryDetails() {
   }

   public static J2735AdvisoryDetails genericAdvisoryDetails(AdvisoryDetails asdmDetails) {
      J2735AdvisoryDetails gad = new J2735AdvisoryDetails();

      if (asdmDetails.asdmID != null)
         gad.setId(CodecUtils.toHex(asdmDetails.asdmID.byteArrayValue()));

      if (asdmDetails.asdmType != null)
         gad.setType(J2735AdvisoryBroadcastType.valueOf(asdmDetails.asdmType.name()));

      if (asdmDetails.distType != null)
         gad.setDistType(CodecUtils.toHex(asdmDetails.distType.byteArrayValue()));

      if (asdmDetails.hasStartTime()) {
         gad.setStartTime(new J2735DateTime(asdmDetails.getStartTime()).getISODateTime());
      }

      if (asdmDetails.hasStopTime()) {
         gad.setStopTime(new J2735DateTime(asdmDetails.getStopTime()).getISODateTime());
      }

      if (asdmDetails.advisoryMessage != null) {
         TravelerInformation timPOJO = decodeAdvisoryMessage(asdmDetails.advisoryMessage.byteArrayValue());
         gad.setTravelerInfo(OssTravelerInfo.genericTravelerInfo(timPOJO));
      }

      return gad;
   }

   private static TravelerInformation decodeAdvisoryMessage(byte[] message) {
      InputStream ins = new ByteArrayInputStream(message);

      Coder coder = J2735.getPERUnalignedCoder();
      TravelerInformation tim = new TravelerInformation();
      try {
         coder.decode(ins, tim);
      } catch (Exception e) {
         logger.error("Error decoding message: " + message, e);
      } finally {
         try {
            ins.close();
         } catch (IOException e) {
            logger.warn("Error closing input stream: ", e);
         }
      }
      return tim;
   }
}

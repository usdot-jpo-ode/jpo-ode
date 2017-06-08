package us.dot.its.jpo.ode.traveler;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Random;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.OctetString;
import com.oss.asn1.PERUnalignedCoder;

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
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssGeoRegion;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class AsdMessage extends OdeObject {

   private static final long serialVersionUID = 8870804435074223135L;
   
   private AdvisorySituationData asd = new AdvisorySituationData();

   public AsdMessage(
         String startTime,
         String stopTime,
         String advisoryMessage,
         J2735GeoRegion serviceRegion,
         SituationDataWarehouse.SDW.TimeToLive ttl) throws ParseException {
      super();
      
      DFullTime dStartTime = dFullTimeFromIsoTimeString(startTime);
      
      DFullTime dStopTime = dFullTimeFromIsoTimeString(stopTime);

      byte[] fourRandomBytes = new byte[4];
      new Random(System.currentTimeMillis()).nextBytes(fourRandomBytes);

      OctetString oAdvisoryMessage = new OctetString(CodecUtils.fromHex(advisoryMessage));
      
      byte[] distroType = {1};
      asd.asdmDetails = new AdvisoryDetails(
            new TemporaryID(fourRandomBytes), 
            AdvisoryBroadcastType.tim,
            new DistributionType(distroType), dStartTime, dStopTime, oAdvisoryMessage);

      asd.dialogID = SemiDialogID.advSitDataDep;
      asd.groupID = new GroupID("jode".getBytes());
      asd.requestID = new TemporaryID(fourRandomBytes);
      asd.seqID = new SemiSequenceID(5);
      asd.serviceRegion = OssGeoRegion.geoRegion(serviceRegion);
      if (ttl != null)
         asd.timeToLive = new TimeToLive(ttl.ordinal());
      else
         asd.timeToLive = new TimeToLive(SituationDataWarehouse.SDW.TimeToLive.THIRTYMINUTES.ordinal());
   }

   private DFullTime dFullTimeFromIsoTimeString(String startTime) throws ParseException {
      ZonedDateTime zdtStart = DateTimeUtils.isoDateTime(startTime);
      DFullTime dStartTime = new DFullTime(
            new DYear(zdtStart.getYear()),
            new DMonth(zdtStart.getMonthValue()),
            new DDay(zdtStart.getDayOfMonth()),
            new DHour(zdtStart.getHour()),
            new DMinute(zdtStart.getMinute()));
      return dStartTime;
   }

   public String encodeHex() throws EncodeFailedException, EncodeNotSupportedException {
      
      PERUnalignedCoder coder = J2735.getPERUnalignedCoder();
      
      ByteBuffer binAsd = coder.encode(asd);

      return CodecUtils.toHex(binAsd.array());
   }
}

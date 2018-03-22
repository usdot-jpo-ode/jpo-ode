package us.dot.its.jpo.ode.plugin.j2735;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Random;

import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisoryDetails.AdvisoryBroadcastType;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisoryDetails.DistributionType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class DdsAdvisorySituationData extends Asn1Object {
   private static final long serialVersionUID = 2755274323293805425L;

   int dialogID = 0x9C;    // SemiDialogID -- 0x9C Advisory Situation Data Deposit
   int seqID = 0x05;       // SemiSequenceID -- 0x05 Data
   String groupID;         // GroupID -- unique ID used to identify an organization
   String requestID;       // DSRC.TemporaryID -- random 4 byte ID generated for data
                           // transfer
   String recordID;        // DSRC.TemporaryID -- used by the provider to overwrite
                           // existing record(s)
   int timeToLive;         // TimeToLive -- indicates how long the SDW should persist
                            // the record(s)
   DdsGeoRegion serviceRegion; // GeoRegion, -- NW and SE corners of the region
                               // applicable
   DdsAdvisoryDetails asdmDetails;

   public DdsAdvisorySituationData() {
      super();
      byte[] gid = { 0, 0, 0, 0 };
      groupID = CodecUtils.toHex(gid);
   }

   public DdsAdvisorySituationData(String startTime, String stopTime, Ieee1609Dot2DataTag advisoryMessage,
         DdsGeoRegion serviceRegion, SituationDataWarehouse.SDW.TimeToLive ttl, String groupID, String recordID) throws ParseException {
      this();

      J2735DFullTime dStartTime = dFullTimeFromIsoTimeString(startTime);

      J2735DFullTime dStopTime = dFullTimeFromIsoTimeString(stopTime);

      byte[] fourRandomBytes = new byte[4];
      new Random(System.currentTimeMillis()).nextBytes(fourRandomBytes);
      String id = CodecUtils.toHex(fourRandomBytes);
      int distroType = DistributionType.rsu.ordinal();
      this.setAsdmDetails(
            new DdsAdvisoryDetails(id, AdvisoryBroadcastType.tim, distroType, dStartTime, dStopTime, advisoryMessage));

      this.setRequestID(id);
      this.setServiceRegion(serviceRegion);
      if (ttl != null) {
         this.setTimeToLive(ttl.ordinal());
      } else {
         this.setTimeToLive(SituationDataWarehouse.SDW.TimeToLive.thirtyminutes.ordinal());
      }
      if (groupID != null) {
         this.setGroupID(groupID);
      } else {
         this.setGroupID(CodecUtils.toHex(new byte[] { 0, 0, 0, 0 }));
      }
      if (recordID != null) {
         this.setRecordID(recordID);
      } else {
         this.setRecordID(CodecUtils.toHex(new byte[] { 0, 0, 0, 0 }));
      }
   }

   private J2735DFullTime dFullTimeFromIsoTimeString(String isoTime) throws ParseException {

      J2735DFullTime dStartTime = new J2735DFullTime();
      
      // use time if present, if not use undefined flag values
      if (null != isoTime) {
         ZonedDateTime zdtTime = DateTimeUtils.isoDateTime(isoTime);
         dStartTime.setYear(zdtTime.getYear());
         dStartTime.setMonth(zdtTime.getMonthValue());
         dStartTime.setDay(zdtTime.getDayOfMonth());
         dStartTime.setHour(zdtTime.getHour());
         dStartTime.setMinute(zdtTime.getMinute());
         // dStartTime.setSecond(zdtTime.getSecond());
         // dStartTime.setOffset(zdtTime.getOffset().getTotalSeconds());
      } else {
         dStartTime.setYear(0);
         dStartTime.setMonth(0);
         dStartTime.setDay(0);
         dStartTime.setHour(31);
         dStartTime.setMinute(60);
      }
      return dStartTime;
   }

   public int getDialogID() {
      return dialogID;
   }

   public void setDialogID(int dialogID) {
      this.dialogID = dialogID;
   }

   public int getSeqID() {
      return seqID;
   }

   public void setSeqID(int seqID) {
      this.seqID = seqID;
   }

   public String getGroupID() {
      return groupID;
   }

   public void setGroupID(String groupID) {
      this.groupID = groupID;
   }

   public String getRequestID() {
      return requestID;
   }

   public void setRequestID(String requestID) {
      this.requestID = requestID;
   }

   public String getRecordID() {
      return recordID;
   }

   public void setRecordID(String recordID) {
      this.recordID = recordID;
   }

   public int getTimeToLive() {
      return timeToLive;
   }

   public void setTimeToLive(int timeToLive) {
      this.timeToLive = timeToLive;
   }

   public DdsGeoRegion getServiceRegion() {
      return serviceRegion;
   }

   public void setServiceRegion(DdsGeoRegion serviceRegion) {
      this.serviceRegion = serviceRegion;
   }

   public DdsAdvisoryDetails getAsdmDetails() {
      return asdmDetails;
   }

   public void setAsdmDetails(DdsAdvisoryDetails asdmDetails) {
      this.asdmDetails = asdmDetails;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((asdmDetails == null) ? 0 : asdmDetails.hashCode());
      result = prime * result + dialogID;
      result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
      result = prime * result + ((recordID == null) ? 0 : recordID.hashCode());
      result = prime * result + ((requestID == null) ? 0 : requestID.hashCode());
      result = prime * result + seqID;
      result = prime * result + ((serviceRegion == null) ? 0 : serviceRegion.hashCode());
      result = prime * result + timeToLive;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DdsAdvisorySituationData other = (DdsAdvisorySituationData) obj;
      if (asdmDetails == null) {
         if (other.asdmDetails != null)
            return false;
      } else if (!asdmDetails.equals(other.asdmDetails))
         return false;
      if (dialogID != other.dialogID)
         return false;
      if (groupID == null) {
         if (other.groupID != null)
            return false;
      } else if (!groupID.equals(other.groupID))
         return false;
      if (recordID == null) {
         if (other.recordID != null)
            return false;
      } else if (!recordID.equals(other.recordID))
         return false;
      if (requestID == null) {
         if (other.requestID != null)
            return false;
      } else if (!requestID.equals(other.requestID))
         return false;
      if (seqID != other.seqID)
         return false;
      if (serviceRegion == null) {
         if (other.serviceRegion != null)
            return false;
      } else if (!serviceRegion.equals(other.serviceRegion))
         return false;
      if (timeToLive != other.timeToLive)
         return false;
      return true;
   }

}

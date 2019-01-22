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
package us.dot.its.jpo.ode.plugin.j2735;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Random;

import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisoryDetails.AdvisoryBroadcastType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class DdsAdvisorySituationData extends Asn1Object {
   private static final long serialVersionUID = 2755274323293805425L;
   
   // Distribution Type field values
   public static final byte NONE = (byte) 0x00;
   public static final byte RSU = (byte) 0x01;
   public static final byte IP = (byte) 0x02;

   private int dialogID = 0x9C;    // SemiDialogID -- 0x9C Advisory Situation Data Deposit
   private int seqID = 0x05;       // SemiSequenceID -- 0x05 Data
   private String groupID;         // GroupID -- unique ID used to identify an organization
   private String requestID;       // DSRC.TemporaryID -- random 4 byte ID generated for data
                           // transfer
   private String recordID;        // DSRC.TemporaryID -- used by the provider to overwrite
                           // existing record(s)
   private int timeToLive;         // TimeToLive -- indicates how long the SDW should persist
                            // the record(s)
   private DdsGeoRegion serviceRegion; // GeoRegion, -- NW and SE corners of the region
                               // applicable
   private DdsAdvisoryDetails asdmDetails;

   public DdsAdvisorySituationData() {
      super();
      byte[] gid = { 0, 0, 0, 0 };
      groupID = CodecUtils.toHex(gid);
   }

   public J2735DFullTime dFullTimeFromIsoTimeString(String isoTime) throws ParseException {

      J2735DFullTime dStartTime = new J2735DFullTime();
      
      // use time if present, if not use undefined flag values
      if (null != isoTime) {
         ZonedDateTime zdtTime = DateTimeUtils.isoDateTime(isoTime);
         dStartTime.setYear(zdtTime.getYear());
         dStartTime.setMonth(zdtTime.getMonthValue());
         dStartTime.setDay(zdtTime.getDayOfMonth());
         dStartTime.setHour(zdtTime.getHour());
         dStartTime.setMinute(zdtTime.getMinute());
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

   public DdsAdvisorySituationData setDialogID(int dialogID) {
      this.dialogID = dialogID;
      return this;
   }

   public int getSeqID() {
      return seqID;
   }

   public DdsAdvisorySituationData setSeqID(int seqID) {
      this.seqID = seqID;
      return this;
   }

   public String getGroupID() {
      return groupID;
   }

   public DdsAdvisorySituationData setGroupID(String groupID) {
     if (groupID != null) {
       this.groupID = groupID;
     } else {
       this.groupID = CodecUtils.toHex(new byte[] { 0, 0, 0, 0 });
     }
     return this;
   }

   public String getRequestID() {
      return requestID;
   }

   public DdsAdvisorySituationData setRequestID(String requestID) {
      this.requestID = requestID;
      return this;
   }

   public String getRecordID() {
      return recordID;
   }

   public DdsAdvisorySituationData setRecordID(String recordID) {
     if (recordID != null) {
       this.recordID = recordID;
     } else {
       this.recordID = CodecUtils.toHex(new byte[] { 0, 0, 0, 0 });
     }
     return this;
   }

   public int getTimeToLive() {
      return timeToLive;
   }

   public DdsAdvisorySituationData setTimeToLive(int timeToLive) {
      this.timeToLive = timeToLive;
      return this;
   }

   public DdsAdvisorySituationData setTimeToLive(SituationDataWarehouse.SDW.TimeToLive timeToLive) {
     if (timeToLive != null) {
       this.setTimeToLive(timeToLive.ordinal());
     } else {
       this.setTimeToLive(SituationDataWarehouse.SDW.TimeToLive.thirtyminutes.ordinal());
     }
     return this;
   }

   public DdsGeoRegion getServiceRegion() {
      return serviceRegion;
   }

   public DdsAdvisorySituationData setServiceRegion(DdsGeoRegion serviceRegion) {
      this.serviceRegion = serviceRegion;
      return this;
   }

   public DdsAdvisoryDetails getAsdmDetails() {
      return asdmDetails;
   }

   public DdsAdvisorySituationData setAsdmDetails(DdsAdvisoryDetails asdmDetails) {
     this.asdmDetails = asdmDetails;
     return this;
   }

   public DdsAdvisorySituationData setAsdmDetails(String startTime, String stopTime, byte distroType, Ieee1609Dot2DataTag advisoryMessage) throws ParseException {
     J2735DFullTime dStartTime = dFullTimeFromIsoTimeString(startTime);

     J2735DFullTime dStopTime = dFullTimeFromIsoTimeString(stopTime);
     String stringDistroType = CodecUtils.toHex(distroType);

     byte[] fourRandomBytes = new byte[4];
     new Random(System.currentTimeMillis()).nextBytes(fourRandomBytes);

     String id = CodecUtils.toHex(fourRandomBytes);
     this.setRequestID(id);

     this.setAsdmDetails(
         new DdsAdvisoryDetails(id, AdvisoryBroadcastType.tim, stringDistroType, dStartTime, dStopTime, advisoryMessage));
     return this;
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

package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class DdsAdvisoryDetails extends Asn1Object {
   private static final long serialVersionUID = 8964772115424427026L;

   public enum AdvisoryBroadcastType {
      spatAggregate, //  (0),
      map,           //  (1),
      tim,           //  (2),
      ev             //  (3),
   }

   public enum DistributionType {
      none, //(0),  "00000000", not intended for redistribution
      rsu,  //(1),  "00000001", intended for redistribution over DSRC
      ip    //(2),  "00000010"  intended for redistribution over IP
   }
   
   String asdmID;                   //         DSRC.TemporaryID,
   int asdmType;                    //    AdvisoryBroadcastType,
   int distType;                    //0, 1 or 2    ,
   J2735DFullTime startTime;        //OPTIONAL,
   J2735DFullTime stopTime;         //OPTIONAL,
   String advisoryMessageBytes;          //  OCTET STRING (SIZE(0..1400))  -- Encoded advisory message
   J2735MessageFrame advisoryMessage;


   public DdsAdvisoryDetails() {
      super();
   }

   
   public String getAsdmID() {
      return asdmID;
   }
   
   public DdsAdvisoryDetails(String asdmID, AdvisoryBroadcastType asdmType, int distType, J2735DFullTime startTime,
         J2735DFullTime stopTime, J2735MessageFrame advisoryMessage) {
      super();
      this.asdmID = asdmID;
      this.asdmType = asdmType.ordinal();
      this.distType = distType;
      this.startTime = startTime;
      this.stopTime = stopTime;
      this.advisoryMessage = advisoryMessage;
   }

   public void setAsdmID(String asdmID) {
      this.asdmID = asdmID;
   }
   public int getAsdmType() {
      return asdmType;
   }
   public void setAsdmType(int asdmType) {
      this.asdmType = asdmType;
   }
   public int getDistType() {
      return distType;
   }
   public void setDistType(int distType) {
      this.distType = distType;
   }
   public J2735DFullTime getStartTime() {
      return startTime;
   }
   public void setStartTime(J2735DFullTime startTime) {
      this.startTime = startTime;
   }
   public J2735DFullTime getStopTime() {
      return stopTime;
   }
   public void setStopTime(J2735DFullTime stopTime) {
      this.stopTime = stopTime;
   }
   public String getAdvisoryMessageBytes() {
      return advisoryMessageBytes;
   }
   public void setAdvisoryMessageBytes(String advisoryMessageBytes) {
      this.advisoryMessageBytes = advisoryMessageBytes;
   }
   public J2735MessageFrame getAdvisoryMessage() {
      return advisoryMessage;
   }
   public void setAdvisoryMessage(J2735MessageFrame advisoryMessage) {
      this.advisoryMessage = advisoryMessage;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((advisoryMessage == null) ? 0 : advisoryMessage.hashCode());
      result = prime * result + ((advisoryMessageBytes == null) ? 0 : advisoryMessageBytes.hashCode());
      result = prime * result + ((asdmID == null) ? 0 : asdmID.hashCode());
      result = prime * result + asdmType;
      result = prime * result + distType;
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((stopTime == null) ? 0 : stopTime.hashCode());
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
      DdsAdvisoryDetails other = (DdsAdvisoryDetails) obj;
      if (advisoryMessage == null) {
         if (other.advisoryMessage != null)
            return false;
      } else if (!advisoryMessage.equals(other.advisoryMessage))
         return false;
      if (advisoryMessageBytes == null) {
         if (other.advisoryMessageBytes != null)
            return false;
      } else if (!advisoryMessageBytes.equals(other.advisoryMessageBytes))
         return false;
      if (asdmID == null) {
         if (other.asdmID != null)
            return false;
      } else if (!asdmID.equals(other.asdmID))
         return false;
      if (asdmType != other.asdmType)
         return false;
      if (distType != other.distType)
         return false;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else if (!startTime.equals(other.startTime))
         return false;
      if (stopTime == null) {
         if (other.stopTime != null)
            return false;
      } else if (!stopTime.equals(other.stopTime))
         return false;
      return true;
   }
   
   
}

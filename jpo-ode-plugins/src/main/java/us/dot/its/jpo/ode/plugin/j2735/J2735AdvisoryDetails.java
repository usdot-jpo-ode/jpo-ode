package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AdvisoryDetails extends Asn1Object {

   private static final long serialVersionUID = -6852036031529394630L;

   public enum OdeAdvisoryBroadcastType {
      spatAggregate, 
      map, 
      tim, 
      ev
   }
   
   public static final int DIST_TYPE_NONE = 0x00;
   public static final int DIST_TYPE_RSU  = 0x01;
   public static final int DIST_TYPE_IP   = 0x02;
   
   
   private String id;
   private OdeAdvisoryBroadcastType type;
   private String distType;
   private String startTime;
   private String stopTime;
   private J2735TravelerInfo travelerInfo;
   
   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssAdvisoryDetails class
//   public J2735AdvisoryDetails(AdvisoryDetails asdmDetails) throws UnsupportedEncodingException {
//      if (asdmDetails.asdmID != null)
//         setId(CodecUtils.toHex(asdmDetails.asdmID.byteArrayValue()));
//      
//      if (asdmDetails.asdmType != null)
//         setType(OdeAdvisoryBroadcastType.valueOf(asdmDetails.asdmType.name()));
//      
//      if (asdmDetails.distType != null)
//         setDistType(CodecUtils.toHex(asdmDetails.distType.byteArrayValue()));
//      
//      if (asdmDetails.hasStartTime()) {
//         setStartTime(new J2735DateTime(asdmDetails.getStartTime()).getISODateTime());
//      }
//      
//      if (asdmDetails.hasStopTime()) {
//         setStopTime(new J2735DateTime(asdmDetails.getStopTime()).getISODateTime());
//      }
//
//      if (asdmDetails.advisoryMessage != null) {
//         TravelerInformation timPOJO = 
//               decodeAdvisoryMessage(asdmDetails.advisoryMessage.byteArrayValue());
//         travelerInfo = new J2735TravelerInfo(timPOJO);
//      }
//      
//   }
//   
//   private TravelerInformation decodeAdvisoryMessage(byte[] message) {
//      InputStream ins = new ByteArrayInputStream(message);
//
//      Coder coder = J2735.getPERUnalignedCoder();
//      TravelerInformation tim = new TravelerInformation();
//      try {
//         coder.decode(ins, tim);
//      } catch (Exception e) {
//         logger.error("Error decoding message: " + message, e);
//      } finally {
//         try {
//            ins.close();
//         } catch (IOException e) {
//            logger.warn("Error closing input stream: ", e);
//         }
//      }
//      return tim;
//   }

   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public OdeAdvisoryBroadcastType getType() {
      return type;
   }
   public void setType(OdeAdvisoryBroadcastType type) {
      this.type = type;
   }
   public String getDistType() {
      return distType;
   }
   public void setDistType(String distType) {
      this.distType = distType;
   }
   public String getStartTime() {
      return startTime;
   }
   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }
   public String getStopTime() {
      return stopTime;
   }
   public void setStopTime(String stopTime) {
      this.stopTime = stopTime;
   }

   public J2735TravelerInfo getTravelerInfo() {
      return travelerInfo;
   }

   public void setTravelerInfo(J2735TravelerInfo travelerInfo) {
      this.travelerInfo = travelerInfo;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((distType == null) ? 0 : distType.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((travelerInfo == null) ? 0 : travelerInfo.hashCode());
      result = prime * result
            + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((stopTime == null) ? 0 : stopTime.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      J2735AdvisoryDetails other = (J2735AdvisoryDetails) obj;
      if (distType == null) {
         if (other.distType != null)
            return false;
      } else if (!distType.equals(other.distType))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (travelerInfo == null) {
         if (other.travelerInfo != null)
            return false;
      } else if (!travelerInfo.equals(other.travelerInfo))
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
      if (type != other.type)
         return false;
      return true;
   }
   
}

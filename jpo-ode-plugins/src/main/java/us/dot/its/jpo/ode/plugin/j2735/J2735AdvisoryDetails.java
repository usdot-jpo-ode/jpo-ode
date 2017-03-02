package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AdvisoryDetails extends Asn1Object {

   private static final long serialVersionUID = -6852036031529394630L;

   public enum J2735AdvisoryBroadcastType {
      spatAggregate, 
      map, 
      tim, 
      ev
   }
   
   public static final int DIST_TYPE_NONE = 0x00;
   public static final int DIST_TYPE_RSU  = 0x01;
   public static final int DIST_TYPE_IP   = 0x02;
   
   
   private String id;
   private J2735AdvisoryBroadcastType type;
   private String distType;
   private String startTime;
   private String stopTime;
   private J2735TravelerInfo travelerInfo;
   

   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public J2735AdvisoryBroadcastType getType() {
      return type;
   }
   public void setType(J2735AdvisoryBroadcastType type) {
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

   public J2735AdvisoryDetails setTravelerInfo(J2735TravelerInfo travelerInfo) {
      this.travelerInfo = travelerInfo;
      return this;
   }

}

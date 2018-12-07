package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class MsgId extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private RoadSignID roadSignID;
   private String furtherInfoID;

   public RoadSignID getRoadSignID() {
      return roadSignID;
   }

   public void setRoadSignID(RoadSignID roadSignID) {
      this.roadSignID = roadSignID;
   }

   public String getFurtherInfoID() {
      return furtherInfoID;
   }

   public void setFurtherInfoID(String furtherInfoID) {
      this.furtherInfoID = furtherInfoID;
   }
}

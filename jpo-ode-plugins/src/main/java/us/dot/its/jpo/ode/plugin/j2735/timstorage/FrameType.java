package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class FrameType extends Asn1Object {

   private static final long serialVersionUID = 1L;

   public enum TravelerInfoType {
      unknown,
      advisory,
      roadSignage,
      commercialSignage
   }

   private String advisory;

   private String commercialSignage;

   private String roadSignage;

   private String unknown;

   public String getAdvisory() {
      return advisory;
   }

   public void setAdvisory(String advisory) {
      this.advisory = advisory;
   }

   public String getUnknown() {
      return unknown;
   }

   public void setUnknown(String unknown) {
      this.unknown = unknown;
   }

   public String getRoadSignage() {
      return roadSignage;
   }

   public void setRoadSignage(String roadSignage) {
      this.roadSignage = roadSignage;
   }

   public String getCommercialSignage() {
      return commercialSignage;
   }

   public void setCommercialSignage(String commercialSignage) {
      this.commercialSignage = commercialSignage;
   }
}

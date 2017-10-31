package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class FrameType extends OdeObject {

   private static final long serialVersionUID = 1L;

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

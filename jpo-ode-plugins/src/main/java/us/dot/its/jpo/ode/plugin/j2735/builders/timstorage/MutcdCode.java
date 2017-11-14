package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class MutcdCode extends Asn1Object {
   private static final long serialVersionUID = 1L;
   
   private String none; // (0), -- non-MUTCD information
   private String regulatory; // (1), -- "R" Regulatory signs
   private String warning; // (2), -- "W" warning signs
   private String maintenance; // (3), -- "M" Maintenance and construction
   private String motoristService; // (4), -- Motorist Services
   private String guide; // (5), -- "G" Guide signs
   private String rec; // 6

   public String getGuide() {
      return guide;
   }

   public void setGuide(String guide) {
      this.guide = guide;
   }

   public String getNone() {
      return none;
   }

   public void setNone(String none) {
      this.none = none;
   }

   public String getRegulatory() {
      return regulatory;
   }

   public void setRegulatory(String regulatory) {
      this.regulatory = regulatory;
   }

   public String getWarning() {
      return warning;
   }

   public void setWarning(String warning) {
      this.warning = warning;
   }

   public String getMaintenance() {
      return maintenance;
   }

   public void setMaintenance(String maintenance) {
      this.maintenance = maintenance;
   }

   public String getMotoristService() {
      return motoristService;
   }

   public void setMotoristService(String motoristService) {
      this.motoristService = motoristService;
   }

   public String getRec() {
      return rec;
   }

   public void setRec(String rec) {
      this.rec = rec;
   }
}

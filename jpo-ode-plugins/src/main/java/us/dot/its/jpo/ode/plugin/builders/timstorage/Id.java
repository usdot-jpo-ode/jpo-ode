package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Id extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private String region;

   private String id;

   public String getRegion() {
      return region;
   }

   public void setRegion(String region) {
      this.region = region;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }
}

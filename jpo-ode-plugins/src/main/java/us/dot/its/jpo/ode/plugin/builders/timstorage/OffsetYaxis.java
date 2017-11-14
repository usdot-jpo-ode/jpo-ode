package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class OffsetYaxis extends Asn1Object {
   private static final long serialVersionUID = 7703530986729802700L;

   private Integer small;
   
   private Integer large;

   public Integer getSmall() {
      return small;
   }

   public void setSmall(Integer small) {
      this.small = small;
   }

   public Integer getLarge() {
      return large;
   }

   public void setLarge(Integer large) {
      this.large = large;
   }

}

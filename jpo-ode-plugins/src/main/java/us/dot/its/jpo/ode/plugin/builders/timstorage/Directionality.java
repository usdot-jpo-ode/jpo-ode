package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Directionality extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private String both;

   public String getBoth() {
      return both;
   }

   public void setBoth(String both) {
      this.both = both;
   }
}

package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Elevation extends Asn1Object {
   private static final long serialVersionUID = 2195525707276004095L;

   BigDecimal elevation;

   public J2735Elevation() {
      super();
   }

   public J2735Elevation(BigDecimal elevation) {
      super();
      this.elevation = elevation;
   }
}

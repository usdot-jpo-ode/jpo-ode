package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Longitude extends Asn1Object {

   private static final long serialVersionUID = 1506381730119918013L;

   BigDecimal longitude;

   public J2735Longitude() {
      super();
   }

   public J2735Longitude(BigDecimal longitude) {
      super();
      this.longitude = longitude;
   }
}

package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Latitude;

public class OssLatitude {

   private OssLatitude() {

   }

   public static Latitude latitude(BigDecimal lat) {
      return new Latitude(lat.scaleByPowerOfTen(7).intValue());
   }
}

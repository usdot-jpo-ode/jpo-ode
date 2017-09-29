package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Latitude;

public class OssLatitude {

   private OssLatitude() {
      throw new UnsupportedOperationException();
   }

   public static Latitude latitude(BigDecimal lat) {
      return new Latitude(lat.scaleByPowerOfTen(7).intValue());
   }

   public static BigDecimal genericLatitude(Latitude lat) {
      BigDecimal returnValue = null;

      if ((lat != null) && (lat.longValue() != 900000001)) {
         returnValue = BigDecimal.valueOf(lat.longValue(), 7);

      }
      return returnValue;
   }
}

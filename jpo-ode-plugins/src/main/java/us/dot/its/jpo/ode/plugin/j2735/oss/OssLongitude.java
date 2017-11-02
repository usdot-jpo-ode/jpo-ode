package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Longitude;

public class OssLongitude {

   private OssLongitude() {
      throw new UnsupportedOperationException();
   }

   public static Longitude longitude(BigDecimal lon) {
      return new Longitude(lon.scaleByPowerOfTen(7).intValue());
   }

   public static BigDecimal genericLongitude(Longitude _long) {
      BigDecimal returnValue = null;

      if ((_long != null) && (_long.longValue() != 1800000001)) {
         returnValue = BigDecimal.valueOf(_long.longValue(), 7);

      }
      return returnValue;
   }
}

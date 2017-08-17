package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Longitude;

public class OssLongitude {

   private OssLongitude() {
   }

   public static Longitude longitude(BigDecimal lon) {
      return new Longitude(lon.scaleByPowerOfTen(7).intValue());
   }

}

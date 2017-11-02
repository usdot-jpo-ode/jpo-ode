package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;

public class OssElevation {

   private OssElevation() {
      throw new UnsupportedOperationException();
   }

   public static BigDecimal genericElevation(Elevation elev) {
      BigDecimal returnValue = null;

      if ((elev != null) && (elev.longValue() != -4096)) {
         returnValue = BigDecimal.valueOf(elev.longValue(), 1);

      }
      return returnValue;
   }
}

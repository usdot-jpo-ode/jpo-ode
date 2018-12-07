package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB12Builder {

   private OffsetB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetB12(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).longValue();
   }
}

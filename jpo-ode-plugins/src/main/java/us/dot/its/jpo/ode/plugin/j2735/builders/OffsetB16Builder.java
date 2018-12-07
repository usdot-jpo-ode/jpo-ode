package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB16Builder {

   private OffsetB16Builder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetB16(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).longValue();
   }
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLB16Builder {

   private OffsetLLB16Builder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetLLB16(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).longValue();
   }
}

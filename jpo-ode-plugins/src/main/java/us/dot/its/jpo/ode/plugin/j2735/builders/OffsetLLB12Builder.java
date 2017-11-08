package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLB12Builder {

   private OffsetLLB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetLLB12(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).longValue();
   }
}

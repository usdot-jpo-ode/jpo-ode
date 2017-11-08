package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLB18Builder {

   private OffsetLLB18Builder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetLLB18(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).longValue();
   }
}

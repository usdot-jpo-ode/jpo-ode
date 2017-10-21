package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLB22Builder {

   private OffsetLLB22Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetLLB22(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).intValue();
   }
}

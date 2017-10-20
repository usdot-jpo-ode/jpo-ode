package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLB14Builder {

   private OffsetLLB14Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetLLB14(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).intValue();
   }
}

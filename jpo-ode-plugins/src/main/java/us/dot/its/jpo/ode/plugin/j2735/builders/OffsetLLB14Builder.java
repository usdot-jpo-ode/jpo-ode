package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B14;

public class OffsetLLB14Builder {

   private OffsetLLB14Builder() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B14 offsetLLB14(BigDecimal offset) {
      return new OffsetLL_B14(offset.scaleByPowerOfTen(7).intValue());
   }

}

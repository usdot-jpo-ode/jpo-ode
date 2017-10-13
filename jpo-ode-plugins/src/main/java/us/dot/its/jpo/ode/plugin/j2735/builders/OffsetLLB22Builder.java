package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B22;

public class OffsetLLB22Builder {

   private OffsetLLB22Builder() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B22 offsetLLB22(BigDecimal offset) {
      return new OffsetLL_B22(offset.scaleByPowerOfTen(7).intValue());
   }

}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B12;

public class OffsetLLB12Builder {

   private OffsetLLB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B12 offsetLLB12(BigDecimal offset) {
      return new OffsetLL_B12(offset.scaleByPowerOfTen(7).intValue());
   }

}

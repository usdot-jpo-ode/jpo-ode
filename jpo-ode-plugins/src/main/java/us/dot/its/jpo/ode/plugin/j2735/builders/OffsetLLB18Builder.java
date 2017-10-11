package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B18;

public class OffsetLLB18Builder {

   private OffsetLLB18Builder() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B18 offsetLLB18(BigDecimal offset) {
      return new OffsetLL_B18(offset.scaleByPowerOfTen(7).intValue());
   }

}

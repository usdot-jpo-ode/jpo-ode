package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B24;

public class OffsetLLB24Builder {

   private OffsetLLB24Builder() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B24 offsetLLB24(BigDecimal offset) {
      return new OffsetLL_B24(offset.scaleByPowerOfTen(7).intValue());
   }

}

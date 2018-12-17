package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetLLBuilder {

   private OffsetLLBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetLL(BigDecimal offset) {
      return offset.scaleByPowerOfTen(7).longValue();
   }
}

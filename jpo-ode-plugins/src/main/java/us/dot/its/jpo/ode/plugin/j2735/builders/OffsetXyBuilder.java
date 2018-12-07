package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetXyBuilder {

   private OffsetXyBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Long offsetXy(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).longValue();
   }
}

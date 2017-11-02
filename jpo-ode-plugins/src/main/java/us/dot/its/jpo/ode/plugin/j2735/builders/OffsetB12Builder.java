package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB12Builder {

   private OffsetB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetB12(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).intValue();
   }
}

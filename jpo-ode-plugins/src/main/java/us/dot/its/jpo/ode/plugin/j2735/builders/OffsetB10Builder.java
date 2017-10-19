package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB10Builder {

   private OffsetB10Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetB10(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).intValue();
   }
}

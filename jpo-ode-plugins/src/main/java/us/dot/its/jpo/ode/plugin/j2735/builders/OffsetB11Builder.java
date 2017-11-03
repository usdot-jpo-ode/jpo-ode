package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB11Builder {

   private OffsetB11Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetB11(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).intValue();
   }
}

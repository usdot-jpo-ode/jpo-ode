package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB14Builder {

   private OffsetB14Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetB14(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).intValue();
   }
}

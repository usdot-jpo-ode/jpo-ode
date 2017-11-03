package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class OffsetB13Builder {

   private OffsetB13Builder() {
      throw new UnsupportedOperationException();
   }

   public static int offsetB13(BigDecimal offset) {
      return offset.scaleByPowerOfTen(2).intValue();
   }
}

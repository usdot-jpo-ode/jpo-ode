package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B14;

public class OffsetB14Builder {

   private OffsetB14Builder() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B14 offsetB14(BigDecimal offset) {
      return new Offset_B14(offset.scaleByPowerOfTen(2).intValue());
   }
}

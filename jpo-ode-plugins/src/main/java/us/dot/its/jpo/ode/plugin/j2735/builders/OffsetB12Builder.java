package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;

public class OffsetB12Builder {

   private OffsetB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B12 offsetB12(BigDecimal offset) {
      return new Offset_B12(offset.scaleByPowerOfTen(2).intValue());
   }
}

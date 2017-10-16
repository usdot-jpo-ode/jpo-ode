package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;

public class OffsetB10Builder {

   private OffsetB10Builder() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B10 offsetB10(BigDecimal offset) {
      return new Offset_B10(offset.scaleByPowerOfTen(2).intValue());
   }
}

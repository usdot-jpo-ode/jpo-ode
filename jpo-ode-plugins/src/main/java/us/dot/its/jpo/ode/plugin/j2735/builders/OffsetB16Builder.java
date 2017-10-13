package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B16;

public class OffsetB16Builder {

   private OffsetB16Builder() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B16 offsetB16(BigDecimal offset) {
      return new Offset_B16(offset.scaleByPowerOfTen(2).intValue());
   }
}

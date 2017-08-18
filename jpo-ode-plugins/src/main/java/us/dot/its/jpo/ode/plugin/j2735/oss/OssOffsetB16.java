package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B16;

public class OssOffsetB16 {

   private OssOffsetB16() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B16 offsetB16(BigDecimal offset) {
      return new Offset_B16(offset.scaleByPowerOfTen(2).intValue());
   }
}

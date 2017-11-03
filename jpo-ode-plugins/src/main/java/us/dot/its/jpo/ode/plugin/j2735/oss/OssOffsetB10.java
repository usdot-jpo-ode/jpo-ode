package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;

public class OssOffsetB10 {

   private OssOffsetB10() {
      throw new UnsupportedOperationException();
   }

   public static Offset_B10 offsetB10(BigDecimal offset) {
      return new Offset_B10(offset.scaleByPowerOfTen(2).intValue());
   }
}

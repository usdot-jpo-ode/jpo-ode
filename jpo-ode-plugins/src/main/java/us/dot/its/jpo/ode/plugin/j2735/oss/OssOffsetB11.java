package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B11;

public class OssOffsetB11 {

   private OssOffsetB11() {
   }

   public static Offset_B11 offsetB11(BigDecimal offset) {
      return new Offset_B11(offset.scaleByPowerOfTen(2).intValue());
   }
}

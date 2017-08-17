package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;

public class OssOffsetB12 {

   private OssOffsetB12() {
   }

   public static Offset_B12 offsetB12(BigDecimal offset) {
      return new Offset_B12(offset.scaleByPowerOfTen(2).intValue());
   }
}

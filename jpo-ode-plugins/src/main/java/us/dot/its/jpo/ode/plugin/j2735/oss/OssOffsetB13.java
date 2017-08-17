package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B13;

public class OssOffsetB13 {

   private OssOffsetB13() {
   }

   public static Offset_B13 offsetB13(BigDecimal offset) {
      return new Offset_B13(offset.scaleByPowerOfTen(2).intValue());
   }
}

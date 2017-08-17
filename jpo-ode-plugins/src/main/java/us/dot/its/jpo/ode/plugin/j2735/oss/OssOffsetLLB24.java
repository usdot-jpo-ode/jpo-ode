package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B24;

public class OssOffsetLLB24 {

   private OssOffsetLLB24() {
   }

   public static OffsetLL_B24 offsetLLB24(BigDecimal offset) {
      return new OffsetLL_B24(offset.scaleByPowerOfTen(7).intValue());
   }

}

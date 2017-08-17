package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B12;

public class OssOffsetLLB12 {

   private OssOffsetLLB12() {
   }

   public static OffsetLL_B12 offsetLLB12(BigDecimal offset) {
      return new OffsetLL_B12(offset.scaleByPowerOfTen(7).intValue());
   }

}

package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B16;

public class OssOffsetLLB16 {

   private OssOffsetLLB16() {
      throw new UnsupportedOperationException();
   }

   public static OffsetLL_B16 offsetLLB16(BigDecimal offset) {
      return new OffsetLL_B16(offset.scaleByPowerOfTen(7).intValue());
   }

}

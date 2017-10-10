package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Scale_B12;

public class OssScaleB12 {

   private OssScaleB12() {
      throw new UnsupportedOperationException();
   }

   public static Scale_B12 scaleB12(BigDecimal scale) {
      return new Scale_B12(scale.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(20)).intValue());
   }
}

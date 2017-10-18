package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class ScaleB12Builder {

   private ScaleB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static int scaleB12(BigDecimal scale) {
      return scale.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(20)).intValue();
   }
}

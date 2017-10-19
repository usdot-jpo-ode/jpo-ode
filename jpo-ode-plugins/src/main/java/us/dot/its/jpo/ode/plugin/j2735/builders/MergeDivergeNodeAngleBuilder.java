package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class MergeDivergeNodeAngleBuilder {

   private MergeDivergeNodeAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int mergeDivergeNodeAngle(BigDecimal angle) {
      return angle.divide(BigDecimal.valueOf(1.5)).intValue();
   }
}

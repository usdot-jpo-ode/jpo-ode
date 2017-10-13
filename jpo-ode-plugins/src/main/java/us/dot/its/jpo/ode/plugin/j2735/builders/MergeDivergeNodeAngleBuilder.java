package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.MergeDivergeNodeAngle;

public class OssMergeDivergeNodeAngle {

   private OssMergeDivergeNodeAngle() {
      throw new UnsupportedOperationException();
   }

   public static MergeDivergeNodeAngle mergeDivergeNodeAngle(BigDecimal angle) {
      return new MergeDivergeNodeAngle(angle.divide(BigDecimal.valueOf(1.5)).intValue());
   }
}

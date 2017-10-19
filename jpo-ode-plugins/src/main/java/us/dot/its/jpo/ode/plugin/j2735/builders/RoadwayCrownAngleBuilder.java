package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class RoadwayCrownAngleBuilder {

   private RoadwayCrownAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int roadwayCrownAngle(BigDecimal angle) {

      BigDecimal min = BigDecimal.valueOf(-38.1);
      BigDecimal max = BigDecimal.valueOf(38.1);
      BigDecimal minZero = BigDecimal.valueOf(-0.15);
      BigDecimal maxZero = BigDecimal.valueOf(0.15);

      if (angle == null) {
         return 128;
      } else if (angle.compareTo(min) >= 0 && angle.compareTo(max) <= 0) {
         if (angle.compareTo(minZero) >= 0 && angle.compareTo(maxZero) <= 0) {

            return 0;
         } else {
            int returnAngle = angle.divide(BigDecimal.valueOf(0.3)).intValue();
            return returnAngle;
         }
      } else {
         throw new IllegalArgumentException("RoadwayCrownAngle is out of bounds");
      }
   }
}

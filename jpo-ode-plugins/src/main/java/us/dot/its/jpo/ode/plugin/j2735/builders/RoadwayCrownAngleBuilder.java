package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class RoadwayCrownAngleBuilder {

   private RoadwayCrownAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int roadwayCrownAngle(BigDecimal angle) {
      return angle.divide(BigDecimal.valueOf(0.3)).intValue();
   }

}

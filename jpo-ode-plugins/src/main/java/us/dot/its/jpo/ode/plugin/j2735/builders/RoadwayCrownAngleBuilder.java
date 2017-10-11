package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.RoadwayCrownAngle;

public class RoadwayCrownAngleBuilder {

   private RoadwayCrownAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static RoadwayCrownAngle roadwayCrownAngle(BigDecimal angle) {
      return new RoadwayCrownAngle(angle.divide(BigDecimal.valueOf(0.3)).intValue());
   }

}

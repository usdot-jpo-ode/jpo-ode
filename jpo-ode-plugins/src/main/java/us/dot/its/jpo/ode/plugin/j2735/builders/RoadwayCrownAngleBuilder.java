package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.RoadwayCrownAngle;

public class OssRoadwayCrownAngle {

   private OssRoadwayCrownAngle() {
      throw new UnsupportedOperationException();
   }

   public static RoadwayCrownAngle roadwayCrownAngle(BigDecimal angle) {
      return new RoadwayCrownAngle(angle.divide(BigDecimal.valueOf(0.3)).intValue());
   }

}

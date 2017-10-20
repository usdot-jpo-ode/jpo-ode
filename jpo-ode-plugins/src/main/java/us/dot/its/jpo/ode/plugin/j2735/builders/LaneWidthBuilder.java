package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class LaneWidthBuilder {

   private LaneWidthBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Long laneWidth(Long jlw) {
      return BigDecimal.valueOf(jlw).scaleByPowerOfTen(2).longValue();
   }

}

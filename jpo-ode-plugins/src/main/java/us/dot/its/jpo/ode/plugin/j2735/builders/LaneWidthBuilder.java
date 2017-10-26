package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class LaneWidthBuilder {

   private LaneWidthBuilder() {
      throw new UnsupportedOperationException();
   }

   public static BigDecimal laneWidth(BigDecimal jlw) {
      return jlw.scaleByPowerOfTen(2);
   }

}

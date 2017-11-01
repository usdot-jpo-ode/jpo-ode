package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class LaneWidthBuilder {

   private LaneWidthBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int laneWidth(BigDecimal jlw) {
      return (jlw.scaleByPowerOfTen(2).intValue());
   }

}
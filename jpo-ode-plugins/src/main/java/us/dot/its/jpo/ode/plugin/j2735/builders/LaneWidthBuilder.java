package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.LaneWidth;

public class LaneWidthBuilder {

   private LaneWidthBuilder() {
      throw new UnsupportedOperationException();
   }

   public static LaneWidth laneWidth(BigDecimal jlw) {
      return new LaneWidth(jlw.scaleByPowerOfTen(2).intValue());
   }

}

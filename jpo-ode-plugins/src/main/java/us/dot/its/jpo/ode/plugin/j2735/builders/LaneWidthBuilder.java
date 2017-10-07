package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.LaneWidth;

public class OssLaneWidth {

   private OssLaneWidth() {
      throw new UnsupportedOperationException();
   }

   public static LaneWidth laneWidth(BigDecimal jlw) {
      return new LaneWidth(jlw.scaleByPowerOfTen(2).intValue());
   }

}

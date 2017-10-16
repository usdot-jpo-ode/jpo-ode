package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.DrivenLineOffsetSm;

public class DrivenLineOffsetSmBuilder {

   private DrivenLineOffsetSmBuilder() {
      throw new UnsupportedOperationException();
   }

   public static DrivenLineOffsetSm drivenLaneOffsetSm(BigDecimal offset) {
      return new DrivenLineOffsetSm(offset.scaleByPowerOfTen(2).intValue());
   }
}

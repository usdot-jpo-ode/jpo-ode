package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.DrivenLineOffsetSm;

public class OssDrivenLineOffsetSm {

   private OssDrivenLineOffsetSm() {
      throw new UnsupportedOperationException();
   }

   public static DrivenLineOffsetSm drivenLaneOffsetSm(BigDecimal offset) {
      return new DrivenLineOffsetSm(offset.scaleByPowerOfTen(2).intValue());
   }
}

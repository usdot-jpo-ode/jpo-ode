package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.DrivenLineOffsetLg;

public class OssDrivenLineOffsetLg {

   private OssDrivenLineOffsetLg() {
      throw new UnsupportedOperationException();
   }

   public static DrivenLineOffsetLg drivenLineOffsetLg(BigDecimal offset) {
      return new DrivenLineOffsetLg(offset.scaleByPowerOfTen(2).intValue());
   }
}

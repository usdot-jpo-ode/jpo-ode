package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.DrivenLineOffsetLg;

public class DrivenLineOffsetLgBuilder {

   private DrivenLineOffsetLgBuilder() {
      throw new UnsupportedOperationException();
   }

   public static DrivenLineOffsetLg drivenLineOffsetLg(BigDecimal offset) {
      return new DrivenLineOffsetLg(offset.scaleByPowerOfTen(2).intValue());
   }
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class VelocityBuilder {

   private VelocityBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int velocity(BigDecimal vel) {
      return vel.multiply(BigDecimal.valueOf(50)).intValue();
   }
}

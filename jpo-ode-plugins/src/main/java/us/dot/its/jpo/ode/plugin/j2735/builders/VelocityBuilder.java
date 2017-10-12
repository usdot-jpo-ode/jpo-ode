package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class VelocityBuilder {

   private VelocityBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Velocity velocity(BigDecimal vel) {
      return new Velocity(vel.multiply(BigDecimal.valueOf(50)).intValue());
   }
}

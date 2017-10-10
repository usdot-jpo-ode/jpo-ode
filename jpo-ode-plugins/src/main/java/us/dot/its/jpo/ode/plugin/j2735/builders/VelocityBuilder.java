package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class OssVelocity {

   private OssVelocity() {
      throw new UnsupportedOperationException();
   }

   public static Velocity velocity(BigDecimal vel) {
      return new Velocity(vel.multiply(BigDecimal.valueOf(50)).intValue());
   }
}

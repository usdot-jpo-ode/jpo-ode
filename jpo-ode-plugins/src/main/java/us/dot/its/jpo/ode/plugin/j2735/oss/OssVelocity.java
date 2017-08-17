package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class OssVelocity {

   private OssVelocity() {
   }

   public static Velocity velocity(long vel) {
      return new Velocity(BigDecimal.valueOf(vel, -1).intValue());
   }

}

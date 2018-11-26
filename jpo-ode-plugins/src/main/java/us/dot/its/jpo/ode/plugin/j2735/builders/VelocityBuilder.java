package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

public class VelocityBuilder {

   private VelocityBuilder() {
      throw new UnsupportedOperationException();
   }

   /**
    * Converts velocity from human readable decimal to ASN-compliant int.
    * @param vel
    * @return
    */
   public static int velocity(BigDecimal vel) {

      int convertedValue = 8191;
      if (vel != null) {
         convertedValue = vel.multiply(BigDecimal.valueOf(50)).intValue();
      }

      return convertedValue;
   }
}

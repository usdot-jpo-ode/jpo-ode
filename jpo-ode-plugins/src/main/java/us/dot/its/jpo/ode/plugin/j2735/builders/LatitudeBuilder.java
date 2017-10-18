package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class LatitudeBuilder {

   private LatitudeBuilder() {
      throw new UnsupportedOperationException();
   }
   
   public static int latitude(BigDecimal lat) {
      return lat.scaleByPowerOfTen(7).intValue();
   }

   public static BigDecimal genericLatitude(JsonNode latitude) {
      BigDecimal returnValue = null;

      if ((latitude != null) && (latitude.asLong() != 900000001)) {
         returnValue = BigDecimal.valueOf(latitude.asLong(), 7);

      }
      return returnValue;
   }
}

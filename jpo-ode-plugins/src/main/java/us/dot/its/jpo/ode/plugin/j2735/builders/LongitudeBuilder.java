package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class LongitudeBuilder {

   private LongitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int longitude(BigDecimal lon) {
      return lon.scaleByPowerOfTen(7).intValue();
   }

   public static BigDecimal genericLongitude(JsonNode longitude) {
      BigDecimal returnValue = null;

      if ((longitude != null) && (longitude.asLong() != 1800000001)) {
         returnValue = BigDecimal.valueOf(longitude.asLong(), 7);

      }
      return returnValue;
   }
}

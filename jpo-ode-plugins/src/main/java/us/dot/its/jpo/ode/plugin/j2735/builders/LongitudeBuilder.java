package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.dsrc.Longitude;

public class LongitudeBuilder {

   private LongitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Longitude longitude(BigDecimal lon) {
      return new Longitude(lon.scaleByPowerOfTen(7).intValue());
   }

   public static BigDecimal genericLongitude(JsonNode longitude) {
      BigDecimal returnValue = null;

      if ((longitude != null) && (longitude.longValue() != 1800000001)) {
         returnValue = BigDecimal.valueOf(longitude.longValue(), 7);

      }
      return returnValue;
   }
}

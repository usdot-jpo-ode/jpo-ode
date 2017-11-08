package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class LongitudeBuilder {

   private LongitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static long j2735Longitude(BigDecimal longitude) {
      return longitude.scaleByPowerOfTen(7).intValue();
   }

   public static long j2735Longitude(JsonNode longitude) {
      return j2735Longitude(longitude.decimalValue());
   }

   public static BigDecimal genericLongitude(JsonNode longitude) {
      return genericLongitude(longitude.asInt());
   }
   
   public static BigDecimal genericLongitude(long longitude) {
      BigDecimal returnValue = null;

      if (longitude != 1800000001) {
         returnValue = BigDecimal.valueOf(longitude, 7);

      }
      return returnValue;
   }
}

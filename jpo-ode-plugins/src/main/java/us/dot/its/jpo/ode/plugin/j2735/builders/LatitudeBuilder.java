package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class LatitudeBuilder {

   private LatitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static long j2735Latitude(BigDecimal latitude) {
      return latitude.scaleByPowerOfTen(7).intValue();
   }

   public static long j2735Latitude(JsonNode latitude) {
      return j2735Latitude(latitude.decimalValue());
   }

   public static BigDecimal genericLatitude(JsonNode latitude) {
      return genericLatitude(latitude.asInt());
   }
   
   public static BigDecimal genericLatitude(long latitude) {
      BigDecimal returnValue = null;

      if (latitude != 900000001) {
         returnValue = BigDecimal.valueOf(latitude, 7);
      }
      return returnValue;
   }
}

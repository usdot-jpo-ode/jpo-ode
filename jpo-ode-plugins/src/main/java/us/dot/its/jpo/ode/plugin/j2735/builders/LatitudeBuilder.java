package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class LatitudeBuilder {

   private LatitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static BigDecimal genericLatitude(JsonNode latitude) {
      BigDecimal returnValue = null;

      if ((latitude != null) && (latitude.longValue() != 900000001)) {
         returnValue = BigDecimal.valueOf(latitude.longValue(), 7);

      }
      return returnValue;
   }
}

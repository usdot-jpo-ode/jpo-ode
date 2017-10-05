package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.dsrc.Latitude;

public class LatitudeBuilder {

   private LatitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static Latitude latitude(BigDecimal lat) {
      return new Latitude(lat.scaleByPowerOfTen(7).intValue());
   }

   public static BigDecimal genericLatitude(JsonNode latitude) {
      BigDecimal returnValue = null;

      if ((latitude != null) && (latitude.longValue() != 900000001)) {
         returnValue = BigDecimal.valueOf(latitude.longValue(), 7);

      }
      return returnValue;
   }
}

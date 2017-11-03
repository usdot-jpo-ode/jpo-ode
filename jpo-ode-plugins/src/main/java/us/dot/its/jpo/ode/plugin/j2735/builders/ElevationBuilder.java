package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class ElevationBuilder {

   private ElevationBuilder() {
      throw new UnsupportedOperationException();
   }

   public static BigDecimal genericElevation(JsonNode elevation) {
      BigDecimal returnValue = null;

      if ((elevation.asLong() != -4096) && (elevation.asLong() < 61439) && (elevation.asLong() > -4095)) {
         returnValue = BigDecimal.valueOf(elevation.asLong(), 1);
      }
      else if (elevation.asLong() >= 61439) {
         returnValue = BigDecimal.valueOf((long)61439, 1);
      }
      else if ((elevation.asLong() <= -4095) && (elevation.asLong() != -4096)) {
         returnValue = BigDecimal.valueOf((long)-4095, 1);
      }
     

      
      
      return returnValue;
   }
}

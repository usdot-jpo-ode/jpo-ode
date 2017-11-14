package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class ElevationBuilder {

   private ElevationBuilder() {
      throw new UnsupportedOperationException();
   }

   public static long j2735Elevation(BigDecimal elevation) {
      return elevation.scaleByPowerOfTen(1).intValue();
   }

   public static long j2735Elevation(JsonNode elevation) {
      return j2735Elevation(elevation.decimalValue());
   }

   public static BigDecimal genericElevation(JsonNode elevation) {
      return genericElevation(elevation.asInt());
   }
   
   public static BigDecimal genericElevation(long elevation) {
      BigDecimal returnValue = null;

      if ((elevation != -4096) && (elevation < 61439) && (elevation > -4095)) {
         returnValue = BigDecimal.valueOf(elevation, 1);
      }
      else if (elevation >= 61439) {
         returnValue = BigDecimal.valueOf((long)61439, 1);
      }
      else if ((elevation <= -4095) && (elevation != -4096)) {
         returnValue = BigDecimal.valueOf((long)-4095, 1);
      }
      return returnValue;
   }
   
   
}

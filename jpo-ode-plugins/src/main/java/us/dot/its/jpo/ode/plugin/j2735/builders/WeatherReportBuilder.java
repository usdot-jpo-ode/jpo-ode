package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipSituation;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipYesNo;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherReport;

public class WeatherReportBuilder {

   private static final int RAIN_RATE_LOWER_BOUND = 0;
   private static final int RAIN_RATE_UPPER_BOUND = 65535;
   private static final int RAIN_RATE_UNDEFINED_VALUE = 65535;
   private static final int SOLAR_RAD_LOWER_BOUND = 0;
   private static final int SOLAR_RAD_UPPER_BOUND = 65535;
   private static final int SOLAR_RAD_UNDEFINED_VALUE = 65535;
   private static final int FRICTION_LOWER_BOUND = 0;
   private static final int FRICTION_UPPER_BOUND = 101;
   private static final int FRICTION_UNDEFINED_VALUE = 101;
   private static final int ROAD_FRICTION_LOWER_BOUND = 0;
   private static final int ROAD_FRICTION_UPPER_BOUND = 50;

   private WeatherReportBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735WeatherReport genericWeatherReport(JsonNode weatherReport) {
      J2735WeatherReport gwr = new J2735WeatherReport();

      // Required element
      gwr.setIsRaining(J2735EssPrecipYesNo.values()[weatherReport.get("isRaining").asInt()]);

      // Optional elements
      if (weatherReport.get("friction") != null) {

         int friction = weatherReport.get("friction").asInt();

         if (friction < FRICTION_LOWER_BOUND || FRICTION_UPPER_BOUND < friction) {
            throw new IllegalArgumentException(
                  String.format("Friction value out of bounds [%d..%d]", FRICTION_LOWER_BOUND, FRICTION_UPPER_BOUND));
         }

         if (friction != FRICTION_UNDEFINED_VALUE) {
            gwr.setFriction(friction);
         }
      }

      if (weatherReport.get("precipSituation") != null) {
         gwr.setPrecipSituation(J2735EssPrecipSituation.values()[weatherReport.get("precipSituation").asInt()]);
      }

      if (weatherReport.get("rainRate") != null) {

         int rainRate = weatherReport.get("rainRate").asInt();

         if (rainRate < RAIN_RATE_LOWER_BOUND || RAIN_RATE_UPPER_BOUND < rainRate) {
            throw new IllegalArgumentException(
                  String.format("Rain rate out of bounds [%d..%d]", RAIN_RATE_LOWER_BOUND, RAIN_RATE_UPPER_BOUND));
         }

         if (rainRate != RAIN_RATE_UNDEFINED_VALUE) {
            gwr.setRainRate(BigDecimal.valueOf(rainRate, 1));
         }
      }

      // coefficient of friction has minimum and undefined value 0
      if (weatherReport.get("roadFriction") != null) {

         int roadFriction = weatherReport.get("roadFriction").asInt();

         if (roadFriction < ROAD_FRICTION_LOWER_BOUND || ROAD_FRICTION_UPPER_BOUND < roadFriction) {
            throw new IllegalArgumentException("Road friction value out of bounds [0..50]");
         }

         gwr.setRoadFriction(BigDecimal.valueOf(roadFriction * 2L, 2));
      }

      if (weatherReport.get("solarRadiation") != null) {

         int solarRadiation = weatherReport.get("solarRadiation").asInt();

         if (solarRadiation < SOLAR_RAD_LOWER_BOUND || SOLAR_RAD_UPPER_BOUND < solarRadiation) {
            throw new IllegalArgumentException(String.format("Solar radiation value out of bounds [%d..%d]",
                  SOLAR_RAD_LOWER_BOUND, SOLAR_RAD_UPPER_BOUND));
         }

         if (solarRadiation != SOLAR_RAD_UNDEFINED_VALUE) {
            gwr.setSolarRadiation(solarRadiation);
         }
      }

      return gwr;
   }

}

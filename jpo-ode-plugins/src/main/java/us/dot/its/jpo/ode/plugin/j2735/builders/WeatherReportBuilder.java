/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
      J2735EssPrecipYesNo enumIsRaining;
      try {
         enumIsRaining = J2735EssPrecipYesNo.valueOf(weatherReport.get("isRaining").fieldNames().next().toUpperCase());
      } catch (IllegalArgumentException e) {
         enumIsRaining = J2735EssPrecipYesNo.NA;
      }
      gwr.setIsRaining(enumIsRaining);

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
         J2735EssPrecipSituation enumPrecipSituation;
         try {
            enumPrecipSituation = J2735EssPrecipSituation.valueOf(weatherReport.get("precipSituation").fieldNames().next().toUpperCase());
         } catch (IllegalArgumentException e) {
            enumPrecipSituation = J2735EssPrecipSituation.UNKNOWN;
         }
         gwr.setPrecipSituation(enumPrecipSituation);
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

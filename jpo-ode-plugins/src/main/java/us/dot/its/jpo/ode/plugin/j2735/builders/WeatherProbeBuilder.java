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

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherProbe;

public class WeatherProbeBuilder {

   private static final Integer TEMP_LOWER_BOUND = 0;
   private static final Integer TEMP_UPPER_BOUND = 191;
   private static final Integer PRESSURE_LOWER_BOUND = 0;
   private static final Integer PRESSURE_UPPER_BOUND = 255;

   private WeatherProbeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735WeatherProbe genericWeatherProbe(JsonNode weatherProbe) {

      J2735WeatherProbe gwp = new J2735WeatherProbe();

      if (weatherProbe.get("airTemp") != null) {
         int airTemp = weatherProbe.get("airTemp").asInt();

         if (airTemp < TEMP_LOWER_BOUND || airTemp > TEMP_UPPER_BOUND) {
            throw new IllegalArgumentException(
                  String.format("Ambient air temperature out of bounds [%d..%d]", TEMP_LOWER_BOUND, TEMP_UPPER_BOUND));
         }

         if (airTemp != 191) {
            gwp.setAirTemp(airTemp - 40);
         }
      }

      if (weatherProbe.get("airPressure") != null) {
         int airPressure = weatherProbe.get("airPressure").asInt();

         if (airPressure < PRESSURE_LOWER_BOUND || airPressure > PRESSURE_UPPER_BOUND) {
            throw new IllegalArgumentException(
                  String.format("Ambient air pressure out of bounds [%d..%d]", PRESSURE_LOWER_BOUND, PRESSURE_UPPER_BOUND));
         }

         if (airPressure != 0) {
            gwp.setAirPressure(((airPressure - 1) * 2) + 580);
         }

      }

      if (weatherProbe.get("rainRates") != null) {
         gwp.setRainRates(WiperSetBuilder.genericWiperSet(weatherProbe.get("rainRates")));
      }

      return gwp;
   }

}

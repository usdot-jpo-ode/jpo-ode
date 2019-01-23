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

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class VehicleSafetyExtensionsBuilder {

   private VehicleSafetyExtensionsBuilder() {
      throw new UnsupportedOperationException();
   }

   public static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, JsonNode vehSafetyExt) {
      J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
      part2Content.setValue(vehSafety);

      JsonNode events = vehSafetyExt.get("events");
      if (events != null) {
         vehSafety.setEvents(
               BitStringBuilder.genericBitString(events, BsmPart2ContentBuilder.VehicleEventFlagsNames.values()));
      }

      JsonNode lights = vehSafetyExt.get("lights");
      if (lights != null) {
         vehSafety.setLights(
               BitStringBuilder.genericBitString(lights, BsmPart2ContentBuilder.ExteriorLightsNames.values()));
      }

      JsonNode pathHistory = vehSafetyExt.get("pathHistory");
      if (pathHistory != null) {
         vehSafety.setPathHistory(PathHistoryBuilder.genericPathHistory(pathHistory));
      }
      JsonNode pathPrediction = vehSafetyExt.get("pathPrediction");
      if (pathPrediction != null) {
         vehSafety.setPathPrediction(PathPredictionBuilder.genericPathPrediction(pathPrediction));
      }

   }

}

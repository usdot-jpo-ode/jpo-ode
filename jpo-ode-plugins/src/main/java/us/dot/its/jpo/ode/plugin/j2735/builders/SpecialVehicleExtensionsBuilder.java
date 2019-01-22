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
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;

public class SpecialVehicleExtensionsBuilder {

   private SpecialVehicleExtensionsBuilder() {
      throw new UnsupportedOperationException();
   }

   public static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, JsonNode specVehExt) {
      J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
      part2Content.setValue(specVeh);

      JsonNode va = specVehExt.get("vehicleAlerts");
      if (va != null) {
         specVeh.setVehicleAlerts(EmergencyDetailsBuilder.genericEmergencyDetails(va));
      }
      JsonNode desc = specVehExt.get("description");
      if (desc != null) {
         specVeh.setDescription(EventDescriptionBuilder.genericEventDescription(desc));
      }
      JsonNode tr = specVehExt.get("trailers");
      if (tr != null) {
         specVeh.setTrailers(TrailerDataBuilder.genericTrailerData(tr));
      }
   }

}

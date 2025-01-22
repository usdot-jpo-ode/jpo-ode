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
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

public class SupplementalVehicleExtensionsBuilder {
    
    private SupplementalVehicleExtensionsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735SupplementalVehicleExtensions evaluateSupplementalVehicleExtensions(J2735BsmPart2Content part2Content,
            JsonNode sve) {
        J2735SupplementalVehicleExtensions genericSVE = new J2735SupplementalVehicleExtensions();
        part2Content.setValue(genericSVE);

        // All elements of this class are optional
        if (sve.has("classification")) {
            genericSVE.setClassification(sve.get("classification").asInt());
        }
        if (sve.has("classDetails")) {
            genericSVE.setClassDetails(VehicleClassificationBuilder.genericVehicleClassification(sve.get("classDetails")));
        }
        if (sve.has("vehicleData")) {
            genericSVE.setVehicleData(VehicleDataBuilder.genericVehicleData(sve.get("vehicleData")));
        }
        if (sve.has("doNotUse1")) {
            genericSVE.setDoNotUse1(WeatherReportBuilder.genericWeatherReport(sve.get("doNotUse1")));
        }
        if (sve.has("doNotUse2")) {
            genericSVE.setDoNotUse2(WeatherProbeBuilder.genericWeatherProbe(sve.get("doNotUse2")));
        }
        if (sve.has("doNotUse3")) {
            genericSVE.setDoNotUse3(ObstacleDetectionBuilder.genericObstacleDetection(sve.get("doNotUse3")));
        }
        if (sve.has("status")) {
            genericSVE.setStatus(DisabledVehicleBuilder.genericDisabledVehicle(sve.get("status")));
        }
        if (sve.has("doNotUse4")) {
            genericSVE.setDoNotUse4(SpeedProfileBuilder.genericSpeedProfile(sve.get("doNotUse4")));
        }
        if (sve.has("doNotUse5")) {
            genericSVE.setDoNotUse5(RTCMPackageBuilder.genericRTCMPackage(sve.get("doNotUse5")));
        }

        return genericSVE;
    }

}

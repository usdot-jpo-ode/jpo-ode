package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.util.CodecUtils;

public class SupplementalVehicleExtensionsBuilder {
    
    private SupplementalVehicleExtensionsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735SupplementalVehicleExtensions genericSupplementalVehicleExtensions(
            JsonNode sve) {
        J2735SupplementalVehicleExtensions genericSVE = new J2735SupplementalVehicleExtensions();

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
        if (sve.has("weatherReport")) {
            genericSVE.setWeatherReport(WeatherReportBuilder.genericWeatherReport(sve.get("weatherReport")));
        }
        if (sve.has("weatherProbe")) {
            genericSVE.setWeatherProbe(WeatherProbeBuilder.genericWeatherProbe(sve.get("weatherProbe")));
        }
        if (sve.has("obstacle")) {
            genericSVE.setObstacle(ObstacleDetectionBuilder.genericObstacleDetection(sve.get("obstacle")));
        }
        if (sve.has("status")) {
            genericSVE.setStatus(DisabledVehicleBuilder.genericDisabledVehicle(sve.get("status")));
        }
        if (sve.has("speedProfile")) {
            genericSVE.setSpeedProfile(SpeedProfileBuilder.genericSpeedProfile(sve.get("speedProfile")));
        }
        if (sve.has("theRTCM")) {
            genericSVE.setTheRTCM(RTCMPackageBuilder.genericRTCMPackage(sve.get("theRTCM")));
        }
        if (sve.has("regional")) {
            JsonNode regional = sve.get("regional");
            Iterator<JsonNode> elements = regional.elements();
            while (elements.hasNext()) {
               JsonNode element = elements.next();
               genericSVE.getRegional().add(new J2735RegionalContent().setId(
                  element.get("regionId").asInt())
                        .setValue(CodecUtils.fromHex(element.get("regExtValue").asText().trim().replaceAll("\\w", ""))));
            }
        }
        return genericSVE;
    }

}

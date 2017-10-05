package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

public class SupplementalVehicleExtensionsBuilder {
    
    private SupplementalVehicleExtensionsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735SupplementalVehicleExtensions genericSupplementalVehicleExtensions(
            JsonNode sve) {
        J2735SupplementalVehicleExtensions suppVeh = new J2735SupplementalVehicleExtensions();

        // All elements of this class are optional
        if (sve.hasClassification()) {
            suppVeh.setClassification(sve.classification.intValue());
        }
        if (sve.hasClassDetails()) {
            suppVeh.setClassDetails(VehicleClassificationBuilder.genericVehicleClassification(sve.classDetails));
        }
        if (sve.hasVehicleData()) {
            suppVeh.setVehicleData(VehicleDataBuilder.genericVehicleData(sve.vehicleData));
        }
        if (sve.hasWeatherReport()) {
            suppVeh.setWeatherReport(WeatherReportBuilder.genericWeatherReport(sve.weatherReport));
        }
        if (sve.hasWeatherProbe()) {
            suppVeh.setWeatherProbe(WeatherProbeBuilder.genericWeatherProbe(sve.weatherProbe));
        }
        if (sve.hasObstacle()) {
            suppVeh.setObstacle(ObstacleDetectionBuilder.genericObstacleDetection(sve.obstacle));
        }
        if (sve.hasStatus()) {
            suppVeh.setStatus(DisabledVehicleBuilder.genericDisabledVehicle(sve.status));
        }
        if (sve.hasSpeedProfile()) {
            suppVeh.setSpeedProfile(SpeedProfileBuilder.genericSpeedProfile(sve.speedProfile));
        }
        if (sve.hasTheRTCM()) {
            suppVeh.setTheRTCM(RTCMPackageBuilder.genericRTCMPackage(sve.theRTCM));
        }
        if (sve.hasRegional()) {
            while (sve.regional.elements().hasMoreElements()) {
                us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_ element = (us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_) sve.regional
                        .elements().nextElement();
                suppVeh.getRegional().add(new J2735RegionalContent().setId(element.regionId.intValue())
                        .setValue(element.regExtValue.getEncodedValue()));
            }
        }
        return suppVeh;
    }

}

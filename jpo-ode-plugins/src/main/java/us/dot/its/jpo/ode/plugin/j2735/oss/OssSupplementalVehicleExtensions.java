package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

public class OssSupplementalVehicleExtensions {
    
    private OssSupplementalVehicleExtensions() {}

    public static J2735SupplementalVehicleExtensions genericSupplementalVehicleExtensions(
            SupplementalVehicleExtensions sve) {
        J2735SupplementalVehicleExtensions suppVeh = new J2735SupplementalVehicleExtensions();

        // All elements of this class are optional
        if (sve.hasClassification()) {
            suppVeh.setClassification(sve.classification.intValue());
        }
        if (sve.hasClassDetails()) {
            suppVeh.setClassDetails(OssVehicleClassification.genericVehicleClassification(sve.classDetails));
        }
        if (sve.hasVehicleData()) {
            suppVeh.setVehicleData(OssVehicleData.genericVehicleData(sve.vehicleData));
        }
        if (sve.hasWeatherReport()) {
            suppVeh.setWeatherReport(OssWeatherReport.genericWeatherReport(sve.weatherReport));
        }
        if (sve.hasWeatherProbe()) {
            suppVeh.setWeatherProbe(OssWeatherProbe.genericWeatherProbe(sve.weatherProbe));
        }
        if (sve.hasObstacle()) {
            suppVeh.setObstacle(OssObstacleDetection.genericObstacleDetection(sve.obstacle));
        }
        if (sve.hasStatus()) {
            suppVeh.setStatus(OssDisabledVehicle.genericDisabledVehicle(sve.status));
        }
        if (sve.hasSpeedProfile()) {
            suppVeh.setSpeedProfile(OssSpeedProfile.genericSpeedProfile(sve.speedProfile));
        }
        if (sve.hasTheRTCM()) {
            suppVeh.setTheRTCM(OssRTCMPackage.genericRTCMPackage(sve.theRTCM));
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

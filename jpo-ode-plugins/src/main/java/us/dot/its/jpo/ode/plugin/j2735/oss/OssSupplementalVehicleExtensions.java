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
            suppVeh.classification = sve.classification.intValue();
        }
        if (sve.hasClassDetails()) {
            suppVeh.classDetails = OssVehicleClassification.genericVehicleClassification(sve.classDetails);
        }
        if (sve.hasVehicleData()) {
            suppVeh.vehicleData = OssVehicleData.genericVehicleData(sve.vehicleData);
        }
        if (sve.hasWeatherReport()) {
            suppVeh.weatherReport = OssWeatherReport.genericWeatherReport(sve.weatherReport);
        }
        if (sve.hasWeatherProbe()) {
            suppVeh.weatherProbe = OssWeatherProbe.genericWeatherProbe(sve.weatherProbe);
        }
        if (sve.hasObstacle()) {
            suppVeh.obstacle = OssObstacleDetection.genericObstacleDetection(sve.obstacle);
        }
        if (sve.hasStatus()) {
            suppVeh.status = OssDisabledVehicle.genericDisabledVehicle(sve.status);
        }
        if (sve.hasSpeedProfile()) {
            suppVeh.speedProfile = OssSpeedProfile.genericSpeedProfile(sve.speedProfile);
        }
        if (sve.hasTheRTCM()) {
            suppVeh.theRTCM = OssRTCMPackage.genericRTCMPackage(sve.theRTCM);
        }
        if (sve.hasRegional()) {
            while (sve.regional.elements().hasMoreElements()) {
                us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_ element = (us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_) sve.regional
                        .elements().nextElement();
                suppVeh.regional.add(new J2735RegionalContent().setId(element.regionId.intValue())
                        .setValue(element.regExtValue.getEncodedValue()));
            }
        }
        return suppVeh;
    }

}

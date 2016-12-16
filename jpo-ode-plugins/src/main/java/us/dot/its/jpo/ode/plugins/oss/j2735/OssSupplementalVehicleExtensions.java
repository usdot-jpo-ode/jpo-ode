package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

public class OssSupplementalVehicleExtensions {

	public static J2735SupplementalVehicleExtensions genericSupplementalVehicleExtensions(
			SupplementalVehicleExtensions sve) {
		J2735SupplementalVehicleExtensions suppVeh = new J2735SupplementalVehicleExtensions();
		
		suppVeh.classification = sve.classification.intValue();
		suppVeh.classDetails = OssVehicleClassification.genericVehicleClassification(sve.classDetails);
		suppVeh.vehicleData = OssVehicleData.genericVehicleData(sve.vehicleData);
		suppVeh.weatherReport = OssWeatherReport.genericWeatherReport(sve.weatherReport);
		suppVeh.weatherProbe = OssWeatherProbe.genericWeatherProbe(sve.weatherProbe);
		suppVeh.obstacle = OssObstacleDetection.genericObstacleDetection(sve.obstacle);
		suppVeh.status = OssDisabledVehicle.genericDisabledVehicle(sve.status);
		suppVeh.speedProfile = OssSpeedProfile.genericSpeedProfile(sve.speedProfile);
		suppVeh.theRTCM = OssRTCMPackage.genericRTCMPackage(sve.theRTCM);
		
		while (sve.regional.elements().hasMoreElements()) {
			us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_ element = 
					(us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions.Regional.Sequence_) sve
					.regional.elements().nextElement();
			suppVeh.regional.add(new J2735RegionalContent()
					.setId(element.regionId.intValue())
					.setValue(element.regExtValue.getEncodedValue())
					);
		}
		return suppVeh;
	}

}

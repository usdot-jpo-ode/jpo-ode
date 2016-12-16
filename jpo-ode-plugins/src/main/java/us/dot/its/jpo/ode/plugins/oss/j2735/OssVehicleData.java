package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;

public class OssVehicleData {

	public static J2735VehicleData genericVehicleData(VehicleData vehicleData) {
		J2735VehicleData vd = new J2735VehicleData();

		vd.bumpers = OssBumperHeights.genericBumperHeights(vehicleData.bumpers);
		vd.height = OssHeight.genericHeight(vehicleData.height);
		vd.mass = OssMassOrWeight.genericMass(vehicleData.mass);
		vd.trailerWeight = OssMassOrWeight.genericWeight(vehicleData.trailerWeight);
		
		return vd ;
	}

}

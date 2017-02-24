package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;

public class OssVehicleData {
    
    private OssVehicleData() {}

    public static J2735VehicleData genericVehicleData(VehicleData vehicleData) {
        J2735VehicleData vd = new J2735VehicleData();

        if (vehicleData.bumpers != null) {
            vd.bumpers = OssBumperHeights.genericBumperHeights(vehicleData.bumpers);
        }
        if (vehicleData.height != null) {
            vd.height = OssHeight.genericHeight(vehicleData.height);
        }
        if (vehicleData.mass != null) {
            vd.mass = OssMassOrWeight.genericMass(vehicleData.mass);
        }
        if (vehicleData.trailerWeight != null) {
            vd.trailerWeight = OssMassOrWeight.genericWeight(vehicleData.trailerWeight);
        }

        return vd;
    }

}

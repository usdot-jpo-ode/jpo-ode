package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;

public class OssVehicleData {
    
    private OssVehicleData() {
       throw new UnsupportedOperationException();
    }

    public static J2735VehicleData genericVehicleData(VehicleData vehicleData) {
        J2735VehicleData vd = new J2735VehicleData();

        if (vehicleData.bumpers != null) {
            vd.setBumpers(OssBumperHeights.genericBumperHeights(vehicleData.bumpers));
        }
        if (vehicleData.height != null) {
            vd.setHeight(OssHeight.genericHeight(vehicleData.height));
        }
        if (vehicleData.mass != null) {
            vd.setMass(OssMassOrWeight.genericMass(vehicleData.mass));
        }
        if (vehicleData.trailerWeight != null) {
            vd.setTrailerWeight(OssMassOrWeight.genericWeight(vehicleData.trailerWeight));
        }

        return vd;
    }

}

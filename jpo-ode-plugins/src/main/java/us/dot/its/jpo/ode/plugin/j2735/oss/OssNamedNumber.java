package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.j2735.itis.IncidentResponseEquipment;
import us.dot.its.jpo.ode.j2735.itis.VehicleGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735NamedNumber;

public class OssNamedNumber {

    private OssNamedNumber() {
       throw new UnsupportedOperationException();
    }

    public static J2735NamedNumber genericGenericLocations(GenericLocations locationDetails) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(locationDetails.name());
        gnn.setValue(locationDetails.longValue());
        return gnn;
    }

    public static J2735NamedNumber genericIncidentResponseEquipment(IncidentResponseEquipment responseEquip) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(responseEquip.name());
        gnn.setValue(responseEquip.longValue());
        return gnn;
    }

    public static J2735NamedNumber genericVehicleGroupAffected(VehicleGroupAffected vehicleType) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(vehicleType.name());
        gnn.setValue(vehicleType.longValue());
        return gnn;
    }

}

package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.j2735.itis.IncidentResponseEquipment;
import us.dot.its.jpo.ode.j2735.itis.VehicleGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735NamedNumber;

public class OssNamedNumber {

	public static J2735NamedNumber genericGenericLocations(GenericLocations locationDetails) {
		J2735NamedNumber gnn = new J2735NamedNumber();
		
		gnn.name = locationDetails.name();
		gnn.value = locationDetails.longValue();
		return gnn ;
	}

	public static J2735NamedNumber genericIncidentResponseEquipment(IncidentResponseEquipment responseEquip) {
		J2735NamedNumber gnn = new J2735NamedNumber();
		
		gnn.name = responseEquip.name();
		gnn.value = responseEquip.longValue();
		return gnn ;
	}

	public static J2735NamedNumber genericVehicleGroupAffected(VehicleGroupAffected vehicleType) {
		J2735NamedNumber gnn = new J2735NamedNumber();
		
		gnn.name = vehicleType.name();
		gnn.value = vehicleType.longValue();
		return gnn ;
	}

}

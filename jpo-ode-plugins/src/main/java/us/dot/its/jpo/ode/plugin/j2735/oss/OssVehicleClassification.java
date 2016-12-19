package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;
import us.dot.its.jpo.ode.plugin.j2735.J2735FuelType;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponderGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleType;

public class OssVehicleClassification {

	public static J2735VehicleClassification genericVehicleClassification(VehicleClassification vc) {
		J2735VehicleClassification gvc = new J2735VehicleClassification();
		
		gvc.fuelType = J2735FuelType.values()[vc.fuelType.intValue()];
		gvc.hpmsType = J2735VehicleType.values()[vc.hpmsType.indexOf()];
		gvc.iso3883 = vc.iso3883.intValue();
		gvc.keyType = vc.keyType.intValue();
		gvc.responderType = J2735ResponderGroupAffected.values()[vc.responderType.indexOf()];
		gvc.responseEquip = OssNamedNumber.genericIncidentResponseEquipment(vc.responseEquip);
		gvc.role = J2735BasicVehicleRole.values()[vc.role.indexOf()];
		gvc.vehicleType = OssNamedNumber.genericVehicleGroupAffected(vc.vehicleType);
		
		while (vc.regional.elements().hasMoreElements()) {
			us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_ element = 
					(us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_) vc
					.regional.elements().nextElement();
			gvc.regional.add(new J2735RegionalContent()
					.setId(element.regionId.intValue())
					.setValue(element.regExtValue.getEncodedValue())
					);
		}

		return gvc ;
	}

}

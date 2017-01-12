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
		
		// All elements of this class are optional
		if (vc.hasFuelType()) {
		    
		    if (vc.fuelType.intValue() < 0 || vc.fuelType.intValue() > 9) {
		        throw new IllegalArgumentException("Fuel type value out of bounds [0..9]");
		    }
		    
		    gvc.fuelType = J2735FuelType.values()[vc.fuelType.intValue()];
		}
		if (vc.hasHpmsType()) {
		    gvc.hpmsType = J2735VehicleType.values()[vc.hpmsType.indexOf()];
		}
		if (vc.hasIso3883()) {
		    gvc.iso3883 = vc.iso3883.intValue();
		}
		if (vc.hasKeyType()) {
		    
		    if (vc.keyType.intValue() < 0 || vc.keyType.intValue() > 255) {
		        throw new IllegalArgumentException("Basic vehicle classification out of bounds [0..255]");
		    }
		    
		    gvc.keyType = vc.keyType.intValue();
		}
		if (vc.hasResponderType()) {
		    gvc.responderType = J2735ResponderGroupAffected.values()[vc.responderType.indexOf()];
		}
		if (vc.hasResponseEquip()) {
		    gvc.responseEquip = OssNamedNumber.genericIncidentResponseEquipment(vc.responseEquip);
		}
		if (vc.hasRole()) {
		    gvc.role = J2735BasicVehicleRole.values()[vc.role.indexOf()];
		}
		if (vc.hasVehicleType()) {
		    gvc.vehicleType = OssNamedNumber.genericVehicleGroupAffected(vc.vehicleType);
		}
		if (vc.hasRegional()) {
   		while (vc.regional.elements().hasMoreElements()) {
   			us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_ element = 
   					(us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_) vc
   					.regional.elements().nextElement();
   			gvc.regional.add(new J2735RegionalContent()
   					.setId(element.regionId.intValue())
   					.setValue(element.regExtValue.getEncodedValue())
   					);
   		}
		}

		return gvc ;
	}

}

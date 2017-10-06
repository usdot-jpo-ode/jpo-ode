package us.dot.its.jpo.ode.plugin.j2735.builders;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;
import us.dot.its.jpo.ode.plugin.j2735.J2735FuelType;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponderGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleType;

public class VehicleClassificationBuilder {

    private VehicleClassificationBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735VehicleClassification genericVehicleClassification(VehicleClassification vc) {
        J2735VehicleClassification gvc = new J2735VehicleClassification();

        // All elements of this class are optional
        if (vc.hasFuelType()) {

            if (vc.fuelType.asInt() < 0 || vc.fuelType.asInt() > 9) {
                throw new IllegalArgumentException("Fuel type value out of bounds [0..9]");
            }

            gvc.setFuelType(J2735FuelType.values()[vc.fuelType.asInt()]);
        }
        if (vc.hasHpmsType()) {
            gvc.setHpmsType(J2735VehicleType.values()[vc.hpmsType.indexOf()]);
        }
        if (vc.hasIso3883()) {
            gvc.setIso3883(vc.iso3883.asInt());
        }
        if (vc.hasKeyType()) {

            if (vc.keyType.asInt() < 0 || vc.keyType.asInt() > 255) {
                throw new IllegalArgumentException("Basic vehicle classification out of bounds [0..255]");
            }

            gvc.setKeyType(vc.keyType.asInt());
        }
        if (vc.hasResponderType()) {
            gvc.setResponderType(J2735ResponderGroupAffected.values()[vc.responderType.indexOf()]);
        }
        if (vc.hasResponseEquip()) {
            gvc.setResponseEquip(NamedNumberBuilder.genericIncidentResponseEquipment(vc.responseEquip));
        }
        if (vc.hasRole()) {
            gvc.setRole(J2735BasicVehicleRole.values()[vc.role.indexOf()]);
        }
        if (vc.hasVehicleType()) {
            gvc.setVehicleType(NamedNumberBuilder.genericVehicleGroupAffected(vc.vehicleType));
        }
        if (vc.hasRegional()) {
            while (vc.regional.elements().hasMoreElements()) {
                us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_ element = (us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_) vc.regional
                        .elements().nextElement();
                gvc.getRegional().add(new J2735RegionalContent().setId(element.regionId.asInt())
                        .setValue(element.regExtValue.getEncodedValue()));
            }
        }

        return gvc;
    }

}

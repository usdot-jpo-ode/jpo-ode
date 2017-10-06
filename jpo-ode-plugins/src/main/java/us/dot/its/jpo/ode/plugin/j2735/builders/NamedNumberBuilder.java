package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NamedNumber;

public class NamedNumberBuilder {

    private NamedNumberBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735NamedNumber genericGenericLocations(JsonNode genericLocations) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(genericLocations.name());
        gnn.setValue(genericLocations.asLong());
        return gnn;
    }

    public static J2735NamedNumber genericIncidentResponseEquipment(JsonNode incidentResponseEquipment) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(incidentResponseEquipment.name());
        gnn.setValue(incidentResponseEquipment.asLong());
        return gnn;
    }

    public static J2735NamedNumber genericVehicleGroupAffected(JsonNode vehicleGroupAffected) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(vehicleGroupAffected.name());
        gnn.setValue(vehicleGroupAffected.asLong());
        return gnn;
    }

}

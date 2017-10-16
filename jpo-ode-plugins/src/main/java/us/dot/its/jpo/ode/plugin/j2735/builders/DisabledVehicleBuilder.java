package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;

public class DisabledVehicleBuilder {
    
    private DisabledVehicleBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735DisabledVehicle genericDisabledVehicle(JsonNode disabledVehicle) {
        J2735DisabledVehicle gstatus = new J2735DisabledVehicle();

        // Required element
        gstatus.setStatusDetails(disabledVehicle.get("statusDetails").asInt());

        // Optional element
        if (disabledVehicle.get("locationDetails") != null) {
            gstatus.setLocationDetails(NamedNumberBuilder.genericGenericLocations(disabledVehicle.get("locationDetails")));
        }

        return gstatus;
    }

}

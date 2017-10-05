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
        gstatus.setStatusDetails(disabledVehicle.statusDetails.intValue());

        // Optional element
        if (disabledVehicle.hasLocationDetails()) {
            gstatus.setLocationDetails(NamedNumberBuilder.genericGenericLocations(disabledVehicle.locationDetails));
        }

        return gstatus;
    }

}

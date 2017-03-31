package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.DisabledVehicle;
import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;

public class OssDisabledVehicle {
    
    private OssDisabledVehicle() {}

    public static J2735DisabledVehicle genericDisabledVehicle(DisabledVehicle status) {
        J2735DisabledVehicle gstatus = new J2735DisabledVehicle();

        // Required element
        gstatus.setStatusDetails(status.statusDetails.intValue());

        // Optional element
        if (status.hasLocationDetails()) {
            gstatus.setLocationDetails(OssNamedNumber.genericGenericLocations(status.locationDetails));
        }

        return gstatus;
    }

}

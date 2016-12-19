package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.DisabledVehicle;
import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;

public class OssDisabledVehicle {

	public static J2735DisabledVehicle genericDisabledVehicle(DisabledVehicle status) {
		J2735DisabledVehicle gstatus = new J2735DisabledVehicle();

		gstatus.statusDetails = status.statusDetails.intValue();
		gstatus.locationDetails = OssNamedNumber.genericGenericLocations(status.locationDetails);
		
		return gstatus;
	}

}

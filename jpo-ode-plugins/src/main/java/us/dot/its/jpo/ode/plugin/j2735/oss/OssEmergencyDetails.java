package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735LightbarInUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735MultiVehicleResponse;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponseType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SirenInUse;

public class OssEmergencyDetails {

	public static J2735EmergencyDetails genericEmergencyDetails(EmergencyDetails vehicleAlerts) {
		J2735EmergencyDetails va = new J2735EmergencyDetails();
		
		// Required elements
		va.sspRights = vehicleAlerts.sspRights.intValue();
      va.sirenUse = J2735SirenInUse.values()[vehicleAlerts.sirenUse.indexOf()];
      va.lightsUse = J2735LightbarInUse.values()[vehicleAlerts.lightsUse.indexOf()];
      va.multi = J2735MultiVehicleResponse.values()[vehicleAlerts.multi.indexOf()];

		// Optional elements
      if (vehicleAlerts.hasEvents()) {
          va.events = OssPrivilegedEvents.genericPrivilegedEvents(vehicleAlerts.events);
      }
      if (vehicleAlerts.hasResponseType()) {
          va.responseType = J2735ResponseType.values()[vehicleAlerts.responseType.indexOf()];
      }
		
		return va;
	}

}

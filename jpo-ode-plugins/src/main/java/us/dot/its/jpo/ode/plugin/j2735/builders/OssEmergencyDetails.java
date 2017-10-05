package us.dot.its.jpo.ode.plugin.j2735.builders;

import us.dot.its.jpo.ode.j2735.dsrc.EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735LightbarInUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735MultiVehicleResponse;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponseType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SirenInUse;

public class OssEmergencyDetails {
    
    private OssEmergencyDetails() {
       throw new UnsupportedOperationException();
    }

	public static J2735EmergencyDetails genericEmergencyDetails(EmergencyDetails vehicleAlerts) {
		J2735EmergencyDetails va = new J2735EmergencyDetails();
		
		// Required elements
		va.setSspRights(vehicleAlerts.sspRights.intValue());
      va.setSirenUse(J2735SirenInUse.values()[vehicleAlerts.sirenUse.indexOf()]);
      va.setLightsUse(J2735LightbarInUse.values()[vehicleAlerts.lightsUse.indexOf()]);
      va.setMulti(J2735MultiVehicleResponse.values()[vehicleAlerts.multi.indexOf()]);

		// Optional elements
      if (vehicleAlerts.hasEvents()) {
          va.setEvents(OssPrivilegedEvents.genericPrivilegedEvents(vehicleAlerts.events));
      }
      if (vehicleAlerts.hasResponseType()) {
          va.setResponseType(J2735ResponseType.values()[vehicleAlerts.responseType.indexOf()]);
      }
		
		return va;
	}

}

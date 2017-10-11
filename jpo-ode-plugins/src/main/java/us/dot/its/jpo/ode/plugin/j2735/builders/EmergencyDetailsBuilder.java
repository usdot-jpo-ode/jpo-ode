package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735LightbarInUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735MultiVehicleResponse;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponseType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SirenInUse;

public class EmergencyDetailsBuilder {
    
    private EmergencyDetailsBuilder() {
       throw new UnsupportedOperationException();
    }

	public static J2735EmergencyDetails genericEmergencyDetails(JsonNode vehicleAlerts) {
		J2735EmergencyDetails va = new J2735EmergencyDetails();
		
		// Required elements
		va.setSspRights(vehicleAlerts.get("sspRights").asInt());
      va.setSirenUse(J2735SirenInUse.valueOf(vehicleAlerts.get("sirenUse").asText().replaceAll("-", "_").toUpperCase()));
      va.setLightsUse(J2735LightbarInUse.valueOf(vehicleAlerts.get("lightsUse").asText().replaceAll("-", "_").toUpperCase()));
      va.setMulti(J2735MultiVehicleResponse.valueOf(vehicleAlerts.get("multi").asText().replaceAll("-", "_").toUpperCase()));

		// Optional elements
      if (vehicleAlerts.get("events") != null) {
          va.setEvents(PrivilegedEventsBuilder.genericPrivilegedEvents(vehicleAlerts.get("events")));
      }
      if (vehicleAlerts.get("responseType") != null) {
          va.setResponseType(J2735ResponseType.valueOf(vehicleAlerts.get("responseType").asText().replaceAll("-", "_").toUpperCase()));
      }
		
		return va;
	}

}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEvents;

public class PrivilegedEventsBuilder {
   
   public enum J2735PrivilegedEventFlagsNames {
      peUnavailable,
      peEmergencyResponse,
      peEmergencyLightsActive,
      peEmergencySoundActive,
      peNonEmergencyLightsActive,
      peNonEmergencySoundActive,
   }

    private static final Integer SSP_LOWER_BOUND = 0;
    private static final Integer SSP_UPPER_BOUND = 31;
    
    private PrivilegedEventsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735PrivilegedEvents genericPrivilegedEvents(JsonNode events) {

        if (events.get("sspRights").asInt() < SSP_LOWER_BOUND || events.get("sspRights").asInt() > SSP_UPPER_BOUND) {
            throw new IllegalArgumentException("SSPindex value out of bounds [0..31]");
        }

        J2735PrivilegedEvents pe = new J2735PrivilegedEvents();
        J2735PrivilegedEventFlags flags = new J2735PrivilegedEventFlags();
        
        char[] eventBits = events.get("event").asText().toCharArray();

        for (int i = 0; i < eventBits.length; i++) {

            String eventName = J2735PrivilegedEventFlagsNames.values()[i].name();
            Boolean eventStatus = (eventBits[i] == '1' ? true : false);
            flags.put(eventName, eventStatus);
        }

        pe.setEvent(flags);
        pe.setSspRights(events.get("sspRights").asInt());

        return pe;
    }

}

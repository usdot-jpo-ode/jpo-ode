package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEvents;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEvents;

public class OssPrivilegedEvents {

    private static final Integer SSP_LOWER_BOUND = 0;
    private static final Integer SSP_UPPER_BOUND = 31;
    
    private OssPrivilegedEvents() {
       throw new UnsupportedOperationException();
    }

    public static J2735PrivilegedEvents genericPrivilegedEvents(PrivilegedEvents events) {

        if (events.sspRights.intValue() < SSP_LOWER_BOUND || events.sspRights.intValue() > SSP_UPPER_BOUND) {
            throw new IllegalArgumentException("SSPindex value out of bounds [0..31]");
        }

        J2735PrivilegedEvents pe = new J2735PrivilegedEvents();
        J2735PrivilegedEventFlags flags = new J2735PrivilegedEventFlags();

        for (int i = 0; i < events.getEvent().getSize(); i++) {

            String eventName = events.getEvent().getNamedBits().getMemberName(i);
            Boolean eventStatus = events.getEvent().getBit(i);

            if (eventName != null) {
                flags.put(eventName, eventStatus);
            }
        }

        pe.setEvent(flags);
        pe.setSspRights(events.sspRights.intValue());

        return pe;
    }

}

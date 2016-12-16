package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEvents;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEvents;

public class OssPrivilegedEvents {

	public static J2735PrivilegedEvents genericPrivilegedEvents(PrivilegedEvents events) {
		J2735PrivilegedEvents pe = new J2735PrivilegedEvents();
		
		pe.event = OssBitString.genericBitString(events.event);
		pe.sspRights = events.sspRights.intValue();
		
		return pe ;
	}

}

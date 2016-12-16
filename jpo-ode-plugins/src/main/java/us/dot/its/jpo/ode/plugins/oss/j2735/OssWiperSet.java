package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

public class OssWiperSet {

	public static J2735WiperSet genericWiperSet(WiperSet ws) {
		J2735WiperSet gws = new J2735WiperSet();
		
		gws.rateFront = ws.rateFront.intValue();
		gws.rateRear = ws.rateRear.intValue();
		gws.statusFront = J2735WiperStatus.values()[ws.statusFront.indexOf()];
		gws.statusRear = J2735WiperStatus.values()[ws.statusRear.indexOf()];
		
		return gws ;
	}

}

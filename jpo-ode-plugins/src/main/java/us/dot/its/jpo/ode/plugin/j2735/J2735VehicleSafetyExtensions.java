package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735VehicleSafetyExtensions extends Asn1Object implements J2735BsmPart2Extension {
	private static final long serialVersionUID = 1L;
	
	public J2735BitString events;
	public J2735PathHistory pathHistory;
	public J2735PathPrediction pathPrediction;
	public J2735BitString lights;
}

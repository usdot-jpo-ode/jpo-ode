package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpecialVehicleExtensions extends Asn1Object implements J2735BsmPart2Extension {
	private static final long serialVersionUID = 1L;
	
	public J2735EmergencyDetails vehicleAlerts;
	public J2735EventDescription description;
	public J2735TrailerData trailers;

}

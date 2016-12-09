package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735EmergencyDetails extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public Integer sspRights;
	public J2735PrivilegedEvents events;
	public J2735LightbarInUse lightsUse;
	public J2735MultiVehicleResponse multi;
	public J2735ResponseType responseType;
	public J2735SirenInUse sirenUse;

}

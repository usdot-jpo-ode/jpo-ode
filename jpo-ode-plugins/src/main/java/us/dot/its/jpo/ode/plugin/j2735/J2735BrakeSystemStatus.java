package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BrakeSystemStatus extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735BitString wheelBrakes;
	public String traction;
	public String abs;
	public String scs;
	public String brakeBoost;
	public String auxBrakes;

}

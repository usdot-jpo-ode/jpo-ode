package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ConnectingLane extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer lane;
	private J2735BitString maneuver;

	public Integer getLane() {
		return lane;
	}

	public void setLane(Integer lane) {
		this.lane = lane;
	}

	public J2735BitString getManeuver() {
		return maneuver;
	}

	public void setManeuver(J2735BitString maneuver) {
		this.maneuver = maneuver;
	}

}

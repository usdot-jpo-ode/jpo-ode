package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ConnectingLane extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer lane;
	private J2735AllowedManeuvers maneuver;

	public Integer getLane() {
		return lane;
	}

	public void setLane(Integer lane) {
		this.lane = lane;
	}

	public J2735AllowedManeuvers getManeuver() {
		return maneuver;
	}

	public void setManeuver(J2735AllowedManeuvers maneuver) {
		this.maneuver = maneuver;
	}

}

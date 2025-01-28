package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneDataAttribute extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pathEndPointAngle;
	private int laneCrownPointCenter;
	private int laneCrownPointLeft;
	private int laneCrownPointRight;
	private int laneAngle;
	private J2735SpeedLimitList speedLimits;

	public int getPathEndPointAngle() {
		return pathEndPointAngle;
	}

	public void setPathEndPointAngle(int pathEndPointAngle) {
		this.pathEndPointAngle = pathEndPointAngle;
	}

	public int getLaneCrownPointCenter() {
		return laneCrownPointCenter;
	}

	public void setLaneCrownPointCenter(int laneCrownPointCenter) {
		this.laneCrownPointCenter = laneCrownPointCenter;
	}

	public int getLaneCrownPointLeft() {
		return laneCrownPointLeft;
	}

	public void setLaneCrownPointLeft(int laneCrownPointLeft) {
		this.laneCrownPointLeft = laneCrownPointLeft;
	}

	public int getLaneCrownPointRight() {
		return laneCrownPointRight;
	}

	public void setLaneCrownPointRight(int laneCrownPointRight) {
		this.laneCrownPointRight = laneCrownPointRight;
	}

	public int getLaneAngle() {
		return laneAngle;
	}

	public void setLaneAngle(int laneAngle) {
		this.laneAngle = laneAngle;
	}

	public J2735SpeedLimitList getSpeedLimits() {
		return speedLimits;
	}

	public void setSpeedLimits(J2735SpeedLimitList speedLimits) {
		this.speedLimits = speedLimits;
	}

}

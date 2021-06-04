package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneDataAttribute extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer pathEndPointAngle;
	private Integer laneCrownPointCenter;
	private Integer laneCrownPointLeft;
	private Integer laneCrownPointRight;
	private Integer laneAngle;
	private J2735SpeedLimitList speedLimits;

	public Integer getPathEndPointAngle() {
		return pathEndPointAngle;
	}

	public void setPathEndPointAngle(Integer pathEndPointAngle) {
		this.pathEndPointAngle = pathEndPointAngle;
	}

	public Integer getLaneCrownPointCenter() {
		return laneCrownPointCenter;
	}

	public void setLaneCrownPointCenter(Integer laneCrownPointCenter) {
		this.laneCrownPointCenter = laneCrownPointCenter;
	}

	public Integer getLaneCrownPointLeft() {
		return laneCrownPointLeft;
	}

	public void setLaneCrownPointLeft(Integer laneCrownPointLeft) {
		this.laneCrownPointLeft = laneCrownPointLeft;
	}

	public Integer getLaneCrownPointRight() {
		return laneCrownPointRight;
	}

	public void setLaneCrownPointRight(Integer laneCrownPointRight) {
		this.laneCrownPointRight = laneCrownPointRight;
	}

	public Integer getLaneAngle() {
		return laneAngle;
	}

	public void setLaneAngle(Integer laneAngle) {
		this.laneAngle = laneAngle;
	}

	public J2735SpeedLimitList getSpeedLimits() {
		return speedLimits;
	}

	public void setSpeedLimits(J2735SpeedLimitList speedLimits) {
		this.speedLimits = speedLimits;
	}

}

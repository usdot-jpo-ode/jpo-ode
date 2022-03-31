package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

public class J2735RegulatorySpeedLimit {
	private J2735SpeedLimitType type;
	private BigDecimal speed;

	public J2735SpeedLimitType getType() {
		return type;
	}

	public void setType(J2735SpeedLimitType type) {
		this.type = type;
	}

	public BigDecimal getSpeed() {
		return speed;
	}

	public void setSpeed(BigDecimal speed) {
		this.speed = speed;
	}

}

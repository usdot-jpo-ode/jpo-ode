package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerHistoryPoint extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal elevationOffset;
	private BigDecimal heading;
	private BigDecimal pivotAngle;
	private J2735Node_XY positionOffset;
	private BigDecimal timeOffset;

	public BigDecimal getElevationOffset() {
		return elevationOffset;
	}

	public void setElevationOffset(BigDecimal elevationOffset) {
		this.elevationOffset = elevationOffset;
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	public BigDecimal getPivotAngle() {
		return pivotAngle;
	}

	public void setPivotAngle(BigDecimal pivotAngle) {
		this.pivotAngle = pivotAngle;
	}

	public J2735Node_XY getPositionOffset() {
		return positionOffset;
	}

	public void setPositionOffset(J2735Node_XY positionOffset) {
		this.positionOffset = positionOffset;
	}

	public BigDecimal getTimeOffset() {
		return timeOffset;
	}

	public void setTimeOffset(BigDecimal timeOffset) {
		this.timeOffset = timeOffset;
	}

}

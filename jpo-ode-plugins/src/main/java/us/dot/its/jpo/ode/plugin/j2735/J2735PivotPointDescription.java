package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PivotPointDescription extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal pivotOffset;
	private BigDecimal pivotAngle;
	private Boolean pivots;

	public BigDecimal getPivotOffset() {
		return pivotOffset;
	}

	public void setPivotOffset(BigDecimal pivotOffset) {
		this.pivotOffset = pivotOffset;
	}

	public BigDecimal getPivotAngle() {
		return pivotAngle;
	}

	public void setPivotAngle(BigDecimal pivotAngle) {
		this.pivotAngle = pivotAngle;
	}

	public Boolean getPivots() {
		return pivots;
	}

	public void setPivots(Boolean pivots) {
		this.pivots = pivots;
	}

}

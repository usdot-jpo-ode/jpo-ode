package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PositionalAccuracy extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal semiMajor;
	private BigDecimal semiMinor;
	private BigDecimal orientation;

	public BigDecimal getSemiMajor() {
		return semiMajor;
	}

	public void setSemiMajor(BigDecimal semiMajor) {
		this.semiMajor = semiMajor;
	}

	public BigDecimal getSemiMinor() {
		return semiMinor;
	}

	public void setSemiMinor(BigDecimal semiMinor) {
		this.semiMinor = semiMinor;
	}

	public BigDecimal getOrientation() {
		return orientation;
	}

	public void setOrientation(BigDecimal orientation) {
		this.orientation = orientation;
	}
}

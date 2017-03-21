package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathPrediction extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal confidence;
	private BigDecimal radiusOfCurve;

	public BigDecimal getConfidence() {
		return confidence;
	}

	public void setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
	}

	public BigDecimal getRadiusOfCurve() {
		return radiusOfCurve;
	}

	public void setRadiusOfCurve(BigDecimal radiusOfCurve) {
		this.radiusOfCurve = radiusOfCurve;
	}

}

package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735FullPositionVector extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735Position3D position;
	public BigDecimal heading;
	public J2735PositionalAccuracy posAccuracy;
	public J2735PositionConfidenceSet posConfidence;
	public J2735TransmissionAndSpeed speed;
	public J2735SpeedandHeadingandThrottleConfidence speedConfidence;
	public J2735TimeConfidence timeConfidence;
	public J2735DDateTime utcTime;

}

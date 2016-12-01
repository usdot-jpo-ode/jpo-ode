package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerHistoryPoint implements Asn1Object {

	public BigDecimal elevationOffset;
	public BigDecimal heading;
	public BigDecimal pivotAngle;
	public J2735Node_XY positionOffset;
	public BigDecimal timeOffset;

}

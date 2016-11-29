package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PositionalAccuracy implements Asn1Object {
	public BigDecimal semiMajor;
	public BigDecimal semiMinor;
	public BigDecimal orientation;
}

package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistoryPoint implements Asn1Object {

	public BigDecimal elevationOffset;
	public BigDecimal heading;
	public BigDecimal latOffset;
	public BigDecimal lonOffset;
	public J2735PositionalAccuracy posAccuracy;
	public BigDecimal speed;
	public BigDecimal timeOffset;

}

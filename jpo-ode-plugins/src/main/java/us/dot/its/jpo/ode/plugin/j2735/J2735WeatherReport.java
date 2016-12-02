package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735WeatherReport extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public Integer friction;
	public J2735EssPrecipYesNo isRaining;
	public J2735EssPrecipSituation precipSituation;
	public BigDecimal rainRate;
	public BigDecimal roadFriction;
	public Integer solarRadiation;

}

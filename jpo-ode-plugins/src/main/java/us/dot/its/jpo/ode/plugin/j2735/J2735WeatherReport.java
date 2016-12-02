package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735WeatherReport implements Asn1Object {

	public Integer friction;
	public J2735EssPrecipYesNo isRaining;
	public J2735EssPrecipSituation precipSituation;
	public BigDecimal rainRate;
	public BigDecimal roadFriction;
	public Integer solarRadiation;

}

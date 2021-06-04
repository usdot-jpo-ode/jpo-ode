package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeLLmD64b extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal lon;
	private BigDecimal lat;
	public J2735NodeLLmD64b() {
		
	}
	public J2735NodeLLmD64b(BigDecimal lon, BigDecimal lat) {
		this.lon = lon;
		this.lat = lat;
	}
	public BigDecimal getLon() {
		return lon;
	}

	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

}

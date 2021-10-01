package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Position3D extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer lat;
	private Integer lon;
	private Integer elevation;

	public Integer getLat() {
		return lat;
	}

	public void setLat(Integer lat) {
		this.lat = lat;
	}

	public Integer getLon() {
		return lon;
	}

	public void setLon(Integer lon) {
		this.lon = lon;
	}

	public Integer getElevation() {
		return elevation;
	}

	public void setElevation(Integer elevation) {
		this.elevation = elevation;
	}

}

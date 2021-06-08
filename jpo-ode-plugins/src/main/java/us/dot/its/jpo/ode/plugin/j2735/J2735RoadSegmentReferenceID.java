package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadSegmentReferenceID extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer region;
	private Integer id;

	public Integer getRegion() {
		return region;
	}

	public void setRegion(Integer region) {
		this.region = region;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}

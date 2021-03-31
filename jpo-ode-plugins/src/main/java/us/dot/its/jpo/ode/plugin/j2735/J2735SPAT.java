package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SPAT extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer timeStamp;
	private String name;
	private J2735IntersectionStateList intersectionStateList;

	public J2735IntersectionStateList getIntersectionStateList() {
		return intersectionStateList;
	}

	public void setIntersectionStateList(J2735IntersectionStateList intersectionStateList) {
		this.intersectionStateList = intersectionStateList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}

}

package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735DisabledVehicle extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer statusDetails;
	private J2735NamedNumber locationDetails;

	public Integer getStatusDetails() {
		return statusDetails;
	}

	public void setStatusDetails(Integer statusDetails) {
		this.statusDetails = statusDetails;
	}

	public J2735NamedNumber getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(J2735NamedNumber locationDetails) {
		this.locationDetails = locationDetails;
	}

}

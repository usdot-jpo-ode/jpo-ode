package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735EmergencyDetails extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer sspRights;
	private J2735PrivilegedEvents events;
	private J2735LightbarInUse lightsUse;
	private J2735MultiVehicleResponse multi;
	private J2735ResponseType responseType;
	private J2735SirenInUse sirenUse;

	public Integer getSspRights() {
		return sspRights;
	}

	public void setSspRights(Integer sspRights) {
		this.sspRights = sspRights;
	}

	public J2735PrivilegedEvents getEvents() {
		return events;
	}

	public void setEvents(J2735PrivilegedEvents events) {
		this.events = events;
	}

	public J2735LightbarInUse getLightsUse() {
		return lightsUse;
	}

	public void setLightsUse(J2735LightbarInUse lightsUse) {
		this.lightsUse = lightsUse;
	}

	public J2735MultiVehicleResponse getMulti() {
		return multi;
	}

	public void setMulti(J2735MultiVehicleResponse multi) {
		this.multi = multi;
	}

	public J2735ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(J2735ResponseType responseType) {
		this.responseType = responseType;
	}

	public J2735SirenInUse getSirenUse() {
		return sirenUse;
	}

	public void setSirenUse(J2735SirenInUse sirenUse) {
		this.sirenUse = sirenUse;
	}

}

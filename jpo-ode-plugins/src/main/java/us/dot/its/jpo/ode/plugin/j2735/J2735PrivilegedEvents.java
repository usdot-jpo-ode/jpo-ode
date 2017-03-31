package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PrivilegedEvents extends Asn1Object {

	private static final long serialVersionUID = 1L;

	private J2735BitString event;
	private Integer sspRights;

	public J2735BitString getEvent() {
		return event;
	}

	public void setEvent(J2735BitString event) {
		this.event = event;
	}

	public Integer getSspRights() {
		return sspRights;
	}

	public void setSspRights(Integer sspRights) {
		this.sspRights = sspRights;
	}

}

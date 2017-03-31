package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RTCMheader extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735AntennaOffsetSet offsetSet;
	private J2735BitString status;

	public J2735AntennaOffsetSet getOffsetSet() {
		return offsetSet;
	}

	public void setOffsetSet(J2735AntennaOffsetSet offsetSet) {
		this.offsetSet = offsetSet;
	}

	public J2735BitString getStatus() {
		return status;
	}

	public void setStatus(J2735BitString status) {
		this.status = status;
	}

}

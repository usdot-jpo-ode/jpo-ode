package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Offset extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735NodeListXY xy;
    private J2735NodeListLL ll;

    public J2735NodeListXY getXY() {
		return xy;
	}

	public void setXY(J2735NodeListXY xy) {
		this.xy = xy;
	}

    public J2735NodeListLL getLL() {
		return ll;
	}

	public void setLL(J2735NodeListLL ll) {
		this.ll = ll;
	}
}

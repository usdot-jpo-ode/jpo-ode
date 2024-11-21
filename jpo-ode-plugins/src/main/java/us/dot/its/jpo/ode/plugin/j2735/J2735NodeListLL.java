package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeListLL extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735NodeLL[] nodes;

    public J2735NodeLL[] getNodes() {
		return nodes;
	}

	public void setNodes(J2735NodeLL[] nodes) {
		this.nodes = nodes;
	}
}

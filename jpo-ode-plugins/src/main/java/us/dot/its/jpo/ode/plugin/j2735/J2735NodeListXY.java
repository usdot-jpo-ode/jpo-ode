package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeListXY extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private J2735NodeSetXY nodes;
	//ToDo add J2735ComputedLane optional

	public J2735NodeSetXY getNodes() {
		return nodes;
	}

	public void setNodes(J2735NodeSetXY nodes) {
		this.nodes = nodes;
	}


}

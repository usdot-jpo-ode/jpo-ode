package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeAttributeXY extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private J2735NodeAttribute nodeAttrList;

	public J2735NodeAttribute getNodeAttrList() {
		return nodeAttrList;
	}

	public void setNodeAttrList(J2735NodeAttribute nodeAttrList) {
		this.nodeAttrList = nodeAttrList;
	}

}

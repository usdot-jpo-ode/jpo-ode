package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeAttributeXYList extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735NodeAttributeXY> localNode = new ArrayList<>();

	public List<J2735NodeAttributeXY> getLocalNode() {
		return localNode;
	}

	public void setLocalNode(List<J2735NodeAttributeXY> localNode) {
		this.localNode = localNode;
	}

}

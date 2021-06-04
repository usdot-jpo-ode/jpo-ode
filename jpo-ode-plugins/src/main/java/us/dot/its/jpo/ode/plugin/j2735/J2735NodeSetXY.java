package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeSetXY  extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735NodeXY> nodes = new ArrayList<>();
	public List<J2735NodeXY> getNodes() {
		return nodes;
	}
	public void setNodes(List<J2735NodeXY> nodes) {
		this.nodes = nodes;
	}

}

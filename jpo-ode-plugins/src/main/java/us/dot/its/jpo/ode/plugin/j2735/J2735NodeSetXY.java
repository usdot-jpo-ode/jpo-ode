package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeSetXY  extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735NodeXY> NodeXY = new ArrayList<>();

	@JsonProperty("NodeXY")
	public List<J2735NodeXY> getNodes() {
		return NodeXY;
	}
	public void setNodes(List<J2735NodeXY> NodeXY) {
		this.NodeXY = NodeXY;
	}

}

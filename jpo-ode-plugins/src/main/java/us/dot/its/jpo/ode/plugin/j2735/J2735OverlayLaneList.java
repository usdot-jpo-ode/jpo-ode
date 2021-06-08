package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735OverlayLaneList extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Integer> laneIds = new ArrayList<>();

	public List<Integer> getLaneIds() {
		return laneIds;
	}

	public void setLaneIds(List<Integer> laneIds) {
		this.laneIds = laneIds;
	}

}

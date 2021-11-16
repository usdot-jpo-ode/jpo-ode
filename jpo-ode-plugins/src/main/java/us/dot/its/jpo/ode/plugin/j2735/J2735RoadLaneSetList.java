package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadLaneSetList extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735GenericLane> roadLanes = new ArrayList<>();

	public List<J2735GenericLane> getRoadLanes() {
		return roadLanes;
	}

	public void setRoadLanes(List<J2735GenericLane> roadLanes) {
		this.roadLanes = roadLanes;
	}

}

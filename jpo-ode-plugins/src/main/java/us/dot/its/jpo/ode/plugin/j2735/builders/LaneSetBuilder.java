package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneList;

public class LaneSetBuilder {
	private LaneSetBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735LaneList genericLaneSet(JsonNode laneSetNode) {
		J2735LaneList laneList = new J2735LaneList();
		if (laneSetNode.isArray()) {
			Iterator<JsonNode> elements = laneSetNode.elements();
			while (elements.hasNext()) {
				laneList.getLaneSet()
						.add(GenericLaneBuilder.genericGenericLane(elements.next().get("GenericLane")));
			}
		} else {
			laneList.getLaneSet()
					.add(GenericLaneBuilder.genericGenericLane(laneSetNode.get("GenericLane")));

		}
		return laneList;
	}
}

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

		JsonNode genericLane = laneSetNode.get("GenericLane");
		if (genericLane != null && genericLane.isArray()) {
			Iterator<JsonNode> elements = genericLane.elements();

			while (elements.hasNext()) {
				laneList.getLaneSet()
                    .add(GenericLaneBuilder.genericGenericLane(elements.next()));
			}
		} else if (genericLane != null) {
			laneList.getLaneSet()
				.add(GenericLaneBuilder.genericGenericLane(genericLane));
		}

		return laneList;
	}
}

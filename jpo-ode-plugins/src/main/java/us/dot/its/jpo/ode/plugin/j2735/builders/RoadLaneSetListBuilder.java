package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RoadLaneSetList;

public class RoadLaneSetListBuilder {
    private RoadLaneSetListBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735RoadLaneSetList genericRoadLaneSetList(JsonNode roadLaneSetNode) {
		J2735RoadLaneSetList roadLaneSetList = new J2735RoadLaneSetList();

		JsonNode genericLane = roadLaneSetNode.get("GenericLane");
		if (genericLane != null && genericLane.isArray()) {
			Iterator<JsonNode> elements = genericLane.elements();

			while (elements.hasNext()) {
				roadLaneSetList.getRoadLanes()
                    .add(GenericLaneBuilder.genericGenericLane(elements.next()));
			}
		} else if (genericLane != null) {
			roadLaneSetList.getRoadLanes()
				.add(GenericLaneBuilder.genericGenericLane(genericLane));
		}

		return roadLaneSetList;
	}
}

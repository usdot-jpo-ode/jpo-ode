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

		System.out.println(laneSetNode.toString());

		if (laneSetNode.isArray()) {
			Iterator<JsonNode> elements = laneSetNode.elements();

			while (elements.hasNext()) {
				System.out.println("Trying to make GenericLane from a list");
				laneList.getLaneSet()
                    .add(GenericLaneBuilder.genericGenericLane(elements.next().get("GenericLane")));
			}
		} else {
			JsonNode genericLane = laneSetNode.get("GenericLane");
			if(genericLane != null)
			{
				System.out.println("Trying to make GenericLane from a non-list");
				laneList.getLaneSet()
                	.add(GenericLaneBuilder.genericGenericLane(genericLane));
			}
		}
		return laneList;
	}
}

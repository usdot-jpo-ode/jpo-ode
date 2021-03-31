package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionStateList;

public class IntersectionStateListBuilder {
	private IntersectionStateListBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735IntersectionStateList genericIntersectionStateList(JsonNode intersections) {
		J2735IntersectionStateList genericIntersectionStateList = new J2735IntersectionStateList();
		if (intersections.isArray()) {
			Iterator<JsonNode> elements = intersections.elements();
			while (elements.hasNext()) {
				genericIntersectionStateList.getIntersectionStatelist()
						.add(IntersectionStateBuilder.genericIntersectionState(elements.next()));
			}
		} else {
			genericIntersectionStateList.getIntersectionStatelist()
					.add(IntersectionStateBuilder.genericIntersectionState(intersections));

		}
		return genericIntersectionStateList;
	}
}

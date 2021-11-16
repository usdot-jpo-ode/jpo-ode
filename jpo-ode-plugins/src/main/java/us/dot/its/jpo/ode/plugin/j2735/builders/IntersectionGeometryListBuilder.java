package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometryList;

public class IntersectionGeometryListBuilder {
	private IntersectionGeometryListBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735IntersectionGeometryList genericIntersectionGeometryList(JsonNode intersections) {
		J2735IntersectionGeometryList genericIntersectionGeometryList = new J2735IntersectionGeometryList();
		
		JsonNode intersectionGeometry = intersections.get("IntersectionGeometry");
		if (intersectionGeometry != null && intersectionGeometry.isArray()) {
			Iterator<JsonNode> elements = intersectionGeometry.elements();

			while (elements.hasNext()) {
				genericIntersectionGeometryList.getIntersections()
					.add(IntersectionGeometryBuilder.genericIntersectionGeometry(elements.next()));
			}
		} else if (intersectionGeometry != null) {
			genericIntersectionGeometryList.getIntersections()
				.add(IntersectionGeometryBuilder.genericIntersectionGeometry(intersectionGeometry));

		}
		return genericIntersectionGeometryList;
	}
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RestrictionClassList;

public class RestrictionClassListBuilder {
    private RestrictionClassListBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735RestrictionClassList genericRestrictionClassList(JsonNode restrictionClassListNode) {
		J2735RestrictionClassList restrictionClassList = new J2735RestrictionClassList();

		JsonNode restrictionClassAssignment = restrictionClassListNode.get("RestrictionClassAssignment");
		if (restrictionClassAssignment != null && restrictionClassAssignment.isArray()) {
			Iterator<JsonNode> elements = restrictionClassAssignment.elements();

			while (elements.hasNext()) {
				restrictionClassList.getRestrictionList()
                    .add(RestrictionClassAssignmentBuilder.genericRestrictionClassAssignment(elements.next()));
			}
		} else if (restrictionClassAssignment != null) {
			restrictionClassList.getRestrictionList()
				.add(RestrictionClassAssignmentBuilder.genericRestrictionClassAssignment(restrictionClassAssignment));
		}

		return restrictionClassList;
	}
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RestrictionClassAssignment;

public class RestrictionClassAssignmentBuilder {
    private RestrictionClassAssignmentBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735RestrictionClassAssignment genericRestrictionClassAssignment(JsonNode restrictionClassAssignmentNode) {
        J2735RestrictionClassAssignment restrictionClassAssignment = new J2735RestrictionClassAssignment();

        JsonNode id = restrictionClassAssignmentNode.get("id");
		if(id != null)
		{
            restrictionClassAssignment.setId(id.asInt());
        }

        JsonNode users = restrictionClassAssignmentNode.get("users");
		if(users != null)
		{
            restrictionClassAssignment.setUsers(RestrictionUserTypeListBuilder.genericRestrictionUserTypeList(users));
        }

        return restrictionClassAssignment;
    }
}

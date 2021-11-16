package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RestrictionUserTypeList;

public class RestrictionUserTypeListBuilder {
    private RestrictionUserTypeListBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735RestrictionUserTypeList genericRestrictionUserTypeList(JsonNode restrictionUserTypeListNode) {
		J2735RestrictionUserTypeList restrictionUserTypeList = new J2735RestrictionUserTypeList();

		JsonNode restrictionUserType = restrictionUserTypeListNode.get("RestrictionUserType");
		if (restrictionUserType != null && restrictionUserType.isArray()) {
			Iterator<JsonNode> elements = restrictionUserType.elements();

			while (elements.hasNext()) {
				restrictionUserTypeList.getRestrictionUserType()
                    .add(RestrictionUserTypeBuilder.genericRestrictionUserType(elements.next()));
			}
		} else if (restrictionUserType != null) {
			restrictionUserTypeList.getRestrictionUserType()
				.add(RestrictionUserTypeBuilder.genericRestrictionUserType(restrictionUserType));
		}

		return restrictionUserTypeList;
	}
}

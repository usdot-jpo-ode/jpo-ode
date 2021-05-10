package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SPAT;

public class SPATBuilder {
	private SPATBuilder()
	{
		throw new UnsupportedOperationException();
	}
	public static J2735SPAT genericSPAT(JsonNode SPATMessage) {
		J2735SPAT genericSPAT = new J2735SPAT();
		JsonNode timeStamp = SPATMessage.get("timeStamp");
		if(timeStamp != null)
		{
			genericSPAT.setTimeStamp(timeStamp.asInt());
		}
		
		JsonNode name = SPATMessage.get("name");
		if(name != null)
		{
			genericSPAT.setName(name.asText());
		}
		
		JsonNode intersections = SPATMessage.get("intersections");
		if(intersections != null)
		{
			genericSPAT.setIntersectionStateList(IntersectionStateListBuilder.genericIntersectionStateList(intersections));					
		}
		return genericSPAT;		
	}
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class IntersectionGeometryBuilder {
	private IntersectionGeometryBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735IntersectionGeometry genericIntersectionGeometry(JsonNode intersectionNode) {
		J2735IntersectionGeometry intersection = new J2735IntersectionGeometry();
		
		JsonNode name = intersectionNode.get("name");
		if (name != null) {
			intersection.setName(name.asText());
		}
		
		JsonNode RefID = intersectionNode.get("id");
		if (RefID != null) {
			J2735IntersectionReferenceID idObj = new J2735IntersectionReferenceID();
			JsonNode region = RefID.get("region");
			if (region != null)
			{
				idObj.setRegion(region.asInt());
			}
			
			JsonNode id = RefID.get("id");
			if (id != null)
			{
				idObj.setId(id.asInt());
			}
			intersection.setId(idObj);
		}
		
		JsonNode revision = intersectionNode.get("revision");
		if (revision != null) {
			intersection.setRevision(revision.asInt());
		}
		
		JsonNode refPoint = intersectionNode.get("refPoint");
		if(refPoint != null)
		{
			J2735Position3D refPointObj= new J2735Position3D();
			if(refPoint.get("lat") != null)
			{
				refPointObj.setLat(refPoint.get("lat").asInt());
			}
			
			if(refPoint.get("long") != null)
			{
				refPointObj.setLon(refPoint.get("long").asInt());
			}
			
			if(refPoint.get("elevation") != null)
			{
				refPointObj.setLat(refPoint.get("elevation").asInt());
			}
			intersection.setRefPoint(refPointObj);
		}
		
		JsonNode laneWidth = intersectionNode.get("laneWidth");
		if (laneWidth != null) {
			intersection.setLaneWidth(laneWidth.asInt());
		}
		
		JsonNode speedLimits = intersectionNode.get("speedLimits");
		if (speedLimits != null) {
			intersection.setSpeedLimits(SpeedLimitListBuilder.genericSpeedLimitList(speedLimits));			
		}
		
		JsonNode laneSet = intersectionNode.get("laneSet");
		if (laneSet != null) {
			intersection.setLaneSet(LaneSetBuilder.genericLaneSet(laneSet));			
		}
		
		return intersection;
	}
}

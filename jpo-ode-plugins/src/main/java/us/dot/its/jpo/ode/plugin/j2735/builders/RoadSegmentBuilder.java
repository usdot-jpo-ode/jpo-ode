package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSegment;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSegmentReferenceID;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class RoadSegmentBuilder {
    private RoadSegmentBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735RoadSegment genericRoadSegment(JsonNode roadSegmentNode) {
        J2735RoadSegment roadSegmentObj = new J2735RoadSegment();

        JsonNode name = roadSegmentNode.get("name");
		if (name != null) {
			roadSegmentObj.setName(name.asText());
		}

        JsonNode refID = roadSegmentNode.get("id");
		if (refID != null) {
			J2735RoadSegmentReferenceID idObj = new J2735RoadSegmentReferenceID();
			JsonNode region = refID.get("region");
			if (region != null)
			{
				idObj.setRegion(region.asInt());
			}
			
			JsonNode id = refID.get("id");
			if (id != null)
			{
				idObj.setId(id.asInt());
			}
			roadSegmentObj.setId(idObj);
		}

        JsonNode revision = roadSegmentNode.get("revision");
		if (revision != null) {
			roadSegmentObj.setRevision(revision.asInt());
		}

        JsonNode refPoint = roadSegmentNode.get("refPoint");
		if (refPoint != null) {
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
			roadSegmentObj.setRefPoint(refPointObj);
		}

        JsonNode laneWidth = roadSegmentNode.get("laneWidth");
		if (laneWidth != null) {
			roadSegmentObj.setLaneWidth(laneWidth.asInt());
		}

        JsonNode speedLimits = roadSegmentNode.get("speedLimits");
		if (speedLimits != null) {
			roadSegmentObj.setSpeedLimits(SpeedLimitListBuilder.genericSpeedLimitList(speedLimits));			
		}

        JsonNode roadLaneSet = roadSegmentNode.get("roadLaneSet");
		if (roadLaneSet != null) {
			roadSegmentObj.setRoadLaneSet(RoadLaneSetListBuilder.genericRoadLaneSetList(roadLaneSet));			
		}

        return roadSegmentObj;
    }
}

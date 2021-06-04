package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometryList;
import us.dot.its.jpo.ode.plugin.j2735.J2735DataParameters;
import us.dot.its.jpo.ode.plugin.j2735.J2735LayerType;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;
import us.dot.its.jpo.ode.plugin.j2735.J2735RestrictionClassList;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSegmentList;

public class MAPBuilder {
	private MAPBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735MAP genericMAP(JsonNode MAPMessage) {
		J2735MAP genericMAP = new J2735MAP();
		JsonNode timeStamp = MAPMessage.get("timeStamp");
		if (timeStamp != null) {
			genericMAP.setTimeStamp(timeStamp.asInt());
		}

		JsonNode msgIssueRevision = MAPMessage.get("msgIssueRevision");
		if (msgIssueRevision != null) {
			genericMAP.setMsgIssueRevision(msgIssueRevision.asInt());
		}

		JsonNode layerType = MAPMessage.get("layerType");
		if (layerType != null) {
			JSONObject layerTypeJson = new JSONObject(layerType);
			String layerTypeKey = layerTypeJson.keys().hasNext()
					? layerTypeJson.keys().next().toString().toLowerCase().replace("-", "_")
					: "";
			if (layerTypeKey == J2735LayerType.curveData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.curveData);
			} else if (layerTypeKey == J2735LayerType.generalMapData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.generalMapData);
			} else if (layerTypeKey == J2735LayerType.mixedContent.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.mixedContent);
			} else if (layerTypeKey == J2735LayerType.none.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.none);
			} else if (layerTypeKey == J2735LayerType.intersectionData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.intersectionData);
			} else if (layerTypeKey == J2735LayerType.roadwaySectionData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.roadwaySectionData);
			} else if (layerTypeKey == J2735LayerType.parkingAreaData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.parkingAreaData);
			} else if (layerTypeKey == J2735LayerType.sharedLaneData.name().toLowerCase()) {
				genericMAP.setLayerType(J2735LayerType.sharedLaneData);
			}
		}
		
		JsonNode layerID = MAPMessage.get("layerID");
		if (layerID != null) {
			genericMAP.setLayerID(layerID.asInt());
		}
	
		JsonNode intersections = MAPMessage.get("intersections");
		if (intersections != null) {
			genericMAP
					.setIntersectionGeometryList(IntersectionGeometryListBuilder.genericIntersectionGeometryList(intersections));
		}
		
		JsonNode roadSegments = MAPMessage.get("intersections");
		if (roadSegments != null) {
			genericMAP
					.setJ2735RoadSegmentList(J2735RoadSegmentListBuilder.genericJ2735RoadSegmentList(roadSegments));
		}
		
		JsonNode dataParameters = MAPMessage.get("intersections");
		if (dataParameters != null) {
			genericMAP
					.setJ2735DataParameters(J2735DataParametersBuilder.genericJ2735DataParameters(dataParameters));
		}
		
		JsonNode restrictionList = MAPMessage.get("intersections");
		if (restrictionList != null) {
			genericMAP
					.setJ2735RestrictionClassList(J2735RestrictionClassListBuilder.genericJ2735RestrictionClassList(restrictionList));
		}
		return genericMAP;
	}
}

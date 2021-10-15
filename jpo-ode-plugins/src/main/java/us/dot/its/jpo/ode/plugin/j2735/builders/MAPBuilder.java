package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735LayerType;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;
import us.dot.its.jpo.ode.plugin.j2735.J2735DataParameters;

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
			JSONObject layerTypeJson = new JSONObject(layerType.toString());
			String layerTypeKey = layerTypeJson.keys().hasNext()
					? layerTypeJson.keys().next().toString().toLowerCase().replace("-", "_")
					: "";
			if (layerTypeKey.equals(J2735LayerType.curveData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.curveData);
			} else if (layerTypeKey.equals(J2735LayerType.generalMapData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.generalMapData);
			} else if (layerTypeKey.equals(J2735LayerType.mixedContent.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.mixedContent);
			} else if (layerTypeKey.equals(J2735LayerType.none.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.none);
			} else if (layerTypeKey.equals(J2735LayerType.intersectionData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.intersectionData);
			} else if (layerTypeKey.equals(J2735LayerType.roadwaySectionData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.roadwaySectionData);
			} else if (layerTypeKey.equals(J2735LayerType.parkingAreaData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.parkingAreaData);
			} else if (layerTypeKey.equals(J2735LayerType.sharedLaneData.name().toLowerCase())) {
				genericMAP.setLayerType(J2735LayerType.sharedLaneData);
			}
		}

		JsonNode layerID = MAPMessage.get("layerID");
		if (layerID != null) {
			genericMAP.setLayerID(layerID.asInt());
		}

		JsonNode intersections = MAPMessage.get("intersections");
		if (intersections != null) {
			genericMAP.setIntersections(IntersectionGeometryListBuilder.genericIntersectionGeometryList(intersections));
		}
		
		JsonNode roadSegments = MAPMessage.get("roadSegments");
		if (roadSegments != null) {
			genericMAP.setRoadSegments(RoadSegmentListBuilder.genericRoadSegmentList(roadSegments));
		}
		
		JsonNode dataParameters = MAPMessage.get("dataParameters");
		if (dataParameters != null) {
			J2735DataParameters dataParametersObj = new J2735DataParameters();

			JsonNode processMethod = dataParameters.get("processMethod");
			if (processMethod != null) {
				dataParametersObj.setProcessMethod(processMethod.asText());
			}

			JsonNode processAgency = dataParameters.get("processAgency");
			if (processAgency != null) {
				dataParametersObj.setProcessAgency(processAgency.asText());
			}

			JsonNode lastCheckedDate = dataParameters.get("lastCheckedDate");
			if (lastCheckedDate != null) {
				dataParametersObj.setLastCheckedDate(lastCheckedDate.asText());
			}

			JsonNode geoidUsed = dataParameters.get("geoidUsed");
			if (geoidUsed != null) {
				dataParametersObj.setGeoidUsed(geoidUsed.asText());
			}

			genericMAP.setDataParameters(dataParametersObj);
		}

		JsonNode restrictionList = MAPMessage.get("restrictionList");
		if (restrictionList != null) {
			genericMAP.setRestrictionList(
					RestrictionClassListBuilder.genericRestrictionClassList(restrictionList));
		}
		return genericMAP;
	}
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributes;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneDirection;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneSharing;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;

public class GenericLaneBuilder {

	public static J2735GenericLane genericGenericLane(JsonNode laneSetNode) {
		J2735GenericLane genericLane = new J2735GenericLane();
		
		JsonNode laneID = laneSetNode.get("laneID");
		if (laneID != null) {
			genericLane.setLaneID(laneID.asInt());
		}
		
		JsonNode name = laneSetNode.get("name");
		if (name != null) {
			genericLane.setName(name.asText());
		}
		
		JsonNode ingressApproach = laneSetNode.get("ingressApproach");
		if (ingressApproach != null) {
			genericLane.setIngressApproach(ingressApproach.asInt());
		}
		
		JsonNode egressApproach = laneSetNode.get("egressApproach");
		if (egressApproach != null) {
			genericLane.setEgressApproach(egressApproach.asInt());
		}
		
		JsonNode laneAttributes = laneSetNode.get("laneAttributes");
		if(laneAttributes != null)
		{
			J2735LaneAttributes laneAttributesObj= new J2735LaneAttributes();
			
			if(laneAttributes.get("directionalUse") != null)
			{
				JSONObject directionalUseJson = new JSONObject(laneAttributes.get("directionalUse"));
				String directionalUseKey = directionalUseJson.keys().hasNext()
						? directionalUseJson.keys().next().toString().toLowerCase().replace("-", "_")
						: "";
				if (directionalUseKey == J2735LaneDirection.egressPath.name().toLowerCase()) {
					laneAttributesObj.setDirectionalUse(J2735LaneDirection.egressPath);
				} else if (directionalUseKey == J2735LaneDirection.ingressPath.name().toLowerCase()) {
					laneAttributesObj.setDirectionalUse(J2735LaneDirection.ingressPath);
				} 
			}
			
			if(laneAttributes.get("shareWith") != null)
			{
				JSONObject shareWithJson = new JSONObject(laneAttributes.get("shareWith"));
				String shareWithKey = shareWithJson.keys().hasNext()
						? shareWithJson.keys().next().toString().toLowerCase().replace("-", "_")
						: "";
				if (shareWithKey == J2735LaneSharing.overlappingLaneDescriptionProvided.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.overlappingLaneDescriptionProvided);
				} 
				else if (shareWithKey == J2735LaneSharing.multipleLanesTreatedAsOneLane.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.multipleLanesTreatedAsOneLane);
				}
				else if (shareWithKey == J2735LaneSharing.otherNonMotorizedTrafficTypes.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.otherNonMotorizedTrafficTypes);
				} 
				else if (shareWithKey == J2735LaneSharing.individualMotorizedVehicleTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.individualMotorizedVehicleTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.busVehicleTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.busVehicleTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.pedestriansTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.pedestriansTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.taxiVehicleTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.taxiVehicleTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.pedestriansTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.pedestrianTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.taxiVehicleTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.trackedVehicleTraffic);
				} 
				else if (shareWithKey == J2735LaneSharing.pedestriansTraffic.name().toLowerCase()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.cyclistVehicleTraffic);
				} 
			}
			if(laneAttributes.get("laneType") != null)
			{
				JsonNode laneType =laneAttributes.get("laneType");
				J2735LaneTypeAttributes  laneTypeObj = new J2735LaneTypeAttributes();
				//TODO
			}

		}
		//TODO
//		private J2735AllowedManeuvers maneuvers;
//		private J2735NodeListXY nodeList;
//		private J2735ConnectsToList connectsTo;
//		private J2735OverlayLaneList overlays;
	
	
		
		
		return null;
	}

}

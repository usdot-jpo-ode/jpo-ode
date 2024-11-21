package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AllowedManeuvers;
import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributes;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesBarrier;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesBike;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesCrosswalk;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesParking;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesSidewalk;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesStriping;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesTrackedVehicle;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneAttributesVehicle;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneDirection;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneSharing;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;
import us.dot.its.jpo.ode.plugin.j2735.J2735OverlayLaneList;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;

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
		if (laneAttributes != null) {
			J2735LaneAttributes laneAttributesObj = new J2735LaneAttributes();

			JsonNode directionalUse = laneAttributes.get("directionalUse");
			if (directionalUse != null) {
				J2735BitString directionalUseObj = BitStringBuilder.genericBitString(directionalUse, J2735LaneDirection.values());
            	laneAttributesObj.setDirectionalUse(directionalUseObj);
			}

			JsonNode sharedWith = laneAttributes.get("sharedWith");
			if (sharedWith != null) {
				J2735BitString sharedWithObj = BitStringBuilder.genericBitString(sharedWith, J2735LaneSharing.values());
            	laneAttributesObj.setShareWith(sharedWithObj);
			}

			JsonNode laneType = laneAttributes.get("laneType");
			if (laneType != null) {
				J2735LaneTypeAttributes laneTypeObj = new J2735LaneTypeAttributes();

				if (laneType.get("vehicle") != null) {
					J2735BitString vehicleObj = BitStringBuilder.genericBitString(laneType.get("vehicle"), J2735LaneAttributesVehicle.values());
            		laneTypeObj.setVehicle(vehicleObj);
				} else if (laneType.get("crosswalk") != null) {
					J2735BitString crosswalkObj = BitStringBuilder.genericBitString(laneType.get("crosswalk"), J2735LaneAttributesCrosswalk.values());
            		laneTypeObj.setCrosswalk(crosswalkObj);
				} else if (laneType.get("bikeLane") != null) {;
					J2735BitString bikeLaneObj = BitStringBuilder.genericBitString(laneType.get("bikeLane"), J2735LaneAttributesBike.values());
            		laneTypeObj.setBikeLane(bikeLaneObj);
				} else if (laneType.get("sidewalk") != null) {
					J2735BitString sidewalkObj = BitStringBuilder.genericBitString(laneType.get("sidewalk"), J2735LaneAttributesSidewalk.values());
            		laneTypeObj.setSidewalk(sidewalkObj);
				} else if (laneType.get("median") != null) {
					J2735BitString medianObj = BitStringBuilder.genericBitString(laneType.get("median"), J2735LaneAttributesBarrier.values());
            		laneTypeObj.setMedian(medianObj);
				} else if (laneType.get("striping") != null) {
					J2735BitString stripingObj = BitStringBuilder.genericBitString(laneType.get("striping"), J2735LaneAttributesStriping.values());
            		laneTypeObj.setStriping(stripingObj);
				} else if (laneType.get("trackedVehicle") != null) {
					J2735BitString trackedVehicleObj = BitStringBuilder.genericBitString(laneType.get("trackedVehicle"), J2735LaneAttributesTrackedVehicle.values());
            		laneTypeObj.setTrackedVehicle(trackedVehicleObj);
				} else if (laneType.get("parking") != null) {
					J2735BitString parkingObj = BitStringBuilder.genericBitString(laneType.get("parking"), J2735LaneAttributesParking.values());
            		laneTypeObj.setParking(parkingObj);
				}

				laneAttributesObj.setLaneType(laneTypeObj);
			}

			genericLane.setLaneAttributes(laneAttributesObj);
		}

		JsonNode maneuvers = laneSetNode.get("maneuvers");
		if (maneuvers != null) {
			J2735BitString maneuversObj = BitStringBuilder.genericBitString(maneuvers, J2735AllowedManeuvers.values());
            genericLane.setManeuvers(maneuversObj);
		}

		JsonNode nodeList = laneSetNode.get("nodeList");
		if (nodeList != null) {
			genericLane.setNodeList(NodeListXYBuilder.genericNodeListXY(nodeList));
		}

		JsonNode connectsTo = laneSetNode.get("connectsTo");
		if (connectsTo != null) {
			genericLane.setConnectsTo(ConnectsToListBuilder.genericConnectsToList(connectsTo));
		}

		JsonNode overlays = laneSetNode.get("overlays");
		if (overlays != null) {
			J2735OverlayLaneList overlayList = new J2735OverlayLaneList();
			if (overlays.isArray()) {
				Iterator<JsonNode> elements = overlays.elements();
				while (elements.hasNext()) {
					overlayList.getLaneIds().add(elements.next().asInt());
				}
			} else {
				overlayList.getLaneIds().add(overlays.asInt());
			}
		}

		return genericLane;
	}
}

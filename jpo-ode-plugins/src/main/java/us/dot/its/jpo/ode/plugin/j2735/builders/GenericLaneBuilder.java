package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import org.json.JSONObject;

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

			if (laneAttributes.get("directionalUse") != null) {
				Integer directionalUse = laneAttributes.get("directionalUse").asInt();
				if (directionalUse.equals(J2735LaneDirection.egressPath.getDirectUseId())) {
					laneAttributesObj.setDirectionalUse(J2735LaneDirection.egressPath);
				} else if (directionalUse.equals(J2735LaneDirection.ingressPath.getDirectUseId())) {
					laneAttributesObj.setDirectionalUse(J2735LaneDirection.ingressPath);
				}
			}

			if (laneAttributes.get("sharedWith") != null) {
				Integer shareWith = laneAttributes.get("sharedWith").asInt();
				if (shareWith == J2735LaneSharing.overlappingLaneDescriptionProvided.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.overlappingLaneDescriptionProvided);
				} else if (shareWith == J2735LaneSharing.multipleLanesTreatedAsOneLane.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.multipleLanesTreatedAsOneLane);
				} else if (shareWith == J2735LaneSharing.otherNonMotorizedTrafficTypes.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.otherNonMotorizedTrafficTypes);
				} else if (shareWith == J2735LaneSharing.individualMotorizedVehicleTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.individualMotorizedVehicleTraffic);
				} else if (shareWith == J2735LaneSharing.busVehicleTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.busVehicleTraffic);
				} else if (shareWith == J2735LaneSharing.pedestriansTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.pedestriansTraffic);
				} else if (shareWith == J2735LaneSharing.taxiVehicleTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.taxiVehicleTraffic);
				} else if (shareWith == J2735LaneSharing.pedestriansTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.pedestrianTraffic);
				} else if (shareWith == J2735LaneSharing.taxiVehicleTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.trackedVehicleTraffic);
				} else if (shareWith == J2735LaneSharing.pedestriansTraffic.getLaneSharingID()) {
					laneAttributesObj.setShareWith(J2735LaneSharing.cyclistVehicleTraffic);
				}
			}

			if (laneAttributes.get("laneType") != null) {
				JsonNode laneType = laneAttributes.get("laneType");
				J2735LaneTypeAttributes laneTypeObj = new J2735LaneTypeAttributes();
				if (laneType.get("vehicle") != null) {
					Integer vehicleKey = laneType.get("vehicle").asInt();
					if (vehicleKey == J2735LaneAttributesVehicle.isVehicleRevocableLane.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.isVehicleRevocableLane);
					} else if (vehicleKey == J2735LaneAttributesVehicle.isVehicleFlyOverLane.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.isVehicleFlyOverLane);
					} else if (vehicleKey == J2735LaneAttributesVehicle.hovLaneUseOnly.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.hovLaneUseOnly);
					} else if (vehicleKey == J2735LaneAttributesVehicle.restrictedToBusUse.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.restrictedToBusUse);
					} else if (vehicleKey == J2735LaneAttributesVehicle.restrictedToTaxiUse.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.restrictedToTaxiUse);
					} else if (vehicleKey == J2735LaneAttributesVehicle.restrictedFromPublicUse
							.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.restrictedFromPublicUse);
					} else if (vehicleKey == J2735LaneAttributesVehicle.hasIRbeaconCoverage.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.hasIRbeaconCoverage);
					} else if (vehicleKey == J2735LaneAttributesVehicle.permissionOnRequest.getLaneAttrvehicleID()) {
						laneTypeObj.setVehicle(J2735LaneAttributesVehicle.permissionOnRequest);
					}
				} else if (laneType.get("crosswalk") != null) {
					Integer crosswalkKey = laneType.get("crosswalk").asInt();
					if (crosswalkKey == J2735LaneAttributesCrosswalk.crosswalkRevocableLane
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.crosswalkRevocableLane);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.bicyleUseAllowed
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.bicyleUseAllowed);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.isXwalkFlyOverLane
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.isXwalkFlyOverLane);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.fixedCycleTime
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.fixedCycleTime);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.biDirectionalCycleTimes
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.biDirectionalCycleTimes);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.hasPushToWalkButton
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.hasPushToWalkButton);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.audioSupport
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.audioSupport);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.rfSignalRequestPresent
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.rfSignalRequestPresent);
					} else if (crosswalkKey == J2735LaneAttributesCrosswalk.unsignalizedSegmentsPresent
							.getLaneAttributesCrosswalkID()) {
						laneTypeObj.setCrosswalk(J2735LaneAttributesCrosswalk.unsignalizedSegmentsPresent);
					}
				} else if (laneType.get("bikeLane") != null) {;
					Integer bikeLaneKey = laneType.get("bikeLane").asInt();
					if (bikeLaneKey == J2735LaneAttributesBike.bikeRevocableLane.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.bikeRevocableLane);
					} else if (bikeLaneKey == J2735LaneAttributesBike.pedestrianUseAllowed.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.pedestrianUseAllowed);
					} else if (bikeLaneKey == J2735LaneAttributesBike.isBikeFlyOverLane.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.isBikeFlyOverLane);
					} else if (bikeLaneKey == J2735LaneAttributesBike.fixedCycleTime.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.fixedCycleTime);
					} else if (bikeLaneKey == J2735LaneAttributesBike.biDirectionalCycleTimes.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.biDirectionalCycleTimes);
					} else if (bikeLaneKey == J2735LaneAttributesBike.fixedCycleTime.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.fixedCycleTime);
					} else if (bikeLaneKey == J2735LaneAttributesBike.biDirectionalCycleTimes.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.biDirectionalCycleTimes);
					} else if (bikeLaneKey == J2735LaneAttributesBike.isolatedByBarrier.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.isolatedByBarrier);
					} else if (bikeLaneKey == J2735LaneAttributesBike.unsignalizedSegmentsPresent.getLaneAttributesBikeID()) {
						laneTypeObj.setBikeLane(J2735LaneAttributesBike.unsignalizedSegmentsPresent);
					}
				} else if (laneType.get("sidewalk") != null) {
					Integer sidewalkKey = laneType.get("sidewalk").asInt();
					if (sidewalkKey == J2735LaneAttributesSidewalk.sidewalkRevocableLane
							.getLaneAttributesSidewalkID()) {
						laneTypeObj.setSidewalk(J2735LaneAttributesSidewalk.sidewalkRevocableLane);
					} else if (sidewalkKey == J2735LaneAttributesSidewalk.bicyleUseAllowed
							.getLaneAttributesSidewalkID()) {
						laneTypeObj.setSidewalk(J2735LaneAttributesSidewalk.bicyleUseAllowed);
					} else if (sidewalkKey == J2735LaneAttributesSidewalk.isSidewalkFlyOverLane
							.getLaneAttributesSidewalkID()) {
						laneTypeObj.setSidewalk(J2735LaneAttributesSidewalk.isSidewalkFlyOverLane);
					} else if (sidewalkKey == J2735LaneAttributesSidewalk.walkBikes.getLaneAttributesSidewalkID()) {
						laneTypeObj.setSidewalk(J2735LaneAttributesSidewalk.walkBikes);
					}
				} else if (laneType.get("median") != null) {
					Integer medianKey = laneType.get("median").asInt();
					if (medianKey == J2735LaneAttributesBarrier.medianRevocableLane.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.medianRevocableLane);
					} else if (medianKey == J2735LaneAttributesBarrier.median.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.median);
					} else if (medianKey == J2735LaneAttributesBarrier.whiteLineHashing.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.whiteLineHashing);
					} else if (medianKey == J2735LaneAttributesBarrier.stripedLines.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.stripedLines);
					} else if (medianKey == J2735LaneAttributesBarrier.doubleStripedLines
							.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.doubleStripedLines);
					} else if (medianKey == J2735LaneAttributesBarrier.trafficCones.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.trafficCones);
					} else if (medianKey == J2735LaneAttributesBarrier.constructionBarrier
							.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.constructionBarrier);
					} else if (medianKey == J2735LaneAttributesBarrier.trafficChannels.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.trafficChannels);
					} else if (medianKey == J2735LaneAttributesBarrier.lowCurbs.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.lowCurbs);
					} else if (medianKey == J2735LaneAttributesBarrier.highCurbs.getLaneAttributesBarrierID()) {
						laneTypeObj.setMedian(J2735LaneAttributesBarrier.highCurbs);
					}
				} else if (laneType.get("striping") != null) {
					Integer stripingKey = laneType.get("striping").asInt();
					if (stripingKey == J2735LaneAttributesStriping.stripeToConnectingLanesRevocableLane
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeToConnectingLanesRevocableLane);
					} else if (stripingKey == J2735LaneAttributesStriping.stripeDrawOnLeft
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeDrawOnLeft);
					} else if (stripingKey == J2735LaneAttributesStriping.stripeDrawOnRight
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeDrawOnRight);
					} else if (stripingKey == J2735LaneAttributesStriping.stripeToConnectingLanesLeft
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeToConnectingLanesLeft);
					} else if (stripingKey == J2735LaneAttributesStriping.stripeToConnectingLanesRight
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeToConnectingLanesRight);
					} else if (stripingKey == J2735LaneAttributesStriping.stripeToConnectingLanesAhead
							.getLaneAttributesStripingID()) {
						laneTypeObj.setStriping(J2735LaneAttributesStriping.stripeToConnectingLanesAhead);
					}
				} else if (laneType.get("trackedVehicle") != null) {
					Integer trackedVehicleKey = laneType.get("trackedVehicle").asInt();
					if (trackedVehicleKey == J2735LaneAttributesTrackedVehicle.specRevocableLane
							.getLaneAttributesTrackedVehicleID()) {
						laneTypeObj.setTrackedVehicle(J2735LaneAttributesTrackedVehicle.specRevocableLane);
					} else if (trackedVehicleKey == J2735LaneAttributesTrackedVehicle.specCommuterRailRoadTrack
							.getLaneAttributesTrackedVehicleID()) {
						laneTypeObj.setTrackedVehicle(J2735LaneAttributesTrackedVehicle.specCommuterRailRoadTrack);
					} else if (trackedVehicleKey == J2735LaneAttributesTrackedVehicle.specLightRailRoadTrack
							.getLaneAttributesTrackedVehicleID()) {
						laneTypeObj.setTrackedVehicle(J2735LaneAttributesTrackedVehicle.specLightRailRoadTrack);
					} else if (trackedVehicleKey == J2735LaneAttributesTrackedVehicle.specHeavyRailRoadTrack
							.getLaneAttributesTrackedVehicleID()) {
						laneTypeObj.setTrackedVehicle(J2735LaneAttributesTrackedVehicle.specHeavyRailRoadTrack);
					} else if (trackedVehicleKey == J2735LaneAttributesTrackedVehicle.specOtherRailType
							.getLaneAttributesTrackedVehicleID()) {
						laneTypeObj.setTrackedVehicle(J2735LaneAttributesTrackedVehicle.specOtherRailType);
					} else if (laneType.get("parking") != null) {
						Integer parkingKey = laneType.get("parking").asInt();
						if (parkingKey == J2735LaneAttributesParking.parkingRevocableLane
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.parkingRevocableLane);
						} else if (parkingKey == J2735LaneAttributesParking.parallelParkingInUse
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.parallelParkingInUse);
						} else if (parkingKey == J2735LaneAttributesParking.headInParkingInUse
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.headInParkingInUse);
						} else if (parkingKey == J2735LaneAttributesParking.doNotParkZone
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.doNotParkZone);
						} else if (parkingKey == J2735LaneAttributesParking.parkingForBusUse
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.parkingForBusUse);
						} else if (parkingKey == J2735LaneAttributesParking.parkingForTaxiUse
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.parkingForTaxiUse);
						} else if (parkingKey == J2735LaneAttributesParking.noPublicParkingUse
								.getLaneAttributesParkingID()) {
							laneTypeObj.setParking(J2735LaneAttributesParking.noPublicParkingUse);
						}
					}
					laneAttributesObj.setLaneType(laneTypeObj);
				}

			}
			genericLane.setLaneAttributes(laneAttributesObj);
		}

		JsonNode maneuvers = laneSetNode.get("maneuvers");
		if (maneuvers != null) {
			Integer maneuversKey =maneuvers.get("maneuvers").asInt();
			if (maneuversKey == J2735AllowedManeuvers.maneuverStraightAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverStraightAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverLeftAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverLeftAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverRightAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverRightAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverUTurnAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverUTurnAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverLeftTurnOnRedAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverLeftTurnOnRedAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverRightTurnOnRedAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverRightTurnOnRedAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverLaneChangeAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverLaneChangeAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.maneuverNoStoppingAllowed.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.maneuverNoStoppingAllowed);
			} else if (maneuversKey == J2735AllowedManeuvers.yieldAllwaysRequired.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.yieldAllwaysRequired);
			} else if (maneuversKey == J2735AllowedManeuvers.goWithHalt.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.goWithHalt);
			} else if (maneuversKey == J2735AllowedManeuvers.caution.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.caution);
			} else if (maneuversKey == J2735AllowedManeuvers.reserved1.getAllowedManeuversID()) {
				genericLane.setManeuvers(J2735AllowedManeuvers.reserved1);
			}
		}

		JsonNode nodeList = laneSetNode.get("nodeList");
		if (nodeList != null) {
			genericLane.setNodeList(NodeListBuilder.genericNodeList(nodeList));
		}

		JsonNode connectsTo = laneSetNode.get("connectsTo");
		if (connectsTo != null) {
			// TODO
//			genericLane.setConnectsTo(ConnectsToListBuilder.genericConnectsToList(connectsTo));
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

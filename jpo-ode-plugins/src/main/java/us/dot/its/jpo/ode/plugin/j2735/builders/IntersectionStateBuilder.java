package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionState;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionStatusObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735ManeuverAssistList;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementList;

public class IntersectionStateBuilder {
	private IntersectionStateBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735IntersectionState genericIntersectionState(JsonNode intersectionState) {
		J2735IntersectionState genericIntersectionState = new J2735IntersectionState();
		if(intersectionState.get("IntersectionState").get("name") != null)
			genericIntersectionState.setName(intersectionState.get("IntersectionState").get("name").asText());

		J2735IntersectionReferenceID id = new J2735IntersectionReferenceID();
		if(intersectionState.get("IntersectionState").get("id").get("id") != null)
			id.setId(intersectionState.get("IntersectionState").get("id").get("id").asInt());

		if(intersectionState.get("IntersectionState").get("id").get("region") != null)
			id.setRegion(intersectionState.get("IntersectionState").get("id").get("region").asInt());
		
		genericIntersectionState.setId(id);
		
		if(intersectionState.get("IntersectionState").get("revision") != null)
			genericIntersectionState.setRevision(intersectionState.get("IntersectionState").get("revision").asInt());

		if(intersectionState.get("IntersectionState").get("status") != null)
		{

			Integer status = intersectionState.get("IntersectionState").get("status").asInt();
			for (J2735IntersectionStatusObject statusobj : J2735IntersectionStatusObject.values()) {
				if (statusobj.ordinal() == status) {
					genericIntersectionState.setStatus(J2735IntersectionStatusObject.values()[status]);
					break;
				}
			}
		}

		if (intersectionState.get("IntersectionState").get("moy") != null)
			genericIntersectionState.setMoy(intersectionState.get("IntersectionState").get("moy").asInt());

		if (intersectionState.get("IntersectionState").get("timeStamp") != null)
			genericIntersectionState.setTimeStamp(intersectionState.get("IntersectionState").get("timeStamp").asInt());

		if (intersectionState.get("IntersectionState").get("enabledLanes") != null
				&& intersectionState.get("IntersectionState").get("enabledLanes").get("LaneID") != null) {
			if (intersectionState.get("IntersectionState").get("enabledLanes").get("LaneID").isArray()) {
				Iterator<JsonNode> elements = intersectionState.get("IntersectionState").get("enabledLanes").get("LaneID").elements();
				while (elements.hasNext()) {
					genericIntersectionState.getEnabledLanes().getEnabledLaneList()
							.add(elements.next().get("LaneID").asInt());
				}
			} else {
				genericIntersectionState.getEnabledLanes().getEnabledLaneList()
						.add(intersectionState.get("IntersectionState").get("enabledLanes").get("LaneID").asInt());
			}
		}

		if (intersectionState.get("IntersectionState").get("states") != null 
				&& intersectionState.get("IntersectionState").get("states").get("MovementState") != null) { // MovementList
			J2735MovementList states = new J2735MovementList();
			if (intersectionState.get("IntersectionState").get("states").get("MovementState").isArray()) {
				Iterator<JsonNode> elements = intersectionState.get("IntersectionState").get("states").get("MovementState").elements();
				while (elements.hasNext()) {
					states.getMovementList().add(MovementStateBuilder.genericMovementState(elements.next()));
				}
			} else {
				states.getMovementList().add(MovementStateBuilder.genericMovementState(
						intersectionState.get("IntersectionState").get("states").get("MovementState")));
			}
			genericIntersectionState.setStates(states);
		}

		if (intersectionState.get("IntersectionState").get("maneuverAssistList") != null
				&& intersectionState.get("IntersectionState").get("maneuverAssistList").get("ConnectionManeuverAssist") != null) { // maneuverAssistList
			J2735ManeuverAssistList maneuverAssistList = new J2735ManeuverAssistList();
			if (intersectionState.get("IntersectionState").get("maneuverAssistList").get("ConnectionManeuverAssist").isArray()) {
				Iterator<JsonNode> elements = intersectionState.get("maneuverAssistList").get("ConnectionManeuverAssist").elements();
				while (elements.hasNext()) {
					maneuverAssistList.getManeuverAssistList()
							.add(ManeuverAssistBuilder.genericManeuverAssist(elements.next()));
				}
			} else {
				maneuverAssistList.getManeuverAssistList().add(ManeuverAssistBuilder.genericManeuverAssist(
						intersectionState.get("IntersectionState").get("maneuverAssistList").get("ConnectionManueverAssist")));
			}
			genericIntersectionState.setManeuverAssistList(maneuverAssistList);
		}

		return genericIntersectionState;
	}
}

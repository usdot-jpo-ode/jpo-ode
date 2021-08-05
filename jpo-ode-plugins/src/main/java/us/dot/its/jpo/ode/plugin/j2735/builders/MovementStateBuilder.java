package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ManeuverAssistList;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementEventList;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementState;

public class MovementStateBuilder {
	private MovementStateBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735MovementState genericMovementState(JsonNode movementStatesJson) {
		J2735MovementState state = new J2735MovementState();
		if (movementStatesJson.get("movementName") != null)
			state.setMovementName(movementStatesJson.get("movementName").asText());

		if (movementStatesJson.get("signalGroup") != null)
			state.setSignalGroup(movementStatesJson.get("signalGroup").asInt());

		if (movementStatesJson.get("state-time-speed")!= null && movementStatesJson.get("state-time-speed").get("MovementEvent").isArray()) {
			Iterator<JsonNode> nestElements = movementStatesJson.get("state-time-speed").get("MovementEvent").elements();
			J2735MovementEventList state_time_speed = new J2735MovementEventList();
			while (nestElements.hasNext()) {
				state_time_speed.getMovementEventList()
						.add(MovementEventBuilder.genericMovementState(nestElements.next()));
			}
			state.setState_time_speed(state_time_speed);
		} else {
			J2735MovementEventList state_time_speed = new J2735MovementEventList();
			state_time_speed.getMovementEventList().add(MovementEventBuilder
					.genericMovementState(movementStatesJson.get("state-time-speed").get("MovementEvent")));

			state.setState_time_speed(state_time_speed);
		}
		
		if(movementStatesJson.get("maneuverAssistList") != null
				&& movementStatesJson.get("maneuverAssistList").get("ConnectionManueverAssist") != null)
		{
			J2735ManeuverAssistList maneuverAssistList = new J2735ManeuverAssistList();
			if (movementStatesJson.get("maneuverAssistList").get("ConnectionManueverAssist").isArray()) {
				Iterator<JsonNode> elements = movementStatesJson.get("maneuverAssistList").get("ConnectionManueverAssist").elements();
				while (elements.hasNext()) {
					maneuverAssistList.getManeuverAssistList()
							.add(ManeuverAssistBuilder.genericManeuverAssist(elements.next()));
				}
			} else {
				maneuverAssistList.getManeuverAssistList().add(ManeuverAssistBuilder.genericManeuverAssist(
						movementStatesJson.get("maneuverAssistList").get("ConnectionManueverAssist")));
			}
			state.setManeuverAssistList(maneuverAssistList);
		}

		return state;
	}
}

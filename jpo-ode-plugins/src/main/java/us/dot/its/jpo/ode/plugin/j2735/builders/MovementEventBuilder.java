package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisorySpeedList;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementEvent;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;
import us.dot.its.jpo.ode.plugin.j2735.J2735TimeChangeDetails;

public class MovementEventBuilder {
	private MovementEventBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735MovementEvent genericMovementState(JsonNode movementEventJson) {
		J2735MovementEvent event = new J2735MovementEvent();
		if (movementEventJson.get("eventState") != null) 
		{
			String eventState = movementEventJson.get("eventState").toString();
			JSONObject eventStateJson = new JSONObject(eventState);
			String eventStateKey = eventStateJson.keys().hasNext()
					? eventStateJson.keys().next().toString().toLowerCase().replace("-", "_")
					: "";
			if (J2735MovementPhaseState.STOP_AND_REMAIN.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.STOP_AND_REMAIN);

			} else if (J2735MovementPhaseState.UNAVAILABLE.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.UNAVAILABLE);

			} else if (J2735MovementPhaseState.STOP_THEN_PROCEED.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.STOP_THEN_PROCEED);

			} else if (J2735MovementPhaseState.DARK.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.DARK);

			} else if (J2735MovementPhaseState.PRE_MOVEMENT.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.PRE_MOVEMENT);

			} else if (J2735MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED);

			} else if (J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED);

			} else if (J2735MovementPhaseState.PERMISSIVE_CLEARANCE.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.PERMISSIVE_CLEARANCE);

			} else if (J2735MovementPhaseState.PROTECTED_CLEARANCE.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.PROTECTED_CLEARANCE);

			} else if (J2735MovementPhaseState.CAUTION_CONFLICTING_TRAFFIC.name().toLowerCase().equals(eventStateKey)) {
				event.setEventState(J2735MovementPhaseState.CAUTION_CONFLICTING_TRAFFIC);
			}
		}

		if (movementEventJson.get("timing") != null) {
			J2735TimeChangeDetails timing = new J2735TimeChangeDetails();
			if (movementEventJson.get("timing").get("minEndTime") != null)
				timing.setMinEndTime(movementEventJson.get("timing").get("minEndTime").asInt());

			if (movementEventJson.get("timing").get("startTime") != null)
				timing.setStartTime(movementEventJson.get("timing").get("startTime").asInt());

			if (movementEventJson.get("timing").get("maxEndTime") != null)
				timing.setMaxEndTime(movementEventJson.get("timing").get("maxEndTime").asInt());

			if (movementEventJson.get("timing").get("likelyTime") != null)
				timing.setLikelyTime(movementEventJson.get("timing").get("likelyTime").asInt());

			if (movementEventJson.get("timing").get("confidence") != null)
				timing.setConfidence(movementEventJson.get("timing").get("confidence").asInt());

			if (movementEventJson.get("timing").get("nextTime") != null)
				timing.setNextTime(movementEventJson.get("timing").get("nextTime").asInt());

			event.setTiming(timing);
		}

		if (movementEventJson.get("speeds") != null 
				&& movementEventJson.get("speeds").get("AdvisorySpeed") != null) {
			J2735AdvisorySpeedList speeds = new J2735AdvisorySpeedList();
			if (movementEventJson.get("speeds").get("AdvisorySpeed").isArray()) {
				Iterator<JsonNode> elements = movementEventJson.get("speeds").get("AdvisorySpeed").elements();
				while (elements.hasNext()) {
					speeds.getAdvisorySpeedList().add(AdvisorySpeedBuilder.genericAdvisorySpeed(elements.next()));
				}
			} else {
				speeds.getAdvisorySpeedList().add(AdvisorySpeedBuilder
						.genericAdvisorySpeed(movementEventJson.get("speeds").get("AdvisorySpeed")));
			}
			event.setSpeeds(speeds);
		}
		return event;
	}
}

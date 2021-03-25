package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectionManueverAssist;

public class ManeuverAssistBuilder {
	private ManeuverAssistBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735ConnectionManueverAssist genericManeuverAssist(JsonNode maneuverAssistJson) {
		J2735ConnectionManueverAssist maneuverAssist = new J2735ConnectionManueverAssist();
		if (maneuverAssistJson.get("connectionID") != null)
			maneuverAssist.setConnectionID(maneuverAssistJson.get("connectionID").asInt());

		if (maneuverAssistJson.get("queueLength") != null)
			maneuverAssist.setQueueLength(maneuverAssistJson.get("queueLength").asInt());

		if (maneuverAssistJson.get("waitOnStop") != null)
			maneuverAssist.setWaitOnStop(maneuverAssistJson.get("waitOnStop").asBoolean());

		if (maneuverAssistJson.get("availableStorageLength") != null)
			maneuverAssist.setAvailableStorageLength(maneuverAssistJson.get("availableStorageLength").asInt());

		if (maneuverAssistJson.get("pedBicycleDetect") != null)
			maneuverAssist.setPedBicycleDetect(maneuverAssistJson.get("pedBicycleDetect").asBoolean());

		return maneuverAssist;
	}

}

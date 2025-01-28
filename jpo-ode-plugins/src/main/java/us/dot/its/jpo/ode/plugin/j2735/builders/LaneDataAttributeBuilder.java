package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneDataAttribute;

public class LaneDataAttributeBuilder {
    private LaneDataAttributeBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735LaneDataAttribute genericLaneDataAttribute(JsonNode data) {
		J2735LaneDataAttribute laneDataAttribute = new J2735LaneDataAttribute();

        JsonNode pathEndPointAngle = data.get("pathEndPointAngle");
        if (pathEndPointAngle != null) {
            laneDataAttribute.setPathEndPointAngle(pathEndPointAngle.asInt());
        }

        JsonNode laneCrownPointCenter = data.get("laneCrownPointCenter");
        if (laneCrownPointCenter != null) {
            laneDataAttribute.setLaneCrownPointCenter(laneCrownPointCenter.asInt());
        }

        JsonNode laneCrownPointLeft = data.get("laneCrownPointLeft");
        if (laneCrownPointLeft != null) {
            laneDataAttribute.setLaneCrownPointLeft(laneCrownPointLeft.asInt());
        }

        JsonNode laneCrownPointRight = data.get("laneCrownPointRight");
        if (laneCrownPointRight != null) {
            laneDataAttribute.setLaneCrownPointRight(laneCrownPointRight.asInt());
        }

        JsonNode laneAngle = data.get("laneAngle");
        if (laneAngle != null) {
            laneDataAttribute.setLaneAngle(laneAngle.asInt());
        }

        JsonNode speedLimits = data.get("speedLimits");
        if (speedLimits != null) {
            laneDataAttribute.setSpeedLimits(SpeedLimitListBuilder.genericSpeedLimitList(speedLimits));
        }

		return laneDataAttribute;
	}
}

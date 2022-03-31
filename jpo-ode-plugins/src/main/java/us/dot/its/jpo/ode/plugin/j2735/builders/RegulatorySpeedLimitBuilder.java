package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RegulatorySpeedLimit;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedLimitType;

public class RegulatorySpeedLimitBuilder {
    private RegulatorySpeedLimitBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735RegulatorySpeedLimit genericRegulatorySpeedLimit(JsonNode regSpeedLimitNode) {
        J2735RegulatorySpeedLimit genericRegulatorySpeedLimit = new J2735RegulatorySpeedLimit();

        JsonNode type = regSpeedLimitNode.get("type");
		if (type != null) {
			J2735SpeedLimitType enumType = J2735SpeedLimitType.valueOf(type.fieldNames().next());
			genericRegulatorySpeedLimit.setType(enumType);
		}

        JsonNode speed = regSpeedLimitNode.get("speed");
		if (speed != null) {
			genericRegulatorySpeedLimit.setSpeed(SpeedOrVelocityBuilder.genericSpeed(speed));
			
		}

        return genericRegulatorySpeedLimit;
    }
}

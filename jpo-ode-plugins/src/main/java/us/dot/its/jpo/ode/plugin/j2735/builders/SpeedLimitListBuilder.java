package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedLimitList;

public class SpeedLimitListBuilder {
    private SpeedLimitListBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735SpeedLimitList genericSpeedLimitList(JsonNode speedLimits) {
		
		J2735SpeedLimitList genericSpeedLimitList = new J2735SpeedLimitList();
		if (speedLimits.isArray()) {
			Iterator<JsonNode> elements = speedLimits.elements();
			while (elements.hasNext()) {
				genericSpeedLimitList.getSpeedLimits()
						.add(RegulatorySpeedLimitBuilder.genericRegulatorySpeedLimit(elements.next().get("RegulatorySpeedLimit")));
			}
		} else {
			genericSpeedLimitList.getSpeedLimits()
					.add(RegulatorySpeedLimitBuilder.genericRegulatorySpeedLimit(speedLimits.get("RegulatorySpeedLimit")));

		}
		return genericSpeedLimitList;
	}
}

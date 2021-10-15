package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RegulatorySpeedLimit;

public class RegulatorySpeedLimitBuilder {
    private RegulatorySpeedLimitBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735RegulatorySpeedLimit genericRegulatorySpeedLimit(JsonNode regSpeedLimit) {
        J2735RegulatorySpeedLimit genericRegulatorySpeedLimit = new J2735RegulatorySpeedLimit();

        return genericRegulatorySpeedLimit;
    }
}

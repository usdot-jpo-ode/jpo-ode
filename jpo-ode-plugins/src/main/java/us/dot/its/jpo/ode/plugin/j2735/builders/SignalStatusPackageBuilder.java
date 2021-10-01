package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalStatusPackage;

public class SignalStatusPackageBuilder {
    private SignalStatusPackageBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735SignalStatusPackage genericSignalStatusPackage(JsonNode sigStatusNode) {
        J2735SignalStatusPackage signalStatusPackage = new J2735SignalStatusPackage();

        return signalStatusPackage;
    }
}

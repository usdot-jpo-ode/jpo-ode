package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalStatusPackageList;

public class SignalStatusPackageListBuilder {
    private SignalStatusPackageListBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735SignalStatusPackageList genericSignalStatusPackageList(JsonNode sigStatus) {
        J2735SignalStatusPackageList signalStatusPackageList = new J2735SignalStatusPackageList();

        JsonNode signalStatusPackage = sigStatus.get("SignalStatusPackage");
        if(signalStatusPackage == null) {
            return null;
        }

        if (signalStatusPackage.isArray()) {
            Iterator<JsonNode> elements = signalStatusPackage.elements();

            while (elements.hasNext()) {
                signalStatusPackageList.getSigStatus()
                    .add(SignalStatusPackageBuilder.genericSignalStatusPackage(elements.next()));
            }
        } else {
            signalStatusPackageList.getSigStatus()
                    .add(SignalStatusPackageBuilder.genericSignalStatusPackage(signalStatusPackage));

        }

        return signalStatusPackageList;
    }
}

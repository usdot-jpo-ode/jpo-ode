package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalRequestList;

public class SignalRequestListBuilder {
    private SignalRequestListBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735SignalRequestList genericSignalRequestList(JsonNode requests) {
		J2735SignalRequestList signalRequestList = new J2735SignalRequestList();

		JsonNode signalRequest = requests.get("SignalRequestPackage");
		if(signalRequest != null) {
			if (signalRequest.isArray()) {
				Iterator<JsonNode> elements = signalRequest.elements();
				while (elements.hasNext()) {
					signalRequestList.getRequests().add(SignalRequestPackageBuilder.genericSignalRequestPackage(elements.next()));
				}
			} else {
				signalRequestList.getRequests().add(SignalRequestPackageBuilder.genericSignalRequestPackage(signalRequest));
			}
		}
		
		return signalRequestList;
	}
}

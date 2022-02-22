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

		if (requests.isArray()) {
			Iterator<JsonNode> elements = requests.elements();

			while (elements.hasNext()) {
				signalRequestList.getRequests()
                    .add(SignalRequestPackageBuilder.genericSignalRequestPackage(elements.next()));
			}
		} else {
			JsonNode signalRequest = requests.get("SignalRequestPackage");
			if(signalRequest != null)
			{
				signalRequestList.getRequests()
					.add(SignalRequestPackageBuilder.genericSignalRequestPackage(signalRequest));
			}
		}
		
		return signalRequestList;
	}
}

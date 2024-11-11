package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;

public class SignalStatusBuilder {
	private SignalStatusBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735SignalStatus genericSignalStatus(JsonNode statusNode) {
		J2735SignalStatus signalStatus = new J2735SignalStatus();

		JsonNode sequenceNumber = statusNode.get("sequenceNumber");
		if (sequenceNumber != null) {
			signalStatus.setSequenceNumber(sequenceNumber.asInt());
		}

		JsonNode statusId = statusNode.get("id");
		if (statusId != null) {
			J2735IntersectionReferenceID idObj = new J2735IntersectionReferenceID();

			JsonNode region = statusId.get("region");
			if (region != null) {
				idObj.setRegion(region.asInt());
			}

			JsonNode refId = statusId.get("id");
			if (refId != null) {
				idObj.setId(refId.asInt());
			}

			signalStatus.setId(idObj);
		}

		JsonNode sigStatus = statusNode.get("sigStatus");
		if (sigStatus != null) {
			signalStatus.setSigStatus(SignalStatusPackageListBuilder.genericSignalStatusPackageList(sigStatus));
		}

		return signalStatus;
	}
}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisorySpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisorySpeedType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;

public class AdvisorySpeedBuilder {
	private AdvisorySpeedBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735AdvisorySpeed genericAdvisorySpeed(JsonNode AdvisorySpeedJson) {
		J2735AdvisorySpeed advisorySpeed = new J2735AdvisorySpeed();
		if (AdvisorySpeedJson.get("type") != null) {

			String type = AdvisorySpeedJson.get("type").toString();
			JSONObject typeJson = new JSONObject(type);
			String typeKey = typeJson.keys().hasNext()
					? typeJson.keys().next().toString().toLowerCase().replace("-", "_")
					: "";

			if (J2735AdvisorySpeedType.ECODRIVE.name().toLowerCase().equals(typeKey)) {
				advisorySpeed.setType(J2735AdvisorySpeedType.ECODRIVE);
			} else if (J2735AdvisorySpeedType.GREENWAVE.name().toLowerCase().equals(typeKey)) {
				advisorySpeed.setType(J2735AdvisorySpeedType.GREENWAVE);
			} else if (J2735AdvisorySpeedType.NONE.name().toLowerCase().equals(typeKey)) {
				advisorySpeed.setType(J2735AdvisorySpeedType.NONE);
			} else if (J2735AdvisorySpeedType.TRANSIT.name().toLowerCase().equals(typeKey)) {
				advisorySpeed.setType(J2735AdvisorySpeedType.TRANSIT);
			}
		}

		if (AdvisorySpeedJson.get("speed") != null) {
			advisorySpeed.setSpeed(AdvisorySpeedJson.get("speed").asInt());
		}

		if (AdvisorySpeedJson.get("distance") != null) {
			advisorySpeed.setDistance(AdvisorySpeedJson.get("distance").asInt());
		}

		if (AdvisorySpeedJson.get("class") != null) {
			advisorySpeed.setClassId(AdvisorySpeedJson.get("class").asInt());
		}

		if (AdvisorySpeedJson.get("confidence") != null) {
			String confidence = AdvisorySpeedJson.get("confidence").toString();
			JSONObject confidenceJson = new JSONObject(confidence);
			String confidenceKey = confidenceJson.keys().hasNext()
					? confidenceJson.keys().next().toString().toLowerCase().replace("-", "_")
					: "";
			if (J2735SpeedConfidence.PREC100MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC100MS);

			} else if (J2735SpeedConfidence.UNAVAILABLE.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.UNAVAILABLE);

			} else if (J2735SpeedConfidence.PREC10MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC10MS);

			} else if (J2735SpeedConfidence.PREC5MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC5MS);

			} else if (J2735SpeedConfidence.PREC1MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC1MS);

			} else if (J2735SpeedConfidence.PREC0_1MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC0_1MS);

			} else if (J2735SpeedConfidence.PREC0_05MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC0_05MS);

			} else if (J2735SpeedConfidence.PREC0_01MS.name().toLowerCase().equals(confidenceKey)) {
				advisorySpeed.setConfidence(J2735SpeedConfidence.PREC0_01MS);
			}
		}
		return advisorySpeed;
	}
}

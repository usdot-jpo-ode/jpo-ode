package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.plugin.j2735.J2735LayerType;
import us.dot.its.jpo.ode.plugin.j2735.J2735PSM;
import us.dot.its.jpo.ode.plugin.j2735.J2735PersonalDeviceUserType;
import us.dot.its.jpo.ode.plugin.j2735.J2735DataParameters;

public class PSMBuilder {
	private PSMBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735PSM genericPSM(JsonNode PSMMessage) {
		J2735PSM genericPSM = new J2735PSM();
		JsonNode basicType = PSMMessage.get("basicType");
		if (basicType != null) {
			genericPSM.setBasicType(J2735PersonalDeviceUserType.valueOf(basicType.get("basicType").fields().next().getKey().replaceAll("-", "_").toUpperCase()));
		}

		return genericPSM;
	}
}

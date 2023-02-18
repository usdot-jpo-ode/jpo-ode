package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.plugin.j2735.J2735LayerType;
import us.dot.its.jpo.ode.plugin.j2735.J2735PSM;
import us.dot.its.jpo.ode.plugin.j2735.J2735PersonalDeviceUserType;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
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

		JsonNode secMark = PSMMessage.get("secMark");
		if (secMark != null) {
			genericPSM.setSecMark(secMark.asInt());
		}

		JsonNode msgCnt = PSMMessage.get("secMark");
		if (msgCnt != null) {
			genericPSM.setMsgCnt(msgCnt.asInt());
		}

		JsonNode id = PSMMessage.get("id");
		if (id != null) {
			genericPSM.setId(id.asText());
		}

		JsonNode position = PSMMessage.get("position");
		if (position != null) {
			OdePosition3D positionObj= new OdePosition3D();
			if(position.get("lat") != null)
			{
				positionObj.setLatitude(LatitudeBuilder.genericLatitude(position.get("lat")));
			}
			
			if(position.get("long") != null)
			{
				positionObj.setLongitude(LongitudeBuilder.genericLongitude(position.get("long")));
			}
			
			if(position.get("elevation") != null)
			{
				positionObj.setElevation(ElevationBuilder.genericElevation(position.get("elevation")));
			}
			genericPSM.setPosition(positionObj);
		}

		JsonNode accuracy = PSMMessage.get("accuracy");
		if (accuracy != null) {
			genericPSM.setAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(accuracy));
		}

		JsonNode speed = PSMMessage.get("speed");
		if (speed != null) {
			genericPSM.setSpeed(speed.asInt());
		}

		JsonNode heading = PSMMessage.get("heading");
		if (heading != null) {
			genericPSM.setHeading(heading.asInt());
		}

		return genericPSM;
	}
}

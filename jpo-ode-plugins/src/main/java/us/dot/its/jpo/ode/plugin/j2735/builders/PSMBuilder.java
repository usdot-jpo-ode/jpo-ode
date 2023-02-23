package us.dot.its.jpo.ode.plugin.j2735.builders;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NumberOfParticipantsInCluster;
import us.dot.its.jpo.ode.plugin.j2735.J2735PSM;
import us.dot.its.jpo.ode.plugin.j2735.J2735PersonalAssistive;
import us.dot.its.jpo.ode.plugin.j2735.J2735PersonalDeviceUsageState;
import us.dot.its.jpo.ode.plugin.j2735.J2735PersonalDeviceUserType;
import us.dot.its.jpo.ode.plugin.j2735.J2735PublicSafetyAndRoadWorkerActivity;
import us.dot.its.jpo.ode.plugin.j2735.J2735PublicSafetyDirectingTrafficSubType;
import us.dot.its.jpo.ode.plugin.j2735.J2735PublicSafetyEventResponderWorkerType;
import us.dot.its.jpo.ode.plugin.j2735.J2735UserSizeAndBehaviour;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735AnimalType;
import us.dot.its.jpo.ode.plugin.j2735.J2735Attachment;

public class PSMBuilder {
	private PSMBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735PSM genericPSM(JsonNode PSMMessage) {
		J2735PSM genericPSM = new J2735PSM();
		JsonNode basicType = PSMMessage.get("basicType");
		if (basicType != null) {
			genericPSM.setBasicType(J2735PersonalDeviceUserType.valueOf(basicType.fields().next().getKey()));
		}

		JsonNode secMark = PSMMessage.get("secMark");
		if (secMark != null) {
			genericPSM.setSecMark(secMark.asInt());
		}

		JsonNode msgCnt = PSMMessage.get("msgCnt");
		if (msgCnt != null) {
			genericPSM.setMsgCnt(msgCnt.asInt());
		}

		JsonNode id = PSMMessage.get("id");
		if (id != null) {
			genericPSM.setId(id.asText().replaceAll("\\s", ""));
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

		// Optional Parameters Begins here:
		JsonNode accelSet = PSMMessage.get("accelSet");
		if (accelSet != null) {
			genericPSM.setAccelSet(AccelerationSet4WayBuilder.genericAccelerationSet4Way(accelSet));
		}

		JsonNode pathHistory = PSMMessage.get("pathHistory");
		if (pathHistory != null) {
			genericPSM.setPathHistory(PathHistoryBuilder.genericPathHistory(pathHistory));
		}

		JsonNode propulsion = PSMMessage.get("propulsion");
		if (propulsion != null) {
			genericPSM.setPropulsion(PropelledInformationBuilder.genericPropelledInformation(propulsion));
		}

		JsonNode useState = PSMMessage.get("useState");
		if (useState != null) {
			genericPSM.setUseState(BitStringBuilder.genericBitString(useState,J2735PersonalDeviceUsageState.values()));
		}

		JsonNode crossRequest = PSMMessage.get("crossRequest");
		if (crossRequest != null) {
			genericPSM.setCrossRequest(crossRequest.asBoolean());
		}

		JsonNode crossState = PSMMessage.get("crossState");
		if (crossState != null) {
			genericPSM.setCrossState(crossState.asBoolean());
		}

		JsonNode clusterSize = PSMMessage.get("clusterSize");
		if (crossState != null) {
			genericPSM.setClusterSize(J2735NumberOfParticipantsInCluster.valueOf(clusterSize.fields().next().getKey().toUpperCase()));
		}

		JsonNode clusterRadius = PSMMessage.get("clusterRadius");
		if (clusterRadius != null) {
			genericPSM.setClusterRadius(clusterRadius.asInt());
		}

		JsonNode eventResponderType = PSMMessage.get("eventResponderType");
		if (eventResponderType != null) {
			genericPSM.setEventResponderType(J2735PublicSafetyEventResponderWorkerType.valueOf(eventResponderType.fields().next().getKey().toUpperCase()));
		}

		JsonNode activityType = PSMMessage.get("activityType");
		if (activityType != null) {
			genericPSM.setActivityType(BitStringBuilder.genericBitString(activityType,J2735PublicSafetyAndRoadWorkerActivity.values()));
		}

		JsonNode activitySubType = PSMMessage.get("activitySubType");
		if (activitySubType != null) {
			genericPSM.setActivitySubType(BitStringBuilder.genericBitString(activitySubType,J2735PublicSafetyDirectingTrafficSubType.values()));
		}

		JsonNode assistType = PSMMessage.get("assistType");
		if (assistType != null) {
			genericPSM.setAssistType(BitStringBuilder.genericBitString(assistType,J2735PersonalAssistive.values()));
		}

		JsonNode sizing = PSMMessage.get("sizing");
		if (sizing != null) {
			genericPSM.setSizing(BitStringBuilder.genericBitString(sizing,J2735UserSizeAndBehaviour.values()));
		}

		JsonNode attachment = PSMMessage.get("attachment");
		if (attachment != null) {
			genericPSM.setAttachment(J2735Attachment.valueOf(attachment.fields().next().getKey().toUpperCase()));
		}

		JsonNode attachmentRadius = PSMMessage.get("attachmentRadius");
		if (attachmentRadius != null) {
			genericPSM.setAttachmentRadius(attachmentRadius.asInt());
		}

		JsonNode animalType = PSMMessage.get("animalType");
		if (animalType != null) {
			genericPSM.setAnimalType(J2735AnimalType.valueOf(animalType.fields().next().getKey().toUpperCase()));
		}

		return genericPSM;
	}
}

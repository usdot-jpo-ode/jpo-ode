package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PSM extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// REQUIRED parameters
	private J2735PersonalDeviceUserType basicType;
	private Integer secMark;
	private Integer msgCnt;
	private String id; // Octet String
	private OdePosition3D position;
	private J2735PositionalAccuracy accuracy;
	private Integer speed;
	private Integer heading;

	// OPTIONAL parameters
	private J2735AccelerationSet4Way accelSet;
	private J2735PathHistory pathHistory;
	private J2735PropelledInformation propulsion;
	private J2735BitString useState;
	private Boolean crossRequest;
	private Boolean crossState;
	private J2735NumberOfParticipantsInCluster clusterSize;
	private Integer clusterRadius;
	private J2735PublicSafetyEventResponderWorkerType eventResponderType;
	private J2735BitString activityType;
	private J2735BitString activitySubType;
	private J2735BitString assistType;
	private J2735BitString sizing;
	private J2735Attachment attachment;
	private Integer attachmentRadius;
	private J2735AnimalType animalType;

	public J2735PersonalDeviceUserType getBasicType() {
		return this.basicType;
	}

	public void setBasicType(J2735PersonalDeviceUserType basicType) {
		this.basicType = basicType;
	}

	public Integer getSecMark() {
		return this.secMark;
	}

	public void setSecMark(Integer secMark) {
		this.secMark = secMark;
	}

	public Integer getMsgCnt() {
		return this.msgCnt;
	}

	public void setMsgCnt(Integer msgCnt) {
		this.msgCnt = msgCnt;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OdePosition3D getPosition() {
		return this.position;
	}

	public void setPosition(OdePosition3D position) {
		this.position = position;
	}

	public J2735PositionalAccuracy getAccuracy() {
		return this.accuracy;
	}

	public void setAccuracy(J2735PositionalAccuracy accuracy) {
		this.accuracy = accuracy;
	}

	public Integer getSpeed() {
		return this.speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Integer getHeading() {
		return this.heading;
	}

	public void setHeading(Integer heading) {
		this.heading = heading;
	}

	public J2735AccelerationSet4Way getAccelSet() {
		return this.accelSet;
	}

	public void setAccelSet(J2735AccelerationSet4Way accelSet) {
		this.accelSet = accelSet;
	}

	public J2735PathHistory getPathHistory() {
		return this.pathHistory;
	}

	public void setPathHistory(J2735PathHistory pathHistory) {
		this.pathHistory = pathHistory;
	}

	public J2735PropelledInformation getPropulsion() {
		return this.propulsion;
	}

	public void setPropulsion(J2735PropelledInformation propulsion) {
		this.propulsion = propulsion;
	}

	public J2735BitString getUseState() {
		return this.useState;
	}

	public void setUseState(J2735BitString useState) {
		this.useState = useState;
	}

	public Boolean isCrossRequest() {
		return this.crossRequest;
	}

	public Boolean getCrossRequest() {
		return this.crossRequest;
	}

	public void setCrossRequest(Boolean crossRequest) {
		this.crossRequest = crossRequest;
	}

	public Boolean isCrossState() {
		return this.crossState;
	}

	public Boolean getCrossState() {
		return this.crossState;
	}

	public void setCrossState(Boolean crossState) {
		this.crossState = crossState;
	}

	public J2735NumberOfParticipantsInCluster getClusterSize() {
		return this.clusterSize;
	}

	public void setClusterSize(J2735NumberOfParticipantsInCluster clusterSize) {
		this.clusterSize = clusterSize;
	}

	public Integer getClusterRadius() {
		return this.clusterRadius;
	}

	public void setClusterRadius(Integer clusterRadius) {
		this.clusterRadius = clusterRadius;
	}

	public J2735PublicSafetyEventResponderWorkerType getEventResponderType() {
		return this.eventResponderType;
	}

	public void setEventResponderType(J2735PublicSafetyEventResponderWorkerType eventResponderType) {
		this.eventResponderType = eventResponderType;
	}

	public J2735BitString getActivityType() {
		return this.activityType;
	}

	public void setActivityType(J2735BitString activityType) {
		this.activityType = activityType;
	}

	public J2735BitString getActivitySubType() {
		return this.activitySubType;
	}

	public void setActivitySubType(J2735BitString activitySubType) {
		this.activitySubType = activitySubType;
	}

	public J2735BitString getAssistType() {
		return this.assistType;
	}

	public void setAssistType(J2735BitString assistType) {
		this.assistType = assistType;
	}

	public J2735BitString getSizing() {
		return this.sizing;
	}

	public void setSizing(J2735BitString sizing) {
		this.sizing = sizing;
	}

	public J2735Attachment getAttachment() {
		return this.attachment;
	}

	public void setAttachment(J2735Attachment attachment) {
		this.attachment = attachment;
	}

	public Integer getAttachmentRadius() {
		return this.attachmentRadius;
	}

	public void setAttachmentRadius(Integer attachmentRadius) {
		this.attachmentRadius = attachmentRadius;
	}

	public J2735AnimalType getAnimalType() {
		return this.animalType;
	}

	public void setAnimalType(J2735AnimalType animalType) {
		this.animalType = animalType;
	}

}

package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735IntersectionState extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private J2735IntersectionReferenceID id;
	private Integer revision;
	private J2735IntersectionStatusObject status;
	private Integer moy;
	private Integer timeStamp;
	private J2735EnableLaneList enabledLanes;
	private J2735MovementList states;
	private J2735ManeuverAssistList maneuverAssistList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public J2735IntersectionReferenceID getId() {
		return id;
	}

	public void setId(J2735IntersectionReferenceID id) {
		this.id = id;
	}

	public Integer getRevision() {
		return revision;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	public J2735IntersectionStatusObject getStatus() {
		return status;
	}

	public void setStatus(J2735IntersectionStatusObject status) {
		this.status = status;
	}

	public Integer getMoy() {
		return moy;
	}

	public void setMoy(Integer moy) {
		this.moy = moy;
	}

	public Integer getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}

	public J2735EnableLaneList getEnabledLanes() {
		return enabledLanes;
	}

	public void setEnabledLanes(J2735EnableLaneList enabledLanes) {
		this.enabledLanes = enabledLanes;
	}

	public J2735MovementList getStates() {
		return states;
	}

	public void setStates(J2735MovementList states) {
		this.states = states;
	}

	public J2735ManeuverAssistList getManeuverAssistList() {
		return maneuverAssistList;
	}

	public void setManeuverAssistList(J2735ManeuverAssistList maneuverAssistList) {
		this.maneuverAssistList = maneuverAssistList;
	}

}

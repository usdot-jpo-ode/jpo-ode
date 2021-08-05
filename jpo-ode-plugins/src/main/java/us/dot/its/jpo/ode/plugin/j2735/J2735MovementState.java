package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MovementState extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private String movementName;
	private Integer signalGroup;
	private J2735MovementEventList state_time_speed;
	private J2735ManeuverAssistList maneuverAssistList;

	public String getMovementName() {
		return movementName;
	}

	public void setMovementName(String movementName) {
		this.movementName = movementName;
	}

	public Integer getSignalGroup() {
		return signalGroup;
	}

	public void setSignalGroup(Integer signalGroup) {
		this.signalGroup = signalGroup;
	}

	public J2735MovementEventList getState_time_speed() {
		return state_time_speed;
	}

	public void setState_time_speed(J2735MovementEventList state_time_speed) {
		this.state_time_speed = state_time_speed;
	}

	public J2735ManeuverAssistList getManeuverAssistList() {
		return maneuverAssistList;
	}

	public void setManeuverAssistList(J2735ManeuverAssistList maneuverAssistList) {
		this.maneuverAssistList = maneuverAssistList;
	}

	// regional

}

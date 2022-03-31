package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MovementEvent extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private J2735MovementPhaseState eventState;
	private J2735TimeChangeDetails timing;
	private J2735AdvisorySpeedList speeds;

	public J2735MovementPhaseState getEventState() {
		return eventState;
	}

	public void setEventState(J2735MovementPhaseState eventState) {
		this.eventState = eventState;
	}

	public J2735TimeChangeDetails getTiming() {
		return timing;
	}

	public void setTiming(J2735TimeChangeDetails timing) {
		this.timing = timing;
	}

	public J2735AdvisorySpeedList getSpeeds() {
		return speeds;
	}

	public void setSpeeds(J2735AdvisorySpeedList speeds) {
		this.speeds = speeds;
	}

	// regional
}

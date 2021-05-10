package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AdvisorySpeed extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private J2735AdvisorySpeedType type;
	private Integer speed;
	private J2735SpeedConfidence confidence;
	private Integer distance;
	private Integer classId;

	// regional
	public J2735AdvisorySpeedType getType() {
		return type;
	}

	public void setType(J2735AdvisorySpeedType type) {
		this.type = type;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public J2735SpeedConfidence getConfidence() {
		return confidence;
	}

	public void setConfidence(J2735SpeedConfidence confidence) {
		this.confidence = confidence;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

}

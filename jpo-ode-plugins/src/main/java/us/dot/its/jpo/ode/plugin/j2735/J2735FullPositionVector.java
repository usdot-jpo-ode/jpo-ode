package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735FullPositionVector extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735Position3D position;
	private BigDecimal heading;
	private J2735PositionalAccuracy posAccuracy;
	private J2735PositionConfidenceSet posConfidence;
	private J2735TransmissionAndSpeed speed;
	private J2735SpeedandHeadingandThrottleConfidence speedConfidence;
	private J2735TimeConfidence timeConfidence;
	private J2735DDateTime utcTime;

	public J2735Position3D getPosition() {
		return position;
	}

	public void setPosition(J2735Position3D position) {
		this.position = position;
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	public J2735PositionalAccuracy getPosAccuracy() {
		return posAccuracy;
	}

	public void setPosAccuracy(J2735PositionalAccuracy posAccuracy) {
		this.posAccuracy = posAccuracy;
	}

	public J2735PositionConfidenceSet getPosConfidence() {
		return posConfidence;
	}

	public void setPosConfidence(J2735PositionConfidenceSet posConfidence) {
		this.posConfidence = posConfidence;
	}

	public J2735TransmissionAndSpeed getSpeed() {
		return speed;
	}

	public void setSpeed(J2735TransmissionAndSpeed speed) {
		this.speed = speed;
	}

	public J2735SpeedandHeadingandThrottleConfidence getSpeedConfidence() {
		return speedConfidence;
	}

	public void setSpeedConfidence(J2735SpeedandHeadingandThrottleConfidence speedConfidence) {
		this.speedConfidence = speedConfidence;
	}

	public J2735TimeConfidence getTimeConfidence() {
		return timeConfidence;
	}

	public void setTimeConfidence(J2735TimeConfidence timeConfidence) {
		this.timeConfidence = timeConfidence;
	}

	public J2735DDateTime getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(J2735DDateTime utcTime) {
		this.utcTime = utcTime;
	}

}

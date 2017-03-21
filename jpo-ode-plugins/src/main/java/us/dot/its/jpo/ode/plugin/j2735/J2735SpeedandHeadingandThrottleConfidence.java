package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpeedandHeadingandThrottleConfidence extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735HeadingConfidence heading;
	private J2735SpeedConfidence speed;
	private J2735ThrottleConfidence throttle;

	public J2735HeadingConfidence getHeading() {
		return heading;
	}

	public void setHeading(J2735HeadingConfidence heading) {
		this.heading = heading;
	}

	public J2735SpeedConfidence getSpeed() {
		return speed;
	}

	public void setSpeed(J2735SpeedConfidence speed) {
		this.speed = speed;
	}

	public J2735ThrottleConfidence getThrottle() {
		return throttle;
	}

	public void setThrottle(J2735ThrottleConfidence throttle) {
		this.throttle = throttle;
	}

}

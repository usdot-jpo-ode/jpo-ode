package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735TransmissionState {
	neutral, // Neutral, speed relative to the vehicle alignment
	park, // Park, speed relative the to vehicle alignment
	forwardGears, // Forward gears, speed relative the to vehicle alignment
	reverseGears, // Reverse gears, speed relative the to vehicle alignment
	reserved1, reserved2, reserved3, unavailable; // not-equipped or unavailable
													// value,
}

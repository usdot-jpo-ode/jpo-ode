package us.dot.its.jpo.ode.common.asn1;

import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class TransmissionAndSpeedHelper {
	
	/**
	 * Create transmission and speed element, assuming forward movement
	 * @param speedMph speed in miles per hour
	 * @return newly created transmission and speed element
	 */
	public static TransmissionAndSpeed createTransmissionAndSpeed(double speedMph) {
		// Velocity 0 .. 8191 -- Units of 0.02 m/s (meters per second)
		// 8191 -- unavailable
		// 1 mph == 0.44704 m/s, 0.44704/0.02 = 22.352
		int units = Math.round((float)((speedMph * 22.352)));
		return new  TransmissionAndSpeed(TransmissionState.forwardGears, new Velocity(units));
	}
	
	/**
	 * Get speed in MPH
	 * @param transmissionAndSpeed J2735 TransmissionAndSpeed value
	 * @return null if speed information is unavailable, positive seed if the vehicle is moving forward, and negative otherwise
	 */
	public static Double getSpeedMph(TransmissionAndSpeed transmissionAndSpeed) {
		Velocity speed = transmissionAndSpeed.getSpeed();

		int units = speed.intValue();
		if ( units == 8191 )
			return null;
		
		TransmissionState transmissionState = transmissionAndSpeed.getTransmisson();
		double value = units * .02 * 2.23694;
		if ( transmissionState != TransmissionState.forwardGears )
			value = -value;
		return value;
	}

}

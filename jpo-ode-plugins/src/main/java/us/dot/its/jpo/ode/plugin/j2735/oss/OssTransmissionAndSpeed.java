package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class OssTransmissionAndSpeed {

	public static J2735TransmissionAndSpeed genericTransmissionAndSpeed(TransmissionAndSpeed ts) {
		J2735TransmissionAndSpeed gts = new J2735TransmissionAndSpeed();

		gts.speed = OssSpeedOrVelocity.genericVelocity(ts.speed);
		gts.transmisson = J2735TransmissionState.values()[ts.transmisson.indexOf()];
		
		return gts ;
	}

}

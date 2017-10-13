package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class OssTransmissionAndSpeed {

    private static final long TRANSMISSION_LOWER_BOUND = 0L;
    private static final long TRANSMISSION_UPPER_BOUND = 7L;
    
    private OssTransmissionAndSpeed() {
       throw new UnsupportedOperationException();
    }

    public static J2735TransmissionAndSpeed genericTransmissionAndSpeed(TransmissionAndSpeed ts) {

        if (ts.transmisson.longValue() < TRANSMISSION_LOWER_BOUND
                || ts.transmisson.longValue() > TRANSMISSION_UPPER_BOUND) {
            throw new IllegalArgumentException("Transmission value out of bounds [0..7]");
        }

        J2735TransmissionAndSpeed gts = new J2735TransmissionAndSpeed();

        gts.setSpeed(OssSpeedOrVelocity.genericVelocity(ts.speed));
        gts.setTransmisson(J2735TransmissionState.values()[ts.transmisson.indexOf()]);

        return gts;
    }

}

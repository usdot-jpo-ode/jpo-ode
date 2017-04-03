package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

public class OssWiperSet {

    private static final long STATUS_LOWER_BOUND = 0L;
    private static final long STATUS_UPPER_BOUND = 6L;
    private static final Integer RATE_LOWER_BOUND = 0;
    private static final Integer RATE_UPPER_BOUND = 127;
    
    private OssWiperSet() {
       throw new UnsupportedOperationException();
    }

    public static J2735WiperSet genericWiperSet(WiperSet ws) {

        if (ws.statusFront.longValue() < STATUS_LOWER_BOUND || ws.statusFront.longValue() > STATUS_UPPER_BOUND) {
            throw new IllegalArgumentException("Front wiper status out of bounds [0..127]");
        }

        if (ws.rateFront.intValue() < RATE_LOWER_BOUND || ws.rateFront.intValue() > RATE_UPPER_BOUND) {
            throw new IllegalArgumentException("Front wiper rate out of bounds [0..6]");
        }

        if (ws.statusRear.longValue() < STATUS_LOWER_BOUND || ws.statusRear.longValue() > STATUS_UPPER_BOUND) {
            throw new IllegalArgumentException("Rear wiper status value out of bounds [0..127]");
        }

        if (ws.rateRear.intValue() < RATE_LOWER_BOUND || ws.rateRear.intValue() > RATE_UPPER_BOUND) {
            throw new IllegalArgumentException("Rear wiper rate out of bounds [0..6]");
        }

        J2735WiperSet gws = new J2735WiperSet();

        gws.setRateFront(ws.rateFront.intValue());
        gws.setRateRear(ws.rateRear.intValue());
        gws.setStatusFront(J2735WiperStatus.values()[ws.statusFront.indexOf()]);
        gws.setStatusRear(J2735WiperStatus.values()[ws.statusRear.indexOf()]);

        return gws;
    }

}

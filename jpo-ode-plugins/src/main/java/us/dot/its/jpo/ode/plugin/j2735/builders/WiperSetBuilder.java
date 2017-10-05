package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

public class WiperSetBuilder {

    private static final long STATUS_LOWER_BOUND = 0L;
    private static final long STATUS_UPPER_BOUND = 6L;
    private static final Integer RATE_LOWER_BOUND = 0;
    private static final Integer RATE_UPPER_BOUND = 127;
    
    private WiperSetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735WiperSet genericWiperSet(JsonNode wiperSet) {

        if (wiperSet.statusFront.longValue() < STATUS_LOWER_BOUND || wiperSet.statusFront.longValue() > STATUS_UPPER_BOUND) {
            throw new IllegalArgumentException("Front wiper status out of bounds [0..127]");
        }

        if (wiperSet.rateFront.intValue() < RATE_LOWER_BOUND || wiperSet.rateFront.intValue() > RATE_UPPER_BOUND) {
            throw new IllegalArgumentException("Front wiper rate out of bounds [0..6]");
        }

        if (wiperSet.statusRear.longValue() < STATUS_LOWER_BOUND || wiperSet.statusRear.longValue() > STATUS_UPPER_BOUND) {
            throw new IllegalArgumentException("Rear wiper status value out of bounds [0..127]");
        }

        if (wiperSet.rateRear.intValue() < RATE_LOWER_BOUND || wiperSet.rateRear.intValue() > RATE_UPPER_BOUND) {
            throw new IllegalArgumentException("Rear wiper rate out of bounds [0..6]");
        }

        J2735WiperSet gws = new J2735WiperSet();

        gws.setRateFront(wiperSet.rateFront.intValue());
        gws.setRateRear(wiperSet.rateRear.intValue());
        gws.setStatusFront(J2735WiperStatus.values()[wiperSet.statusFront.indexOf()]);
        gws.setStatusRear(J2735WiperStatus.values()[wiperSet.statusRear.indexOf()]);

        return gws;
    }

}

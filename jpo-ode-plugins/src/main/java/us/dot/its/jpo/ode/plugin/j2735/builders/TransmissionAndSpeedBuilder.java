package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class TransmissionAndSpeedBuilder {

    private static final long TRANSMISSION_LOWER_BOUND = 0L;
    private static final long TRANSMISSION_UPPER_BOUND = 7L;
    
    private TransmissionAndSpeedBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TransmissionAndSpeed genericTransmissionAndSpeed(JsonNode ts) {

        if (ts.get("transmission").asLong() < TRANSMISSION_LOWER_BOUND
                || ts.get("transmission").asLong() > TRANSMISSION_UPPER_BOUND) {
            throw new IllegalArgumentException("Transmission value out of bounds [0..7]");
        }

        J2735TransmissionAndSpeed gts = new J2735TransmissionAndSpeed();

        gts.setSpeed(SpeedOrVelocityBuilder.genericVelocity(ts.get("speed")));
        gts.setTransmisson(J2735TransmissionState.valueOf(ts.get("transmisson").asText().toUpperCase()));

        return gts;
    }

}

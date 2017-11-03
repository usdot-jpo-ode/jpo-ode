package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;

public class PositionalAccuracyBuilder {
    
    private PositionalAccuracyBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735PositionalAccuracy genericPositionalAccuracy(JsonNode positionalAccuracy) {

        if (positionalAccuracy.get("semiMajor").asInt() < 0 || positionalAccuracy.get("semiMajor").asInt() > 255) {
            throw new IllegalArgumentException("SemiMajorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get("semiMinor").asInt() < 0 || positionalAccuracy.get("semiMinor").asInt() > 255) {
            throw new IllegalArgumentException("SemiMinorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get("orientation").asInt() < 0 || positionalAccuracy.get("orientation").asInt() > 65535) {
            throw new IllegalArgumentException("SemiMajorOrientation value out of bounds");
        }

        J2735PositionalAccuracy genericPositionalAccuracy = new J2735PositionalAccuracy();

        if (positionalAccuracy.get("semiMajor").asInt() != 255) {
            genericPositionalAccuracy.setSemiMajor(BigDecimal.valueOf(positionalAccuracy.get("semiMajor").asInt() * (long)5, 2));
        }

        if (positionalAccuracy.get("semiMinor").asInt() != 255) {
            genericPositionalAccuracy.setSemiMinor(BigDecimal.valueOf(positionalAccuracy.get("semiMinor").asInt() * (long)5, 2));
        }

        if (positionalAccuracy.get("orientation").asInt() != 65535) {

            genericPositionalAccuracy.setOrientation(BigDecimal
                    .valueOf((0.0054932479) * (double) (positionalAccuracy.get("orientation").asLong()))
                    .setScale(10, RoundingMode.HALF_EVEN));

        }
        return genericPositionalAccuracy;

    }

}

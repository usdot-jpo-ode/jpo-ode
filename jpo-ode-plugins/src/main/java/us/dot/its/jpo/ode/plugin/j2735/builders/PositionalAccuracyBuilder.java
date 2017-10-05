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

        if (positionalAccuracy.get("semiMajor").intValue() < 0 || positionalAccuracy.get("semiMajor").intValue() > 255) {
            throw new IllegalArgumentException("SemiMajorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get("semiMinor").intValue() < 0 || positionalAccuracy.get("semiMinor").intValue() > 255) {
            throw new IllegalArgumentException("SemiMinorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get("orientation").intValue() < 0 || positionalAccuracy.get("orientation").intValue() > 65535) {
            throw new IllegalArgumentException("SemiMajorOrientation value out of bounds");
        }

        J2735PositionalAccuracy genericPositionalAccuracy = new J2735PositionalAccuracy();

        if (positionalAccuracy.get("semiMajor").intValue() != 255) {
            genericPositionalAccuracy.setSemiMajor(BigDecimal.valueOf(positionalAccuracy.get("semiMajor").intValue() * (long)5, 2));
        }

        if (positionalAccuracy.get("semiMinor").intValue() != 255) {
            genericPositionalAccuracy.setSemiMinor(BigDecimal.valueOf(positionalAccuracy.get("semiMinor").intValue() * (long)5, 2));
        }

        if (positionalAccuracy.get("orientation").intValue() != 65535) {

            genericPositionalAccuracy.setOrientation(BigDecimal
                    .valueOf((0.0054932479) * (double) (positionalAccuracy.get("orientation").longValue()))
                    .setScale(10, RoundingMode.HALF_EVEN));

        }
        return genericPositionalAccuracy;

    }

}

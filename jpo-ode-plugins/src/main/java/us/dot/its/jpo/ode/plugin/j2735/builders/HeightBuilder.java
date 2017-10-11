package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class HeightBuilder {

    private HeightBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericHeight(JsonNode height) {

        if (height.asInt() < 0 || height.asInt() > 127) {
            throw new IllegalArgumentException("Vehicle height out of bounds");
        }

        return BigDecimal.valueOf(height.asInt() * (long) 5, 2);
    }

}

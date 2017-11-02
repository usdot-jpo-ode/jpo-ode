package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class HeadingBuilder {
    
    public static BigDecimal genericHeading(JsonNode heading) {
        return AngleBuilder.longToDecimal(heading.asLong()).setScale(1);
    }

    public static BigDecimal genericCoarseHeading(JsonNode coarseHeading) {

        if (coarseHeading.asInt() < 0 || coarseHeading.asInt() > 240) {
            throw new IllegalArgumentException("Coarse heading value out of bounds");
        }

        BigDecimal result = null;

        if (coarseHeading.asInt() != 240) {
            result = BigDecimal.valueOf(coarseHeading.asLong() * 15, 1).setScale(1);
        }

        return result;
    }

}

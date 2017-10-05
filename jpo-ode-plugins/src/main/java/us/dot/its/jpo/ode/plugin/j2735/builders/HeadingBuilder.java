package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class HeadingBuilder {
    
    public static BigDecimal genericHeading(JsonNode heading) {
        return AngleBuilder.longToDecimal(heading.longValue());
    }

    public static BigDecimal genericCoarseHeading(JsonNode coarseHeading) {

        if (coarseHeading.intValue() < 0 || coarseHeading.intValue() > 240) {
            throw new IllegalArgumentException("Coarse heading value out of bounds");
        }

        BigDecimal result = null;

        if (coarseHeading.intValue() != 240) {
            result = BigDecimal.valueOf(coarseHeading.longValue() * 15, 1);
        }

        return result;
    }

}

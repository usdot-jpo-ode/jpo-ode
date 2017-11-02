package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class TimeOffsetBuilder {

    private static final Integer TIME_OFFSET_LOWER_BOUND = 1;
    private static final Integer TIME_OFFSET_UPPER_BOUND = 65535;
    
    private TimeOffsetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericTimeOffset(JsonNode timeOffset) {

        BigDecimal result;

        if (timeOffset.asInt() == 65535) {
            result = null;
        } else if (timeOffset.asInt() < TIME_OFFSET_LOWER_BOUND) {
            throw new IllegalArgumentException("Time offset value below lower bound [1]");
        } else if (timeOffset.asInt() > TIME_OFFSET_UPPER_BOUND) {
            result = BigDecimal.valueOf(655.34);
        } else {
            result = BigDecimal.valueOf(timeOffset.longValue(), 2);
        }

        return result;
    }

}

package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;

public class OssTimeOffset {

    private static final Integer TIME_OFFSET_LOWER_BOUND = 1;
    private static final Integer TIME_OFFSET_UPPER_BOUND = 65535;

    public static BigDecimal genericTimeOffset(TimeOffset timeOffset) {

        BigDecimal result;

        if (timeOffset.intValue() == 65535) {
            result = null;
        } else if (timeOffset.intValue() < TIME_OFFSET_LOWER_BOUND) {
            throw new IllegalArgumentException("Time offset value below lower bound [1]");
        } else if (timeOffset.intValue() > TIME_OFFSET_UPPER_BOUND) {
            result = BigDecimal.valueOf(655.34);
        } else {
            result = BigDecimal.valueOf(timeOffset.longValue(), 2);
        }

        return result;
    }

}

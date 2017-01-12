package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.CoarseHeading;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;

public class OssHeading {

    public static BigDecimal genericHeading(Heading heading) {
        return OssAngle.longToDecimal(heading.longValue());
    }

    public static BigDecimal genericHeading(CoarseHeading heading) {

        if (heading.intValue() < 0 || heading.intValue() > 240) {
            throw new IllegalArgumentException("Coarse heading value out of bounds");
        }

        BigDecimal result = null;

        if (heading.intValue() != 240) {
            result = BigDecimal.valueOf(heading.longValue() * 15, 1);
        }

        return result;
    }

}

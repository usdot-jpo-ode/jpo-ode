package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PivotPointDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735PivotPointDescription;

public class OssPivotPointDescription {

    private static final Integer PIVOT_OFFSET_LOWER_BOUND = -1024;
    private static final Integer PIVOT_OFFSET_UPPER_BOUND = 1023;

    public static J2735PivotPointDescription genericPivotPointDescription(PivotPointDescription ppd) {
        J2735PivotPointDescription gppd = new J2735PivotPointDescription();

        if (ppd.pivotOffset.intValue() < PIVOT_OFFSET_LOWER_BOUND
                || ppd.pivotOffset.intValue() > PIVOT_OFFSET_UPPER_BOUND) {
            throw new IllegalArgumentException("Pivot offset value out of bounds [-1024..1023]");
        } else if (ppd.pivotOffset.intValue() == -1024) {
            ppd.pivotOffset = null;
        } else {
            gppd.pivotOffset = BigDecimal.valueOf(ppd.pivotOffset.intValue(), 2);
        }

        gppd.pivotAngle = OssAngle.genericAngle(ppd.pivotAngle);
        gppd.pivots = ppd.pivots.booleanValue();

        return gppd;
    }

}

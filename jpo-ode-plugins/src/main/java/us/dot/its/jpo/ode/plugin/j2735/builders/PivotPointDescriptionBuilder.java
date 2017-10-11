package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PivotPointDescription;

public class PivotPointDescriptionBuilder {

    private static final Integer PIVOT_OFFSET_LOWER_BOUND = -1024;
    private static final Integer PIVOT_OFFSET_UPPER_BOUND = 1023;
    
    private PivotPointDescriptionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735PivotPointDescription genericPivotPointDescription(JsonNode ppd) {
        J2735PivotPointDescription gppd = new J2735PivotPointDescription();

        if (ppd.get("pivotOffset").intValue() < PIVOT_OFFSET_LOWER_BOUND
                || ppd.get("pivotOffset").intValue() > PIVOT_OFFSET_UPPER_BOUND) {
            throw new IllegalArgumentException("Pivot offset value out of bounds [-1024.1023]");
        } else if (ppd.get("pivotOffset").intValue() == -1024) {
            gppd.setPivotOffset(null);
        } else {
            gppd.setPivotOffset(BigDecimal.valueOf(ppd.get("pivotOffset").intValue(), 2));
        }

        gppd.setPivotAngle(AngleBuilder.genericAngle(ppd.get("pivotAngle")));
        gppd.setPivots(ppd.get("pivots").asBoolean());

        return gppd;
    }

}

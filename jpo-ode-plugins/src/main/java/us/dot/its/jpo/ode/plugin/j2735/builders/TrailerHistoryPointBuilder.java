package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;

public class TrailerHistoryPointBuilder {

    private TrailerHistoryPointBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerHistoryPoint genericTrailerHistoryPoint(JsonNode thp) {
        J2735TrailerHistoryPoint gthp = new J2735TrailerHistoryPoint();

        gthp.setElevationOffset(OffsetBuilder.genericVertOffset_B07(thp.get("elevationOffset")));
        gthp.setHeading(HeadingBuilder.genericHeading(thp.get("heading")));
        gthp.setPivotAngle(AngleBuilder.genericAngle(thp.get("pivotAngle")));
        gthp.setPositionOffset(Node_XYBuilder.genericNode_XY(thp.get("positionOffset")));
        gthp.setTimeOffset(TimeOffsetBuilder.genericTimeOffset(thp.get("timeOffset")));

        return gthp;
    }

}

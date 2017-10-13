package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;

public class OssTrailerHistoryPoint {

    private OssTrailerHistoryPoint() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerHistoryPoint genericTrailerHistoryPoint(TrailerHistoryPoint thp) {
        J2735TrailerHistoryPoint gthp = new J2735TrailerHistoryPoint();

        gthp.setElevationOffset(OssOffset.genericOffset(thp.elevationOffset));
        gthp.setHeading(OssHeading.genericHeading(thp.heading));
        gthp.setPivotAngle(OssAngle.genericAngle(thp.pivotAngle));
        gthp.setPositionOffset(OssNode_XY.genericNode_XY(thp.positionOffset));
        gthp.setTimeOffset(OssTimeOffset.genericTimeOffset(thp.timeOffset));

        return gthp;
    }

}

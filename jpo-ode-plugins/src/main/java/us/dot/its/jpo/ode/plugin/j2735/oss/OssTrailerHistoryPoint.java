package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;

public class OssTrailerHistoryPoint {

    private OssTrailerHistoryPoint() {
    }

    public static J2735TrailerHistoryPoint genericTrailerHistoryPoint(TrailerHistoryPoint thp) {
        J2735TrailerHistoryPoint gthp = new J2735TrailerHistoryPoint();

        gthp.elevationOffset = OssOffset.genericOffset(thp.elevationOffset);
        gthp.heading = OssHeading.genericHeading(thp.heading);
        gthp.pivotAngle = OssAngle.genericAngle(thp.pivotAngle);
        gthp.positionOffset = OssNode_XY.genericNode_XY(thp.positionOffset);
        gthp.timeOffset = OssTimeOffset.genericTimeOffset(thp.timeOffset);

        return gthp;
    }

}

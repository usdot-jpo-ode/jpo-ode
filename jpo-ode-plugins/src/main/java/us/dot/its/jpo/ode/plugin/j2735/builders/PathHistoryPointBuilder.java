package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class OssPathHistoryPoint {
    
    private OssPathHistoryPoint() {
       throw new UnsupportedOperationException();
    }

    public static J2735PathHistoryPoint genericPathHistoryPoint(PathHistoryPoint pathHistoryPoint) {

        J2735PathHistoryPoint php = new J2735PathHistoryPoint();

        // Required elements
        if (pathHistoryPoint.latOffset.longValue() == -131072) {
            php.setLatOffset(null);
        } else if (pathHistoryPoint.latOffset.longValue() < -131072) {
            php.setLatOffset(BigDecimal.valueOf(-0.0131071));
        } else if (pathHistoryPoint.latOffset.longValue() > 131071) {
            php.setLatOffset(BigDecimal.valueOf(0.0131071));
        } else {
            php.setLatOffset(BigDecimal.valueOf(pathHistoryPoint.latOffset.longValue(), 7));
        }

        if (pathHistoryPoint.lonOffset.longValue() == -131072) {
            php.setLonOffset(null);
        } else if (pathHistoryPoint.lonOffset.longValue() < -131072) {
            php.setLonOffset(BigDecimal.valueOf(-0.0131071));
        } else if (pathHistoryPoint.lonOffset.longValue() > 131071) {
            php.setLonOffset(BigDecimal.valueOf(0.0131071));
        } else {
            php.setLonOffset(BigDecimal.valueOf(pathHistoryPoint.lonOffset.longValue(), 7));
        }

        if (pathHistoryPoint.elevationOffset.longValue() == -2048) {
            php.setElevationOffset(null);
        } else if (pathHistoryPoint.elevationOffset.longValue() < -2048) {
            php.setElevationOffset(BigDecimal.valueOf(-204.7));
        } else if (pathHistoryPoint.elevationOffset.longValue() > 2047) {
            php.setElevationOffset(BigDecimal.valueOf(204.7));
        } else {
            php.setElevationOffset(BigDecimal.valueOf(pathHistoryPoint.elevationOffset.longValue(), 1));
        }

        php.setTimeOffset(OssTimeOffset.genericTimeOffset(pathHistoryPoint.timeOffset));

        // Optional elements
        if (pathHistoryPoint.speed != null) {
            php.setSpeed(OssSpeedOrVelocity.genericSpeed(pathHistoryPoint.speed));
        }
        if (pathHistoryPoint.posAccuracy != null) {
            php.setPosAccuracy(OssPositionalAccuracy.genericPositionalAccuracy(pathHistoryPoint.posAccuracy));
        }
        if (pathHistoryPoint.heading != null) {
            php.setHeading(OssHeading.genericHeading(pathHistoryPoint.heading));
        }

        return php;
    }

}

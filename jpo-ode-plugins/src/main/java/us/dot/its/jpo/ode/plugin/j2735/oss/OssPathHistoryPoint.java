package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class OssPathHistoryPoint {

    public static J2735PathHistoryPoint genericPathHistoryPoint(PathHistoryPoint pathHistoryPoint) {

        J2735PathHistoryPoint php = new J2735PathHistoryPoint();

        // Required elements
        if (pathHistoryPoint.latOffset.longValue() == -131072) {
            php.setLatOffset(null);
        } else if (pathHistoryPoint.latOffset.longValue() < -131072) {
            php.setLatOffset(BigDecimal.valueOf(-0.0131071));
        } else if (pathHistoryPoint.latOffset.longValue() > 131071){
            php.setLatOffset(BigDecimal.valueOf(0.0131071));
        } else {
            php.setLatOffset(BigDecimal.valueOf(pathHistoryPoint.latOffset.longValue(), 7));
        }
        
        php.setLonOffset(BigDecimal.valueOf(pathHistoryPoint.lonOffset.longValue(), 7));

        if (pathHistoryPoint.elevationOffset.longValue() != 2048)
            php.setElevationOffset(BigDecimal.valueOf(pathHistoryPoint.elevationOffset.longValue(), 1));
        if (pathHistoryPoint.timeOffset.intValue() != 65535) {
            php.setTimeOffset(BigDecimal.valueOf(pathHistoryPoint.timeOffset.intValue(), 2));
        }

        // Optional elements
        if (pathHistoryPoint.posAccuracy != null) {
            php.setPosAccuracy(OssPositionalAccuracy.genericPositionalAccuracy(pathHistoryPoint.posAccuracy));
        }
        if (pathHistoryPoint.speed != null) {
            if (pathHistoryPoint.speed.intValue() != 8191) {
                // speed is received in units of 0.02 m/s
                php.setSpeed(BigDecimal.valueOf(pathHistoryPoint.speed.intValue() * 2, 2));
            }
        }
        if (pathHistoryPoint.heading != null) {
            php.setHeading(BigDecimal.valueOf(pathHistoryPoint.heading.longValue() * 15, 1));
        }

        return php;
    }

}

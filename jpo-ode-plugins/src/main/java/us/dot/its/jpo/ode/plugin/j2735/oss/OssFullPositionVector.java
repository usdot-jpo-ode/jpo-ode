package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735TimeConfidence;
import us.dot.its.jpo.ode.plugin.j2735.builders.Position3DBuilder;

public class OssFullPositionVector {
    
    private static final long LONG_LOWER_BOUND = -1799999999L;
    private static final long LONG_UPPER_BOUND = 1800000001L;
    private static final long LAT_LOWER_BOUND = -900000000;
    private static final long LAT_UPPER_BOUND = 900000001;
    private static final long ELEV_LOWER_BOUND = -4096;
    private static final long ELEV_UPPER_BOUND = 61439;
    private static final long TIME_CONF_LOWER_BOUND = 0;
    private static final long TIME_CONF_UPPER_BOUND = 39;
    
    private OssFullPositionVector() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735FullPositionVector genericFullPositionVector(FullPositionVector initialPosition) {
        
        // Bounds checks
        if (initialPosition._long.longValue() < LONG_LOWER_BOUND 
                || initialPosition._long.longValue() > LONG_UPPER_BOUND) {
            throw new IllegalArgumentException("Longitude value out of bounds [-1799999999..1800000001]");
        }
        
        if (initialPosition.lat.longValue() < LAT_LOWER_BOUND
                || initialPosition.lat.longValue() > LAT_UPPER_BOUND) {
            throw new IllegalArgumentException("Latitude value out of bounds [-900000000..900000001]");
        }
        
        if (initialPosition.elevation.longValue() < ELEV_LOWER_BOUND
                || initialPosition.elevation.longValue() > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("Elevation value out of bounds [-4096..61439]");
        }
        
        if (initialPosition.timeConfidence.longValue() < TIME_CONF_LOWER_BOUND
                || initialPosition.timeConfidence.longValue() > TIME_CONF_UPPER_BOUND) {
            throw new IllegalArgumentException("Time confidence value out of bounds [0..39]");
        }
        
        // Required elements
        J2735FullPositionVector fpv = new J2735FullPositionVector();
        
        fpv.setPosition(Position3DBuilder.odePosition3D(
              new DsrcPosition3D(
                    initialPosition.lat.longValue(), 
                    initialPosition._long.longValue(),
                    initialPosition.elevation.longValue())));
        
        // Optional elements
        if (initialPosition.heading != null) {
            fpv.setHeading(OssHeading.genericHeading(initialPosition.heading));
        }

        if (initialPosition.posAccuracy != null) {
            fpv.setPosAccuracy(OssPositionalAccuracy.genericPositionalAccuracy(initialPosition.posAccuracy));
        }

        if (initialPosition.posConfidence != null) {
            fpv.setPosConfidence(OssPositionConfidenceSet.genericPositionConfidenceSet(initialPosition.posConfidence));
        }

        if (initialPosition.speed != null) {
            fpv.setSpeed(OssTransmissionAndSpeed.genericTransmissionAndSpeed(initialPosition.speed));
        }

        if (initialPosition.speedConfidence != null) {
            fpv.setSpeedConfidence(OssSpeedandHeadingandThrottleConfidence
                    .genericSpeedandHeadingandThrottleConfidence(initialPosition.speedConfidence));
        }

        if (initialPosition.timeConfidence != null) {
            fpv.setTimeConfidence(J2735TimeConfidence.values()[initialPosition.timeConfidence.indexOf()]);
        }

        if (initialPosition.utcTime != null) {
            fpv.setUtcTime(OssDDateTime.genericDDateTime(initialPosition.utcTime));
        }

        return fpv;
    }

}

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TimeConfidence;

public class FullPositionVectorBuilder {
    
    private static final long LONG_LOWER_BOUND = -1799999999L;
    private static final long LONG_UPPER_BOUND = 1800000001L;
    private static final long LAT_LOWER_BOUND = -900000000;
    private static final long LAT_UPPER_BOUND = 900000001;
    private static final long ELEV_LOWER_BOUND = -4096;
    private static final long ELEV_UPPER_BOUND = 61439;
    private static final long TIME_CONF_LOWER_BOUND = 0;
    private static final long TIME_CONF_UPPER_BOUND = 39;
    
    private FullPositionVectorBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735FullPositionVector genericFullPositionVector(JsonNode initialPosition) {
       
       long longitude = initialPosition.get("long").asLong();
       long latitude = initialPosition.get("lat").asLong();
       
       Long elevation = null;
       if (initialPosition.get("elevation") != null) {
          elevation = initialPosition.get("elevation").asLong();
       }
       
       Long timeConfidence = null;
       if (initialPosition.get("timeConfidence") != null) {
          elevation = initialPosition.get("timeConfidence").asLong();
       }
       
       // TODO these bound checks should be lower down in the hierarchy
        // Bounds checks
        if (longitude < LONG_LOWER_BOUND 
                || longitude > LONG_UPPER_BOUND) {
            throw new IllegalArgumentException("Longitude value out of bounds [-1799999999..1800000001]");
        }
        
        if (latitude < LAT_LOWER_BOUND
                || latitude > LAT_UPPER_BOUND) {
            throw new IllegalArgumentException("Latitude value out of bounds [-900000000..900000001]");
        }
        
        if (elevation < ELEV_LOWER_BOUND
                || elevation > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("Elevation value out of bounds [-4096..61439]");
        }
        
        if (timeConfidence < TIME_CONF_LOWER_BOUND
                || timeConfidence > TIME_CONF_UPPER_BOUND) {
            throw new IllegalArgumentException("Time confidence value out of bounds [0..39]");
        }
        
        // Required elements
        J2735FullPositionVector fpv = new J2735FullPositionVector();
        
        fpv.setPosition(new J2735Position3D(
                latitude, 
                longitude,
                elevation));
        
        // Optional elements
        if (initialPosition.get("heading") != null) {
            fpv.setHeading(HeadingBuilder.genericHeading(initialPosition.get("heading")));
        }

        if (initialPosition.get("posAccuracy") != null) {
            fpv.setPosAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(initialPosition.get("posAccuracy")));
        }

        if (initialPosition.get("posConfidence") != null) {
            fpv.setPosConfidence(PositionConfidenceSetBuilder.genericPositionConfidenceSet(initialPosition.get("posConfidence")));
        }

        if (initialPosition.get("speed") != null) {
            fpv.setSpeed(TransmissionAndSpeedBuilder.genericTransmissionAndSpeed(initialPosition.get("speed")));
        }

        if (initialPosition.get("speedConfidence") != null) {
            fpv.setSpeedConfidence(SpeedandHeadingandThrottleConfidenceBuilder
                    .genericSpeedandHeadingandThrottleConfidence(initialPosition.get("speedConfidence")));
        }

        if (initialPosition.get("timeConfidence") != null) {
            fpv.setTimeConfidence(J2735TimeConfidence.valueOf(initialPosition.get("timeConfidence").asText().replaceAll("-", "_").toUpperCase()));
        }

        if (initialPosition.get("utcTime") != null) {
            fpv.setUtcTime(DDateTimeBuilder.genericDDateTime(initialPosition.get("utcTime")));
        }

        return fpv;
    }

}

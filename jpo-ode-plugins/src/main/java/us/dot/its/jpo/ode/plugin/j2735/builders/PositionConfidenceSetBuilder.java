package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735ElevationConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735PositionConfidence;

public class PositionConfidenceSetBuilder {
    
    private static final long POS_LOWER_BOUND = 0L;
    private static final long POS_UPPER_BOUND = 15L;
    private static final long ELEV_LOWER_BOUND = 0L;
    private static final long ELEV_UPPER_BOUND = 15L;
    
    private PositionConfidenceSetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735PositionConfidenceSet genericPositionConfidenceSet(JsonNode posConfidence) {
        
        if (posConfidence.get("pos").asLong() < POS_LOWER_BOUND || 
                posConfidence.get("pos").asLong() > POS_UPPER_BOUND) {
            throw new IllegalArgumentException("PositionConfidence value out of bounds");
        }

        if (posConfidence.get("elevation").asLong() < ELEV_LOWER_BOUND || 
                posConfidence.get("elevation").asLong() > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("ElevationConfidence value out of bounds");
        }

        J2735PositionConfidenceSet pc = new J2735PositionConfidenceSet();
        
        pc.setPos(J2735PositionConfidence.valueOf(posConfidence.get("pos").asText().replaceAll("-", "_").toUpperCase()));
        pc.setElevation(J2735ElevationConfidence.valueOf(posConfidence.get("elevation").asText().replaceAll("-", "_").toUpperCase()));
        
        return pc;
    }

}

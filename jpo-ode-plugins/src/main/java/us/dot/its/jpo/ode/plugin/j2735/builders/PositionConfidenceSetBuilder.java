package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735ElevationConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735PositionConfidence;

public class OssPositionConfidenceSet {
    
    private static final long POS_LOWER_BOUND = 0L;
    private static final long POS_UPPER_BOUND = 15L;
    private static final long ELEV_LOWER_BOUND = 0L;
    private static final long ELEV_UPPER_BOUND = 15L;
    
    private OssPositionConfidenceSet() {
       throw new UnsupportedOperationException();
    }

    public static J2735PositionConfidenceSet genericPositionConfidenceSet(PositionConfidenceSet posConfidence) {
        
        if (posConfidence.pos.longValue() < POS_LOWER_BOUND || 
                posConfidence.pos.longValue() > POS_UPPER_BOUND) {
            throw new IllegalArgumentException("PositionConfidence value out of bounds");
        }

        if (posConfidence.elevation.longValue() < ELEV_LOWER_BOUND || 
                posConfidence.elevation.longValue() > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("ElevationConfidence value out of bounds");
        }

        J2735PositionConfidenceSet pc = new J2735PositionConfidenceSet();
        
        pc.setPos(J2735PositionConfidence.values()[posConfidence.pos.indexOf()]);
        pc.setElevation(J2735ElevationConfidence.values()[posConfidence.elevation.indexOf()]);
        
        return pc;
    }

}

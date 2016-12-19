package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735ElevationConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735PositionConfidence;

public class OssPositionConfidenceSet {

	public static J2735PositionConfidenceSet genericPositionConfidenceSet(PositionConfidenceSet posConfidence) {
		
		J2735PositionConfidenceSet pc = new J2735PositionConfidenceSet();
		
		pc.pos = J2735PositionConfidence.values()[posConfidence.pos.indexOf()];
		pc.elevation = J2735ElevationConfidence.values()[posConfidence.elevation.indexOf()];
		
		return pc ;
	}

}

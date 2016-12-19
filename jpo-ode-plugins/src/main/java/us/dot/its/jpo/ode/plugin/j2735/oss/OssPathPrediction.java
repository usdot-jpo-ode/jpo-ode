package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PathPrediction;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathPrediction;

public class OssPathPrediction {

	public static J2735PathPrediction genericPathPrediction(PathPrediction pathPrediction) {
		J2735PathPrediction pp = new J2735PathPrediction();
		
		pp.confidence = BigDecimal.valueOf(pathPrediction.confidence.longValue() * 5, 1);
		pp.radiusOfCurve = BigDecimal.valueOf(pathPrediction.radiusOfCurve.longValue(), 1);
		return pp ;
	}

}

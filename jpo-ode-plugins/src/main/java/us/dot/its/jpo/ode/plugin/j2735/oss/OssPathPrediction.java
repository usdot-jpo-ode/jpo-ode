package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PathPrediction;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathPrediction;

public class OssPathPrediction {
    
    private OssPathPrediction() {}

    public static J2735PathPrediction genericPathPrediction(PathPrediction pathPrediction) {
        J2735PathPrediction pp = new J2735PathPrediction();

        if (pathPrediction.radiusOfCurve.longValue() >= 32767 
                || pathPrediction.radiusOfCurve.longValue() < -32767) {
            pp.radiusOfCurve = BigDecimal.ZERO.setScale(1);
        } else {
            pp.radiusOfCurve = BigDecimal.valueOf(pathPrediction.radiusOfCurve.longValue(), 1);
        }
        
        if (pathPrediction.confidence.longValue() < 0 || pathPrediction.confidence.longValue() > 200) {
            throw new IllegalArgumentException("Confidence value out of bounds [0..200]");
        } else {
            pp.confidence = BigDecimal.valueOf(pathPrediction.confidence.longValue() * 5, 1);
        }

        return pp;
    }

}

package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;

public class OssTimeOffset {

	public static BigDecimal genericTimeOffset(TimeOffset timeOffset) {
	    
	    if (timeOffset.intValue() < 1 || timeOffset.intValue() > 65535) {
	        throw new IllegalArgumentException("Time offset out of bounds");
	    }
		
		BigDecimal result = null;
		
		if (timeOffset.intValue() != 65535) {
			result = BigDecimal.valueOf(timeOffset.longValue(), 2);
		}
		
		return result;
	}

}

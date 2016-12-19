package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;

public class OssOffset {

	public static BigDecimal genericOffset(VertOffset_B07 offset) {
		
		BigDecimal result = null;
		
		if (offset.intValue() == -64) {
		    // -64 is undefined flag value: result = null
		} else if (offset.intValue() > 63) {
		    result = BigDecimal.valueOf(6.3);
		} else if (offset.intValue() < -64) {
		    result = BigDecimal.valueOf(-6.3);
		} else {
		    result = BigDecimal.valueOf(offset.longValue(), 1);
		}

		return result;
		
	}

	public static BigDecimal genericOffset(Offset_B12 offset) {
	    
	    if (offset.intValue() < -2048 || offset.intValue() > 2047) {
	        throw new IllegalArgumentException("Offset-B12 out of bounds");
	    }
		
		BigDecimal result = null;
		
		if (offset.intValue() != -2048) {
			result = BigDecimal.valueOf(offset.longValue(), 2);
		}

		return result;
		
	}

}

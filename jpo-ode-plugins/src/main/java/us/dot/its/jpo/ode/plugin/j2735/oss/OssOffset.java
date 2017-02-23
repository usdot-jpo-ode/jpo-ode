package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B09;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;

public class OssOffset {
    
    private static final Integer OFF_B12_LOWER_BOUND = -2048;
    private static final Integer OFF_B12_UPPER_BOUND = 2047;
    private static final Integer OFF_B09_LOWER_BOUND = -256;
    private static final Integer OFF_B09_UPPER_BOUND = 255;
    private static final Integer OFF_B10_LOWER_BOUND = -512;
    private static final Integer OFF_B10_UPPER_BOUND = 511;

	public static BigDecimal genericOffset(VertOffset_B07 offset) {
		
		BigDecimal result;
		
		if (offset.intValue() == -64) {
		    result = null;
		} else if (offset.intValue() >= 63) {
		    result = BigDecimal.valueOf(6.3);
		} else if (offset.intValue() < -64) {
		    result = BigDecimal.valueOf(-6.3);
		} else {
		    result = BigDecimal.valueOf(offset.longValue(), 1);
		}

		return result;
		
	}

	public static BigDecimal genericOffset(Offset_B12 offset) {
	    
	    if (offset.intValue() < OFF_B12_LOWER_BOUND || offset.intValue() > OFF_B12_UPPER_BOUND) {
	        throw new IllegalArgumentException("Offset-B12 out of bounds [-2048..2047]");
	    }
		
		BigDecimal result = null;
		
		if (offset.intValue() != -2048) {
			result = BigDecimal.valueOf(offset.longValue(), 2);
		}

		return result;
		
	}
	
	public static BigDecimal genericOffset(Offset_B09 offset) {
	    
	    if (offset.intValue() < OFF_B09_LOWER_BOUND || offset.intValue() > OFF_B09_UPPER_BOUND) {
	        throw new IllegalArgumentException("Offset-B09 out of bounds [-256..255]");
	    }
	    
	    BigDecimal result = null;
	    
	    if(offset.intValue() != -256) {
	        result = BigDecimal.valueOf(offset.longValue(), 2);
	    }
	    
	    return result;
	}
	
	public static BigDecimal genericOffset(Offset_B10 offset) {
	    
	    if (offset.intValue() < OFF_B10_LOWER_BOUND || offset.intValue() > OFF_B10_UPPER_BOUND) {
	        throw new IllegalArgumentException("Offset-B10 out of bounds [-512..511]");
	    }
	    
	    BigDecimal result = null;
	    
	    if(offset.intValue() != -512) {
	        result = BigDecimal.valueOf(offset.longValue(), 2);
	    }
	    
	    return result;
	    
	}

}

package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;

public class OssHeight {

	public static BigDecimal genericHeight(VehicleHeight height) {
	    
	    if (height.intValue() < 0 || height.intValue() > 127) {
	        throw new IllegalArgumentException("Vehicle height out of bounds");
	    }
	    
		return BigDecimal.valueOf(height.intValue() * 5, 2);
	}

}

package us.dot.its.jpo.ode.plugins.oss.j2735;

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

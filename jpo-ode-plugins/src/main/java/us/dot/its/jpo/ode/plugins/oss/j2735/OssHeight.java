package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;

public class OssHeight {

	public static BigDecimal genericHeight(VehicleHeight height) {
		return BigDecimal.valueOf(height.intValue() * 5, 2);
	}

}

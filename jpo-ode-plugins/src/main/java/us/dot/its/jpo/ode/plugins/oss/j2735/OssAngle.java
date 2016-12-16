package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;

public class OssAngle {

	public static BigDecimal genericAngle(Angle angle) {
		return longToDecimal(angle.longValue());
	}

	public static BigDecimal longToDecimal(long longValue) {
		return BigDecimal.valueOf(longValue * 125, 4);
	}

	
}

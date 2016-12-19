package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.PositionalAccuracy;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;

public class OssPositionalAccuracy {
	public static J2735PositionalAccuracy genericPositionalAccuracy(
			PositionalAccuracy positionalAccuracy) {
		J2735PositionalAccuracy genericPositionalAccuracy = 
				new J2735PositionalAccuracy();

		if (positionalAccuracy.semiMajor.intValue() != 255) {
			genericPositionalAccuracy.semiMajor = 
					BigDecimal.valueOf(
							positionalAccuracy.semiMajor.intValue() * 5, 2);
		}
		
		if (positionalAccuracy.semiMinor.intValue() != 255) {
			genericPositionalAccuracy.semiMinor = 
					BigDecimal.valueOf(
							positionalAccuracy.semiMinor.intValue() * 5, 2);
		}
		
		if (positionalAccuracy.orientation.intValue() != 65535) {
			genericPositionalAccuracy.orientation = 
					BigDecimal.valueOf(
							(double)positionalAccuracy.orientation.longValue() / 65535.0);
		}
		return genericPositionalAccuracy;
		
	}


}

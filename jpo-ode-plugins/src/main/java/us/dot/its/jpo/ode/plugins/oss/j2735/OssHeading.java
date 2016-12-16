package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.CoarseHeading;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;

public class OssHeading {

	public static BigDecimal genericHeading(Heading heading) {
		return OssAngle.longToDecimal(heading.longValue());
	}

	public static BigDecimal genericHeading(CoarseHeading heading) {
		return BigDecimal.valueOf(heading.longValue() * 15, 1);
	}

}

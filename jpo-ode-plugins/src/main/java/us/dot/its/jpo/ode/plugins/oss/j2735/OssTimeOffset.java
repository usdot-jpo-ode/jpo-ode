package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;

public class OssTimeOffset {

	public static BigDecimal genericTimeOffset(TimeOffset timeOffset) {
		return BigDecimal.valueOf(timeOffset.longValue(), 2);
	}

}
